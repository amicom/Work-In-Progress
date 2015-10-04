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


import remoteapi.exceptions.ApiCommandNotAvailable;
import storage.JSonStorage;
import utils.net.HeaderCollection;
import utils.net.httpserver.requests.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author daniel
 *
 */
public class RemoteAPIRequest implements HttpRequestInterface {

    protected final HttpRequest request;
    private final InterfaceHandler<?> iface;

    private final String[] parameters;
    private final Method method;
    private final String jqueryCallback;
    private final String methodName;

    public RemoteAPIRequest(final InterfaceHandler<?> iface, final String methodName, final String[] parameters, final HttpRequest request, final String jqueryCallback) throws ApiCommandNotAvailable {
        this.iface = iface;
        this.parameters = parameters;
        this.request = request;
        this.methodName = methodName;
        this.jqueryCallback = jqueryCallback;
        this.method = this.iface.getMethod(methodName, this.parameters.length);
        if (method == null) {
            throw new ApiCommandNotAvailable(request.getRequestedURL());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return request + "\r\n" + "Method: " + method + "\r\nParameters:" + JSonStorage.serializeToJson(parameters);
    }

    public HttpRequest getHttpRequest() {
        return this.request;
    }

    public InterfaceHandler<?> getIface() {
        return this.iface;
    }

    public InputStream getInputStream() throws IOException {
        if (this.request instanceof PostRequest) {
            return ((PostRequest) this.request).getInputStream();
        }
        return null;
    }

    /**
     * @return the jqueryCallback
     */
    public String getJqueryCallback() {
        return this.jqueryCallback;
    }

    /**
     * @return
     */
    public Method getMethod() {

        return this.method;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return this.methodName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.appwork.utils.net.httpserver.requests.HttpRequestInterface#
     * getParameterbyKey(java.lang.String)
     */
    @Override
    public String getParameterbyKey(final String key) throws IOException {
        List<KeyValuePair> params = this.request.getRequestedURLParameters();
        if (params != null) {
            for (final KeyValuePair param : params) {
                if (key.equalsIgnoreCase(param.key)) {
                    return param.value;
                }
            }
        }
        if (this.request instanceof PostRequest) {
            params = ((PostRequest) this.request).getPostParameter();
            if (params != null) {
                for (final KeyValuePair param : params) {
                    if (key.equalsIgnoreCase(param.key)) {
                        return param.value;
                    }
                }
            }
        }
        return null;

    }

    public String[] getParameters() {
        return this.parameters;
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see org.appwork.utils.net.httpserver.requests.HttpRequestInterface#
    // * getPostParameter()
    // */
    // @Override
    // public List<KeyValuePair> getPostParameter() throws IOException {
    // return request.getPostParameter();
    // }

    /**
     * @see http://en.wikipedia.org/wiki/X-Forwarded-For There may be several
     *      Remote Addresses if the connection is piped through several proxies.<br>
     *      [0] is always the direct address.<br>
     *      if remoteAdresses.size>1 then<br>
     *      [1] is the actuall clients ip.<br>
     *      [2] is the proxy next to him..<br>
     *      [3] is the proxy next to [2]<br>
     *      ..<br>
     *      [size-1] should be the address next to [0]<br>
     */
    public List<String> getRemoteAddresses() {
        return this.request.getRemoteAddress();
    }

    public String getRequestedPath() {
        return this.request.getRequestedPath();
    }

    public String getRequestedURL() {
        return this.request.getRequestedURL();
    }

    /**
     * @return the requestedURLParameters
     */
    public List<KeyValuePair> getRequestedURLParameters() {
        return this.request.getRequestedURLParameters();
    }

    public HeaderCollection getRequestHeaders() {
        return this.request.getRequestHeaders();
    }

    /**
     * @return
     */
    public long getRequestID() {
        return -1;
    }

    public REQUESTTYPE getRequestType() {
        if (this.request instanceof OptionsRequest) {
            return REQUESTTYPE.OPTIONS;
        }
        if (this.request instanceof HeadRequest) {
            return REQUESTTYPE.HEAD;
        }
        if (this.request instanceof PostRequest) {
            return REQUESTTYPE.POST;
        }
        if (this.request instanceof GetRequest) {
            return REQUESTTYPE.GET;
        }
        return REQUESTTYPE.UNKNOWN;
    }

    /**
     * @return
     */
    public String getSignature() {
        return null;
    }

    public boolean validateRID() {
        return true;
    }

    public static enum REQUESTTYPE {
        HEAD,
        POST,
        OPTIONS,
        GET,
        UNKNOWN
    }

}