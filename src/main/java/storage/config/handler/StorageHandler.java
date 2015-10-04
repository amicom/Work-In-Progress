/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.storage.config
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package storage.config.handler;


import events.ConfigEvent;
import events.ConfigEventSender;
import scheduler.DelayedRunnable;
import storage.*;
import storage.config.ConfigInterface;
import storage.config.InterfaceParseException;
import storage.config.annotations.*;
import utils.Application;
import utils.Files;
import utils.StringUtils;
import utils.exceptions.WTFException;
import utils.logging.Log;
import utils.reflection.Clazz;
import utils.shutdown.ShutdownController;
import utils.shutdown.ShutdownEvent;
import utils.shutdown.ShutdownRequest;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * @param <T>
 * @author thomas
 */
public class StorageHandler<T extends ConfigInterface> implements InvocationHandler {

    private static final HashMap<StorageHandler<?>, String> STORAGEMAP = new HashMap<StorageHandler<?>, String>();
    // set externaly to start profiling
    public static HashMap<String, Long> PROFILER_MAP = null;
    public static HashMap<String, Long> PROFILER_CALLNUM_MAP = null;

    static {
        ShutdownController.getInstance().addShutdownEvent(new ShutdownEvent() {

            @Override
            public long getMaxDuration() {
                return 0;
            }

            @Override
            public int getHookPriority() {
                return 0;
            }

            @Override
            public void onShutdown(final ShutdownRequest shutdownRequest) {
                StorageHandler.saveAll();
            }

            @Override
            public String toString() {
                return "ShutdownEvent: SaveAllStorageHandler";
            }
        });
    }

    protected final HashMap<Method, KeyHandler<?>> methodMap = new HashMap<Method, KeyHandler<?>>();
    protected final Storage primitiveStorage;
    private final Class<T> configInterface;
    private final File path;
    private final String storageID;
    boolean saveInShutdownHookEnabled = true;
    protected static final DelayedRunnable SAVEDELAYER = new DelayedRunnable(5000, 30000) {

        @Override
        public void delayedrun() {
            StorageHandler.saveAll();
        }
    };
    private ConfigEventSender<Object> eventSender = null;
    private String relativCPPath;
    private volatile WriteStrategy writeStrategy = null;
    private boolean objectCacheEnabled = true;

    public StorageHandler(final File filePath, final Class<T> configInterface) {
        final InitHook initHook = configInterface.getAnnotation(InitHook.class);
        if (initHook != null) {
            try {
                initHook.value().newInstance().doHook(filePath, configInterface);
            } catch (final Exception e) {
                throw new WTFException(e);
            }
        }
        this.configInterface = configInterface;
        this.path = filePath;
        if (filePath.getName().endsWith(".json") || filePath.getName().endsWith(".ejs")) {
            Log.L.warning(filePath + " should not have an extension!!");
        }
        final File expected = Application.getResource("cfg/" + configInterface.getName());
        String storageID = null;
        if (!this.path.equals(expected)) {
            storageID = Files.getRelativePath(expected.getParentFile().getParentFile(), this.path);
            if (StringUtils.isEmpty(storageID)) {
                storageID = this.path.getAbsolutePath();
            }
        }
        this.storageID = storageID;
        this.primitiveStorage = StorageHandler.createPrimitiveStorage(this.path, null, configInterface, StorageHandler.SAVEDELAYER);
        final CryptedStorage cryptedStorage = configInterface.getAnnotation(CryptedStorage.class);
        if (cryptedStorage != null) {
            this.validateKeys(cryptedStorage);
        }
        try {
            Log.L.finer("Init StorageHandler for Interface:" + configInterface.getName() + "|Path:" + this.path);
            this.parseInterface();
        } catch (final InterfaceParseException e) {
            throw e;
        } catch (final Throwable e) {
            throw new InterfaceParseException(e);
        }
        this.addStorageHandler(this, configInterface.getName(), storageID);
    }

