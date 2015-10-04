/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.remoteapi
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package remoteapi;


import http.HTTPConstants;
import remoteapi.annotations.*;
import storage.InvalidTypeException;
import storage.JSonStorage;
import storage.config.annotations.AllowStorage;
import utils.logging.Log;
import utils.net.HTTPHeader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author thomas
 *
 */
public class InterfaceHandler<T> {
    private static Method HELP;

    static {
        try {
            InterfaceHandler.HELP = InterfaceHandler.class.getMethod("help", RemoteAPIRequest.class, RemoteAPIResponse.class);
        } catch (final SecurityException e) {
            Log.exception(e);
        } catch (final NoSuchMethodException e) {
            Log.exception(e);
        }

    }

    private final RemoteAPIInterface impl;
    private final List<Class<T>> interfaceClasses;
    private final HashMap<Method, Integer> parameterCountMap;
    private final HashMap<Method, Integer> methodsAuthLevel;
    private final HashMap<String, Method> methods;
    private final HashSet<Method> signatureRequiredMethods;
    private final int defaultAuthLevel;
    private Method signatureHandler = null;
    private boolean sessionRequired = false;
    private SoftReference<byte[]> helpBytes = new SoftReference<byte[]>(null);
    private SoftReference<byte[]> helpBytesJson = new SoftReference<byte[]>(null);

    private InterfaceHandler(final Class<T> c, final RemoteAPIInterface x, final int defaultAuthLevel) throws SecurityException, NoSuchMethodException {
        this.interfaceClasses = new ArrayList<Class<T>>();
        this.interfaceClasses.add(c);
        this.impl = x;
        this.defaultAuthLevel = defaultAuthLevel;
        this.methodsAuthLevel = new HashMap<Method, Integer>();
        this.parameterCountMap = new HashMap<Method, Integer>();
        this.methods = new HashMap<String, Method>();
        this.signatureRequiredMethods = new HashSet<Method>();
        if (x instanceof InterfaceHandlerSetter) {
            ((InterfaceHandlerSetter) x).setInterfaceHandler(this);
        }
    }

    public static <T extends RemoteAPIInterface> InterfaceHandler<T> create(final Class<T> c, final RemoteAPIInterface x, final int defaultAuthLevel) throws ParseException, SecurityException, NoSuchMethodException {
        final InterfaceHandler<T> ret = new InterfaceHandler<T>(c, x, defaultAuthLevel);
        ret.parse();
        return ret;
    }

    public void add(final Class<T> c, final RemoteAPIInterface process, final int defaultAuthLevel) throws ParseException {
        if (this.sessionRequired != (c.getAnnotation(ApiSessionRequired.class) != null)) {
            throw new ParseException("Check SessionRequired for " + this);
        }
        if (defaultAuthLevel != this.getDefaultAuthLevel()) {
            throw new ParseException("Check Authlevel " + c + " " + this);
        }
        if (process != this.impl) {
            throw new ParseException(process + "!=" + this.impl);
        }
        try {
            this.interfaceClasses.add(c);
            this.parse();
        } catch (final ParseException e) {
            this.interfaceClasses.remove(c);
            this.parse();
            throw e;
        }

    }

    public int getAuthLevel(final Method m) {
        final Integer auth = this.methodsAuthLevel.get(m);
        if (auth != null) {
            return auth;
        }
        return this.defaultAuthLevel;
    }

    public int getDefaultAuthLevel() {
        return this.defaultAuthLevel;
    }

    /**
     * @param length
     * @param methodName
     * @return
     */
    public Method getMethod(final String methodName, final int length) {
        final String methodID = methodName + length;
        final Method ret = this.methods.get(methodID);
        if (ret != null) {
            return ret;
        }
        return this.methods.get(methodName);
    }

    /**
     * @param method
     * @return
     */
    public int getParameterCount(final Method method) {
        final Integer ret = this.parameterCountMap.get(method);
        if (ret != null) {
            return ret;
        }
        return -1;
    }

    public Method getSignatureHandler() {
        return this.signatureHandler;
    }

