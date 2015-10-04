/**
 * Copyright (c) 2009 - 2013 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.remoteapi
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package remoteapi.exceptions;


import http.HTTPConstants;
import storage.JSonStorage;
import utils.net.HTTPHeader;
import utils.net.httpserver.HttpConnectionExceptionHandler;
import utils.net.httpserver.requests.HttpRequestInterface;
import utils.net.httpserver.responses.HttpResponse;
import utils.net.httpserver.responses.HttpResponseInterface;

import java.io.IOException;

/**
 * @author Thomas
 * 
 */
public class BasicRemoteAPIException extends Exception implements HttpConnectionExceptionHandler {
    /**
     * 
     */
    private static final long     serialVersionUID = 1L;
    private HttpRequestInterface request;

    private HttpResponseInterface response;

    private final String          type;

    private final HTTPConstants.ResponseCode code;

    private final Object          data;

    /**
     * @param e
     */
    public BasicRemoteAPIException(final IOException e) {
        this(e, "UNKNOWN", HTTPConstants.ResponseCode.SERVERERROR_INTERNAL, null);
    }

    /**
     * @param name
     * @param code2
     */
    public BasicRemoteAPIException(final String name, final HTTPConstants.ResponseCode code2) {
        this(null, name, code2, null);
    }

    /**
     * @param cause
     * @param name
     * @param code
     * @param data
     */
    public BasicRemoteAPIException(final Throwable cause, final String name, final HTTPConstants.ResponseCode code, final Object data) {
        super(name + "(" + code + ")", cause);
        this.data = data;
        this.type = name;
        this.code = code;
    }

    public HTTPConstants.ResponseCode getCode() {
        return this.code;
    }

    public Object getData() {
        return this.data;
    }

    public HttpRequestInterface getRequest() {
        return this.request;
    }

    public HttpResponseInterface getResponse() {
        return this.response;
    }

    public String getType() {
        return this.type;
    }

    /**
     * @param response
     * @throws IOException
     */
    public boolean handle(final HttpResponse response) throws IOException {
        byte[] bytes;
        final String str = JSonStorage.serializeToJson(new DeviceErrorResponse(this.getType(), this.data));
        bytes = str.getBytes("UTF-8");
        response.setResponseCode(this.getCode());
        /* needed for ajax/crossdomain */
        response.getResponseHeaders().add(new HTTPHeader(HTTPConstants.HEADER_RESPONSE_ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
        response.getResponseHeaders().add(new HTTPHeader(HTTPConstants.HEADER_RESPONSE_CONTENT_TYPE, "text; charset=UTF-8"));
        response.getResponseHeaders().add(new HTTPHeader(HTTPConstants.HEADER_RESPONSE_CONTENT_LENGTH, bytes.length + ""));
        response.getOutputStream(true).write(bytes);
        response.getOutputStream(true).flush();
        return true;

    }

    /**
     * @param request
     */
    public void setRequest(final HttpRequestInterface request) {
        this.request = request;

    }

    /**
     * @param response
     * @throws IOException
     */
    public boolean handle(final HttpResponse response) throws IOException {
        byte[] bytes;
        final String str = JSonStorage.serializeToJson(new DeviceErrorResponse(this.getType(), this.data));
        bytes = str.getBytes("UTF-8");
        response.setResponseCode(this.getCode());
        /* needed for ajax/crossdomain */
        response.getResponseHeaders().add(new HTTPHeader(HTTPConstants.HEADER_RESPONSE_ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
        response.getResponseHeaders().add(new HTTPHeader(HTTPConstants.HEADER_RESPONSE_CONTENT_TYPE, "text; charset=UTF-8"));
        response.getResponseHeaders().add(new HTTPHeader(HTTPConstants.HEADER_RESPONSE_CONTENT_LENGTH, bytes.length + ""));
        response.getOutputStream(true).write(bytes);
        response.getOutputStream(true).flush();
        return true;

    }    /**
     * @param response
     */
    public void setResponse(final HttpResponseInterface response) {
        this.response = response;

    }

}