    public StorageHandler(final Storage storage, final Class<T> configInterface) {
        final InitHook initHook = configInterface.getAnnotation(InitHook.class);
        String storagePath = storage.getID();
        if (storagePath.endsWith(".json")) {
            storagePath = storagePath.replaceFirst("\\.json$", "");
        } else if (storagePath.endsWith(".ejs")) {
            storagePath = storagePath.replaceFirst("\\.ejs$", "");
        }
        this.primitiveStorage = storage;
        this.path = new File(storagePath);
        if (initHook != null) {
            try {
                initHook.value().newInstance().doHook(this.path, configInterface);
            } catch (final Exception e) {
                throw new WTFException(e);
            }
        }
        this.configInterface = configInterface;
        final File expected = Application.getResource("cfg/" + configInterface.getName());
        String storageID = null;
        if (!this.path.equals(expected)) {
            storageID = Files.getRelativePath(expected.getParentFile().getParentFile(), this.path);
            if (StringUtils.isEmpty(storageID)) {
                storageID = this.path.getAbsolutePath();
            }
        }
        this.storageID = storageID;
        final CryptedStorage cryptedStorage = configInterface.getAnnotation(CryptedStorage.class);
        if (cryptedStorage != null) {
            this.validateKeys(cryptedStorage);
        }
        try {
            Log.L.finer("Init StorageHandler for Interface:" + configInterface.getName() + "|Path:" + this.path);
            this.parseInterface();
        } catch (final Throwable e) {
            throw new InterfaceParseException(e);
        }
        this.addStorageHandler(this, configInterface.getName(), storageID);
    }

    public StorageHandler(final String classPath, final Class<T> configInterface) throws URISyntaxException {
        final InitHook initHook = configInterface.getAnnotation(InitHook.class);
        if (initHook != null) {
            try {
                initHook.value().newInstance().doHook(classPath, configInterface);
            } catch (final Exception e) {
                throw new WTFException(e);
            }
        }
        this.configInterface = configInterface;
        this.relativCPPath = classPath;
        if (classPath.endsWith(".json") || classPath.endsWith(".ejs")) {
            Log.L.warning(classPath + " should not have an extension!!");
        }
        this.path = Application.getResource(classPath);
        final File expected = Application.getResource("cfg/" + configInterface.getName());
        String storageID = null;
        if (!this.path.equals(expected)) {
            storageID = Files.getRelativePath(expected.getParentFile().getParentFile(), this.path);
            if (StringUtils.isEmpty(storageID)) {
                storageID = this.path.getAbsolutePath();
            }
        }
        this.storageID = storageID;
        this.primitiveStorage = StorageHandler.createPrimitiveStorage(Application.getResource(classPath), classPath, configInterface, StorageHandler.SAVEDELAYER);
        final CryptedStorage cryptedStorage = configInterface.getAnnotation(CryptedStorage.class);
        if (cryptedStorage != null) {
            this.validateKeys(cryptedStorage);
        }
        try {
            Log.L.finer("Init StorageHandler for Interface:" + configInterface.getName() + "|Path:" + this.path);
            this.parseInterface();
        } catch (final Throwable e) {
            throw new InterfaceParseException(e);
        }
        this.addStorageHandler(this, configInterface.getName(), storageID);
    }

    public static JsonKeyValueStorage createPrimitiveStorage(final File filePath, final String classPath, final Class<? extends ConfigInterface> configInterface, final Runnable saveCallback) {
        final CryptedStorage crypted = configInterface.getAnnotation(CryptedStorage.class);
        JsonKeyValueStorage ret = null;
        if (crypted != null) {
            byte[] key = JSonStorage.KEY;
            if (crypted.key() != null) {
                key = crypted.key();
            }
            URL urlClassPath = null;
            if (classPath != null) {
                urlClassPath = Application.class.getClassLoader().getResource(classPath + ".ejs");
            }
            ret = new JsonKeyValueStorage(new File(filePath.getAbsolutePath() + ".ejs"), urlClassPath, false, key) {
                @Override
                public void requestSave() {
                    super.requestSave();
                    if (saveCallback != null) {
                        saveCallback.run();
                    }
                }
            };
        } else {
            URL urlClassPath = null;
            if (classPath != null) {
                urlClassPath = Application.class.getClassLoader().getResource(classPath + ".json");
            }
            ret = new JsonKeyValueStorage(new File(filePath.getAbsolutePath() + ".json"), urlClassPath, true, null) {
                @Override
                public void requestSave() {
                    super.requestSave();
                    if (saveCallback != null) {
                        saveCallback.run();
                    }
                }
            };
        }
        return ret;
    }

    /**
     * @param interfaceName
     * @param storage
     * @return
     */
    public static StorageHandler<?> getStorageHandler(final String interfaceName, final String storage) {
        synchronized (StorageHandler.STORAGEMAP) {
            final String ID = interfaceName + "." + storage;
            final Iterator<Entry<StorageHandler<?>, String>> it = StorageHandler.STORAGEMAP.entrySet().iterator();
            StorageHandler<?> ret = null;
            while (it.hasNext()) {
                final Entry<StorageHandler<?>, String> next = it.next();
                if (ID.equals(next.getValue()) && (ret = next.getKey()) != null) {
                    return ret;
                }
            }
        }
        return null;
    }