    public void help(final RemoteAPIRequest request, final RemoteAPIResponse response) throws InstantiationException, IllegalAccessException, IOException {
        byte[] bytes = null;
        if ("true".equals(request.getParameterbyKey("json"))) {
            bytes = this.helpBytesJson.get();
            if (bytes == null) {
                bytes = this.helpJSON(request, response).getBytes("UTF-8");
                this.helpBytesJson = new SoftReference<byte[]>(bytes);
            }
            response.getResponseHeaders().add(new HTTPHeader(HTTPConstants.HEADER_RESPONSE_CONTENT_TYPE, "application/json"));
        } else {
            bytes = this.helpBytes.get();
            if (bytes == null) {
                bytes = this.helpText().getBytes("UTF-8");
                this.helpBytes = new SoftReference<byte[]>(bytes);
            }
            response.getResponseHeaders().add(new HTTPHeader(HTTPConstants.HEADER_RESPONSE_CONTENT_TYPE, "text/plain"));
        }
        response.setResponseCode(HTTPConstants.ResponseCode.SUCCESS_OK);
        response.sendBytes(request, bytes);
    }

    private String helpJSON(final RemoteAPIRequest request, final RemoteAPIResponse response) throws IOException {
        final List<RemoteAPIMethodDefinition> methodDefinitions = new ArrayList<RemoteAPIMethodDefinition>();
        for (final Method m : this.methods.values()) {
            final RemoteAPIMethodDefinition mDef = new RemoteAPIMethodDefinition();
            mDef.setMethodName(m.getName());

            final ApiDoc an = m.getAnnotation(ApiDoc.class);
            if (an != null) {
                mDef.setDescription(an.value());
            }

            final List<String> parameters = new ArrayList<String>();

            for (int i = 0; i < m.getGenericParameterTypes().length; i++) {
                if (m.getParameterTypes()[i] == RemoteAPIRequest.class || m.getParameterTypes()[i] == RemoteAPIResponse.class) {
                    continue;
                }
                parameters.add(m.getParameterTypes()[i].getSimpleName());
            }
            mDef.setParameters(parameters);

            methodDefinitions.add(mDef);
        }
        return JSonStorage.serializeToJson(methodDefinitions);
    }

    private String helpText() {
        final StringBuilder sb = new StringBuilder();
        for (final Class<T> interfaceClass : this.interfaceClasses) {
            sb.append(interfaceClass.getName());
            sb.append("\r\n");
        }
        sb.append("\r\n");
        for (final Method m : this.methods.values()) {
            if (m == InterfaceHandler.HELP) {
                sb.append("\r\n====- " + m.getName() + " -====");
                sb.append("\r\n    Description: This Call");
                sb.append("\r\n           Call: ");
                sb.append("/" + m.getName() + "\r\n");
                continue;

            }
            String name = m.getName();
            final ApiMethodName methodname = m.getAnnotation(ApiMethodName.class);
            if (methodname != null) {
                name = methodname.value();
            }
            sb.append("\r\n====- " + name + " -====");
            final ApiDoc an = m.getAnnotation(ApiDoc.class);
            if (an != null) {
                sb.append("\r\n    Description: ");
                sb.append(an.value() + "");
            }
            // sb.append("\r\n    Description: ");

            final HashMap<Type, Integer> map = new HashMap<Type, Integer>();
            String call = "/" + name;
            int count = 0;
            for (int i = 0; i < m.getGenericParameterTypes().length; i++) {
                if (m.getParameterTypes()[i] == RemoteAPIRequest.class || m.getParameterTypes()[i] == RemoteAPIResponse.class) {
                    continue;
                }
                count++;
                if (i > 0) {
                    call += "&";

                } else {
                    call += "?";
                }

                Integer num = map.get(m.getParameterTypes()[i]);
                if (num == null) {
                    map.put(m.getParameterTypes()[i], 0);
                    num = 0;
                }
                num++;
                call += m.getParameterTypes()[i].getSimpleName() + "" + num;
                sb.append("\r\n      Parameter: " + count + " - " + m.getParameterTypes()[i].getSimpleName() + "" + num);
                map.put(m.getParameterTypes()[i], num);

            }
            sb.append("\r\n           Call: " + call);

            sb.append("\r\n");
        }
        return sb.toString();
    }

    /**
     * @param method
     * @param parameters
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public Object invoke(final Method method, final Object[] parameters) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (method.getDeclaringClass() == InterfaceHandler.class) {
            return method.invoke(this, parameters);
        } else {
            return method.invoke(this.impl, parameters);
        }
    }

    /**
     * @return the sessionRequired
     */
    public boolean isSessionRequired() {
        return this.sessionRequired;
    }

