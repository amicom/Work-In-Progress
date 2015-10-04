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

import storage.JSonStorage;
import storage.TypeRef;
import storage.config.annotations.DefaultEnumValue;
import storage.config.annotations.DefaultFactory;
import storage.config.annotations.DefaultJsonObject;

import java.lang.annotation.Annotation;

/**
 * @author Thomas
 *
 */
public class EnumKeyHandler extends KeyHandler<Enum> {

    /**
     * @param storageHandler
     * @param key
     */
    public EnumKeyHandler(final StorageHandler<?> storageHandler, final String key) {
        super(storageHandler, key);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Class<? extends Annotation> getDefaultAnnotation() {

        return DefaultEnumValue.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Enum getDefaultValue() {
        return this.defaultValue;

    }

    @Override
    protected void initDefaults() throws Throwable {
        this.setDefaultValue(this.getRawClass().getEnumConstants()[0]);
        final DefaultFactory df = this.getAnnotation(DefaultFactory.class);
        if (df != null) {
            this.setDefaultValue((Enum) df.value().newInstance().getDefaultValue());
        }
        final DefaultJsonObject defaultJson = this.getAnnotation(DefaultJsonObject.class);
        if (defaultJson != null) {
            this.setDefaultValue(JSonStorage.restoreFromString(defaultJson.value(), new TypeRef<Enum>(this.getRawClass()) {
            }, null));
        }
        final DefaultEnumValue ann = this.getAnnotation(DefaultEnumValue.class);
        if (ann != null) {
            this.setDefaultValue(Enum.valueOf(this.getRawClass(), ann.value()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.storage.config.KeyHandler#initHandler()
     */
    @Override
    protected void initHandler() throws Throwable {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.storage.config.KeyHandler#putValue(java.lang.Object)
     */
    @Override
    protected void putValue(final Enum object) {
        this.storageHandler.getPrimitiveStorage().put(this.getKey(), object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.appwork.storage.config.KeyHandler#validateValue(java.lang.Object)
     */
    @Override
    protected void validateValue(final Enum object) throws Throwable {
        // TODO Auto-generated method stub

    }

}