    public static void saveAll() {
        synchronized (StorageHandler.STORAGEMAP) {
            for (final StorageHandler<?> storageHandler : StorageHandler.STORAGEMAP.keySet()) {
                try {
                    if (storageHandler.isSaveInShutdownHookEnabled()) {
                        storageHandler.getPrimitiveStorage().save();
                    }
                } catch (final Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void addStorageHandler(final StorageHandler<? extends ConfigInterface> storageHandler, final String interfaceName, final String storage) {
        synchronized (StorageHandler.STORAGEMAP) {
            final StorageHandler<?> existing = StorageHandler.getStorageHandler(interfaceName, storage);
            if (existing != null && existing != storageHandler) {
                throw new IllegalStateException("You cannot init the configinterface " + this.configInterface + " twice");
            }
            final String ID = interfaceName + "." + storage;
            StorageHandler.STORAGEMAP.put(storageHandler, ID);
        }
    }

    protected KeyHandler<?> createKeyHandler(final String key, final Type type) {
        if (Clazz.isBoolean(type)) {
            return new BooleanKeyHandler(this, key);
        } else if (Clazz.isByte(type)) {
            return new ByteKeyHandler(this, key);
        } else if (Clazz.isDouble(type)) {
            return new DoubleKeyHandler(this, key);
        } else if (Clazz.isFloat(type)) {
            return new FloatKeyHandler(this, key);

        } else if (Clazz.isInteger(type)) {
            return new IntegerKeyHandler(this, key);
        } else if (type instanceof Class && ((Class<?>) type).isEnum()) {
            return new EnumKeyHandler(this, key);
        } else if (type == String.class) {
            return new StringKeyHandler(this, key);
        } else if (Clazz.isLong(type)) {
            return new LongKeyHandler(this, key);

        } else if (type instanceof Class && ((Class<?>) type).isArray()) {

            final Class<?> ct = ((Class<?>) type).getComponentType();
            final boolean p = ct.isPrimitive();
            if (Clazz.isBoolean(ct)) {

                if (p) {
                    return new ListHandler<boolean[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultBooleanArrayValue.class;
                        }

                    };
                } else {
                    return new ListHandler<Boolean[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultBooleanArrayValue.class;
                        }

                    };
                }

            } else if (Clazz.isLong(ct)) {
                if (p) {
                    return new ListHandler<long[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultLongArrayValue.class;
                        }

                    };
                } else {
                    return new ListHandler<Long[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultLongArrayValue.class;
                        }

                    };
                }

            } else if (Clazz.isInteger(ct)) {
                if (p) {
                    return new ListHandler<int[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultIntArrayValue.class;
                        }

                    };
                } else {
                    return new ListHandler<Integer[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultIntArrayValue.class;
                        }

                    };
                }
            } else if (Clazz.isByte(ct)) {
                if (p) {
                    return new ListHandler<byte[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultByteArrayValue.class;
                        }

                    };
                } else {
                    return new ListHandler<Byte[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultByteArrayValue.class;
                        }

                    };
                }
            } else if (Clazz.isFloat(ct)) {

                if (p) {
                    return new ListHandler<float[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultFloatArrayValue.class;
                        }

                    };
                } else {
                    return new ListHandler<Float[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultFloatArrayValue.class;
                        }

                    };
                }
            } else if (ct == String.class) {
                return new StringListHandler(this, key, type);
            } else if (ct.isEnum()) {
                return new EnumListHandler(this, key, type);

            } else if (Clazz.isDouble(ct)) {
                if (p) {
                    return new ListHandler<double[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultDoubleArrayValue.class;
                        }

                    };
                } else {
                    return new ListHandler<Double[]>(this, key, type) {
                        @Override
                        protected Class<? extends Annotation> getDefaultAnnotation() {

                            return DefaultDoubleArrayValue.class;
                        }

                    };
                }
            } else {

                return new ObjectKeyHandler(this, key, type);
            }
        } else {

            return new ObjectKeyHandler(this, key, type);
        }
    }

    /**
     * @param e
     */
    protected void error(final Throwable e) {
        new Thread("ERROR THROWER") {
            @Override
            public void run() {
                //TODO: ZACK - fix this line
//                Dialog.getInstance().showExceptionDialog(e.getClass().getSimpleName(), e.getMessage(), e);
            }
        }.start();
        // we could throw the exception here, but this would kill the whole
        // interface. So we just show a a dialog for the developer and let the
        // rest of the interface work.
    }

    protected void fireEvent(final ConfigEvent.Types type, final KeyHandler<?> keyHandler, final Object parameter) {
        if (this.hasEventListener()) {
            this.getEventSender().fireEvent(new ConfigEvent(type, keyHandler, parameter));
        }
    }

    public Class<T> getConfigInterface() {
        return this.configInterface;
    }

    public synchronized ConfigEventSender<Object> getEventSender() {
        if (this.eventSender == null) {
            this.eventSender = new ConfigEventSender<Object>();
        }
        return this.eventSender;
    }

    @SuppressWarnings("unchecked")
    public KeyHandler<Object> getKeyHandler(final String key) {
        return this.getKeyHandler(key, KeyHandler.class);

    }

    @SuppressWarnings("unchecked")
    public <E extends KeyHandler<?>> E getKeyHandler(final String key, final Class<E> class1) {
        final String keyHandlerKey = key.toLowerCase(Locale.ENGLISH);
        for (KeyHandler<?> keyHandler : methodMap.values()) {
            if (keyHandlerKey.equals(keyHandler.getKey())) {
                return (E) keyHandler;
            }
        }
        throw new NullPointerException("No KeyHandler: " + key + " in " + this.configInterface);
    }

    public HashMap<Method, KeyHandler<?>> getMap() {
        return this.methodMap;
    }

    /**
     * @return
     */
    public File getPath() {
        return this.path;
    }

    /**
     * @param keyHandler
     * @return
     */
    public Object getPrimitive(final KeyHandler<?> keyHandler) {
        // only evaluate defaults of required
        if (this.primitiveStorage.hasProperty(keyHandler.getKey())) {
            if (Clazz.isBoolean(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), false);
            } else if (Clazz.isLong(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), 0l);
            } else if (Clazz.isInteger(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), 0);
            } else if (Clazz.isFloat(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), 0.0f);
            } else if (Clazz.isByte(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), (byte) 0);
            } else if (keyHandler.getRawClass() == String.class) {
                return this.getPrimitive(keyHandler.getKey(), (String) null);
                // } else if (getter.getRawClass() == String[].class) {
                // return this.get(getter.getKey(),
                // getter.getDefaultStringArray());
            } else if (keyHandler.getRawClass().isEnum()) {

                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
            } else if (keyHandler.getRawClass() == Double.class | keyHandler.getRawClass() == double.class) {
                return this.getPrimitive(keyHandler.getKey(), 0.0d);
            } else {
                throw new StorageException("Invalid datatype: " + keyHandler.getRawClass());
            }
        } else {
            if (Clazz.isBoolean(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
            } else if (Clazz.isLong(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
            } else if (Clazz.isInteger(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
            } else if (Clazz.isFloat(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
            } else if (Clazz.isByte(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
            } else if (keyHandler.getRawClass() == String.class) {
                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
                // } else if (getter.getRawClass() == String[].class) {
                // return this.get(getter.getKey(),
                // getter.getDefaultStringArray());
            } else if (keyHandler.getRawClass().isEnum()) {
                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
            } else if (Clazz.isDouble(keyHandler.getRawClass())) {
                return this.getPrimitive(keyHandler.getKey(), keyHandler.getDefaultValue());
            } else {
                throw new StorageException("Invalid datatype: " + keyHandler.getRawClass());
            }
        }
    }

    public <E> E getPrimitive(final String key, final E def) {
        return this.primitiveStorage.get(key, def);
    }

    public Storage getPrimitiveStorage() {
        return this.primitiveStorage;
    }

    public String getRelativCPPath() {
        return this.relativCPPath;
    }

    public String getStorageID() {
        return this.storageID;
    }

    public Object getValue(final String key) {
        return this.getKeyHandler(key).getValue();
    }

    public WriteStrategy getWriteStrategy() {
        return this.writeStrategy;
    }

    public void setWriteStrategy(final WriteStrategy writeStrategy) {
        this.writeStrategy = writeStrategy;
    }

    public synchronized boolean hasEventListener() {
        return this.eventSender != null && this.eventSender.hasListener();
    }

    @SuppressWarnings("unchecked")
    public Object invoke(final Object instance, final Method m, final Object[] parameter) throws Throwable {
        if (m != null) {
            final long t = StorageHandler.PROFILER_MAP == null ? 0 : System.nanoTime();
            try {
                final KeyHandler<?> handler = this.methodMap.get(m);
                if (handler != null) {
                    if (handler.isGetter(m)) {
                        return handler.getValue();
                    } else {
                        ((KeyHandler<Object>) handler).setValue(parameter[0]);
                        if (this.writeStrategy != null) {
                            this.writeStrategy.write(this, handler);
                        }
                        return null;
                    }
                } else if (m.getName().equals("toString")) {
                    return this.toString();
                    // } else if (m.getName().equals("addListener")) {
                    // this.eventSender.addListener((ConfigEventListener)
                    // parameter[0]);
                    // return null;
                    // } else if (m.getName().equals("removeListener")) {
                    // this.eventSender.removeListener((ConfigEventListener)
                    // parameter[0]);
                    // return null;
                } else if (m.getName().equals("_getStorageHandler")) {
                    return this;

                } else {
                    throw new WTFException(m + " ??? no keyhandler. This is not possible!");
                }
            } finally {
                if (StorageHandler.PROFILER_MAP != null && m != null) {
                    final long dur = System.nanoTime() - t;
                    final String id = m.toString();
                    Long g = StorageHandler.PROFILER_MAP.get(id);
                    if (g == null) {
                        g = 0l;
                    }
                    StorageHandler.PROFILER_MAP.put(id, g + dur);
                }
                if (StorageHandler.PROFILER_CALLNUM_MAP != null && m != null) {
                    final String id = m.toString();
                    final Long g = StorageHandler.PROFILER_CALLNUM_MAP.get(id);
                    StorageHandler.PROFILER_CALLNUM_MAP.put(id, g == null ? 1 : g + 1);
                }

            }
        } else {
            // yes.... Method m may be null. this happens if we call a
            // method in the interface's own static init.
            return this;
        }
    }

    /**
     * @return
     */
    public boolean isObjectCacheEnabled() {
        return this.objectCacheEnabled;
    }

    public void setObjectCacheEnabled(final boolean objectCacheEnabled) {
        this.objectCacheEnabled = objectCacheEnabled;
    }

    public boolean isSaveInShutdownHookEnabled() {
        return this.saveInShutdownHookEnabled;
    }

    public void setSaveInShutdownHookEnabled(final boolean saveInShutdownHookEnabled) {
        this.saveInShutdownHookEnabled = saveInShutdownHookEnabled;
    }

    /**
     * @throws Throwable
     */
    protected void parseInterface() throws Throwable {
        final HashMap<String, Method> keyGetterMap = new HashMap<String, Method>();
        final HashMap<String, Method> keySetterMap = new HashMap<String, Method>();
        String key;
        final HashMap<String, KeyHandler<?>> parseMap = new HashMap<String, KeyHandler<?>>();
        Class<?> clazz = this.configInterface;
        while (clazz != null && clazz != ConfigInterface.class) {
            for (final Method m : clazz.getDeclaredMethods()) {
                final String methodName = m.getName().toLowerCase(Locale.ENGLISH);
                if (methodName.startsWith("get")) {
                    key = methodName.substring(3);
                    // we do not allow to setters/getters with the same name but
                    // different cases. this only confuses the user when editing
                    // the
                    // later config file
                    if (keyGetterMap.containsKey(key)) {
                        this.error(new InterfaceParseException("Key " + key + " Dupe found! " + keyGetterMap.get(key) + "<-->" + m));
                        continue;
                    }
                    keyGetterMap.put(key, m);
                    if (m.getParameterTypes().length > 0) {
                        this.error(new InterfaceParseException("Getter " + m + " has parameters."));
                        keyGetterMap.remove(key);
                        continue;
                    }
                    try {
                        final AllowStorage allow = m.getAnnotation(AllowStorage.class);
                        boolean found = false;
                        if (allow != null) {
                            for (final Class<?> c : allow.value()) {
                                if (m.getReturnType() == c) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            JSonStorage.canStore(m.getGenericReturnType(), false);
                        }
                    } catch (final InvalidTypeException e) {
                        final AllowStorage allow = m.getAnnotation(AllowStorage.class);
                        boolean found = false;
                        if (allow != null) {
                            for (final Class<?> c : allow.value()) {
                                if (e.getType() == c) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            this.error(new InterfaceParseException(e));
                            keyGetterMap.remove(key);
                            continue;
                        }
                    }
                    KeyHandler<?> kh = parseMap.get(key);
                    if (kh == null) {
                        kh = this.createKeyHandler(key, m.getGenericReturnType());
                        parseMap.put(key, kh);
                    }
                    kh.setGetMethod(m);
                    this.methodMap.put(m, kh);
                } else if (methodName.startsWith("is")) {
                    key = methodName.substring(2);
                    // we do not allow to setters/getters with the same name but
                    // different cases. this only confuses the user when editing
                    // the
                    // later config file
                    if (keyGetterMap.containsKey(key)) {
                        this.error(new InterfaceParseException("Key " + key + " Dupe found! " + keyGetterMap.get(key) + "<-->" + m));
                        continue;
                    }
                    keyGetterMap.put(key, m);
                    if (m.getParameterTypes().length > 0) {
                        this.error(new InterfaceParseException("Getter " + m + " has parameters."));
                        keyGetterMap.remove(key);
                        continue;
                    }
                    try {
                        JSonStorage.canStore(m.getGenericReturnType(), false);
                    } catch (final InvalidTypeException e) {
                        this.error(new InterfaceParseException(e));
                        keyGetterMap.remove(key);
                        continue;
                    }
                    KeyHandler<?> kh = parseMap.get(key);
                    if (kh == null) {
                        kh = this.createKeyHandler(key, m.getGenericReturnType());
                        parseMap.put(key, kh);
                    }
                    kh.setGetMethod(m);
                    this.methodMap.put(m, kh);
                } else if (methodName.startsWith("set")) {
                    key = methodName.substring(3);
                    if (keySetterMap.containsKey(key)) {
                        this.error(new InterfaceParseException("Key " + key + " Dupe found! " + keySetterMap.get(key) + "<-->" + m));
                        continue;
                    }
                    keySetterMap.put(key, m);
                    if (m.getParameterTypes().length != 1) {
                        this.error(new InterfaceParseException("Setter " + m + " has !=1 parameters."));
                        keySetterMap.remove(key);
                        continue;

                    }
                    if (m.getReturnType() != void.class) {
                        this.error(new InterfaceParseException("Setter " + m + " has a returntype != void"));
                        keySetterMap.remove(key);
                        continue;
                    }
                    try {
                        JSonStorage.canStore(m.getGenericParameterTypes()[0], false);
                    } catch (final InvalidTypeException e) {
                        final AllowStorage allow = m.getAnnotation(AllowStorage.class);
                        boolean found = false;
                        if (allow != null) {
                            for (final Class<?> c : allow.value()) {
                                if (e.getType() == c) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            this.error(new InterfaceParseException(e));
                            keySetterMap.remove(key);
                            continue;
                        }
                    }
                    KeyHandler<?> kh = parseMap.get(key);
                    if (kh == null) {
                        kh = this.createKeyHandler(key, m.getGenericParameterTypes()[0]);
                        parseMap.put(key, kh);
                    }
                    kh.setSetMethod(m);
                    this.methodMap.put(m, kh);
                } else {
                    this.error(new InterfaceParseException("Only getter and setter allowed:" + m));
                    continue;
                }
            }
            // run down the calss hirarchy to find all methods. getMethods does
            // not work, because it only finds public methods
            final Class<?>[] interfaces = clazz.getInterfaces();
            clazz = interfaces[0];
        }
        final ArrayList<Method> methodsToRemove = new ArrayList<Method>();
        for (final KeyHandler<?> kh : this.methodMap.values()) {
            try {
                kh.init();
            } catch (final Throwable e) {
                this.error(e);
                methodsToRemove.add(kh.getGetMethod());
                methodsToRemove.add(kh.getSetMethod());
            }
        }
        for (final Method m : methodsToRemove) {
            this.methodMap.remove(m);
        }
    }

    @Override
    public String toString() {
        final HashMap<String, Object> ret = new HashMap<String, Object>();
        for (final KeyHandler<?> h : this.methodMap.values()) {
            try {
                ret.put(h.getKey(), this.invoke(null, h.getGetMethod(), new Object[]{}));
            } catch (final Throwable e) {
                e.printStackTrace();
                ret.put(h.getKey(), e.getMessage());
            }
        }
        return JSonStorage.toString(ret);
    }

    protected void validateKeys(final CryptedStorage crypted) {
    }

    public void write() {
        this.getPrimitiveStorage().save();
    }

    /**
     * @param key2
     */

}