    /**
     * @param sessionRequired
     *            the sessionRequired to set
     * @throws ParseException
     */
    protected void setSessionRequired(final boolean sessionRequired) throws ParseException {

        this.sessionRequired = sessionRequired;
    }

    public boolean isSignatureRequired(final Method m) {
        return this.signatureRequiredMethods.contains(m);
    }

    /**
     * @throws ParseException
     *
     */
    private void parse() throws ParseException {
        this.methods.clear();
        this.parameterCountMap.clear();
        this.methodsAuthLevel.clear();
        this.methods.put("help", InterfaceHandler.HELP);
        this.parameterCountMap.put(InterfaceHandler.HELP, 0);
        this.methodsAuthLevel.put(InterfaceHandler.HELP, 0);
        this.signatureHandler = null;
        Class<T> signatureHandlerNeededClass = null;
        for (final Class<T> interfaceClass : this.interfaceClasses) {
            for (final Method m : interfaceClass.getMethods()) {
                final ApiHiddenMethod hidden = m.getAnnotation(ApiHiddenMethod.class);
                if (hidden != null) {
                    continue;
                }
                this.validateMethod(m);
                int paramCounter = 0;
                for (final Class<?> c : m.getParameterTypes()) {
                    if (c != RemoteAPIRequest.class && c != RemoteAPIResponse.class) {
                        paramCounter++;
                    }
                }
                String name = m.getName();
                if ("handleRemoteAPISignature".equals(name) && paramCounter == 0) {
                    this.signatureHandler = m;
                    continue;
                }
                final ApiMethodName methodname = m.getAnnotation(ApiMethodName.class);
                if (methodname != null) {
                    name = methodname.value();
                }
                if (this.methods.put(name + paramCounter, m) != null) {

                    throw new ParseException(interfaceClass + " already contains method: \r\n" + name + "\r\n");
                }

                if (m.getAnnotation(ApiRawMethod.class) != null) {
                    this.methods.put(name, m);
                }
                this.parameterCountMap.put(m, paramCounter);

                final ApiAuthLevel auth = m.getAnnotation(ApiAuthLevel.class);
                if (auth != null) {
                    this.methodsAuthLevel.put(m, auth.value());
                }
                final ApiSignatureRequired signature = m.getAnnotation(ApiSignatureRequired.class);
                if (signature != null) {
                    signatureHandlerNeededClass = interfaceClass;
                    this.signatureRequiredMethods.add(m);
                }
            }
        }
        if (signatureHandlerNeededClass != null && this.signatureHandler == null) {
            throw new ParseException(signatureHandlerNeededClass + " Contains methods that need validated Signatures but no Validator provided");
        }
    }

    /**
     * @param m
     * @throws ParseException
     */
    private void validateMethod(final Method m) throws ParseException {
        if (m == InterfaceHandler.HELP) {
            throw new ParseException(m + " is reserved for internal usage");
        }
        boolean responseIsParamater = false;
        for (final Type t : m.getGenericParameterTypes()) {
            if (RemoteAPIRequest.class == t) {
                continue;
            } else if (RemoteAPIResponse.class == t) {
                if (m.getAnnotation(AllowResponseAccess.class) == null) {
                    responseIsParamater = true;
                }
                continue;
            } else {
                try {
                    JSonStorage.canStore(t, m.getAnnotation(AllowNonStorableObjects.class) != null);
                } catch (final InvalidTypeException e) {
                    throw new ParseException("Parameter " + t + " of " + m + " is invalid", e);
                }
            }
        }
        if (responseIsParamater) {
            if (m.getGenericReturnType() != void.class && m.getGenericReturnType() != Void.class) {
                if (!RemoteAPISignatureHandler.class.isAssignableFrom(m.getDeclaringClass())) {
                    throw new ParseException("Response in Parameters. " + m + " must return void, and has to handle the response itself");
                }
            }
        } else {
            try {
                if (m.getGenericReturnType() == void.class || m.getGenericReturnType() == Void.class) {
                    // void is ok.
                    return;
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
                        JSonStorage.canStore(m.getGenericReturnType(), m.getAnnotation(AllowNonStorableObjects.class) != null);
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
                        throw new InvalidTypeException(e);
                    }
                }
            } catch (final InvalidTypeException e) {
                throw new ParseException("return Type of " + m + " is invalid", e);
            }
        }

    }
}
