/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpserver.test
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package remoteapi;

import remoteapi.exceptions.ApiCommandNotAvailable;
import utils.net.HeaderCollection;
import utils.net.httpserver.requests.HttpRequest;
import utils.net.httpserver.requests.KeyValuePair;
import utils.net.httpserver.session.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author daniel
 *
 */
public class SessionRemoteAPIRequest<T extends HttpSession> extends RemoteAPIRequest {

    private final T session;
    private final RemoteAPIRequest apiRequest;

    public SessionRemoteAPIRequest(final HttpRequest request, final RemoteAPIRequest apiRequest, final T session) throws ApiCommandNotAvailable {
        super(apiRequest.getIface(), apiRequest.getMethodName(), apiRequest.getParameters(), request, apiRequest.getJqueryCallback());
        this.apiRequest = apiRequest;
        this.session = session;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "SessionAPIRequest_" + (session == null ? null : session.getSessionID()) + "\r\n" + apiRequest;
    }

    public RemoteAPIRequest getApiRequest() {
        return this.apiRequest;
    }

    @Override
    public InterfaceHandler<?> getIface() {

        return this.apiRequest.getIface();
    }

    @Override
    public InputStream getInputStream() throws IOException {

        return this.apiRequest.getInputStream();
    }

    @Override
    public String getJqueryCallback() {

        return this.apiRequest.getJqueryCallback();
    }

    @Override
    public Method getMethod() {

        return this.apiRequest.getMethod();
    }

    @Override
    public String getMethodName() {

        return this.apiRequest.getMethodName();
    }

    @Override
    public String[] getParameters() {

        return this.apiRequest.getParameters();
    }

    // @Override
    // public List<KeyValuePair> getPostParameter() throws IOException {
    //
    // return apiRequest.getPostParameter();
    // }

    @Override
    public List<String> getRemoteAddresses() {

        return this.apiRequest.getRemoteAddresses();
    }

    @Override
    public String getRequestedPath() {

        return this.apiRequest.getRequestedPath();
    }

    @Override
    public String getRequestedURL() {

        return this.apiRequest.getRequestedURL();
    }

    @Override
    public List<KeyValuePair> getRequestedURLParameters() {

        return this.apiRequest.getRequestedURLParameters();
    }

    @Override
    public HeaderCollection getRequestHeaders() {

        return this.apiRequest.getRequestHeaders();
    }

    @Override
    public REQUESTTYPE getRequestType() {

        return this.apiRequest.getRequestType();
    }

    public T getSession() {
        return this.session;
    }

}
