/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.remoteapi
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package remoteapi;

import utils.Application;
import utils.net.ChunkedOutputStream;
import utils.net.HTTPHeader;
import utils.net.HeaderCollection;
import utils.net.httpserver.responses.HttpResponse;
import utils.net.httpserver.responses.HttpResponseInterface;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import static http.HTTPConstants.*;

/**
 * @author daniel
 * 
 */
public class RemoteAPIResponse implements HttpResponseInterface {

    protected final int        MAXUNCOMPRESSED = 32767;

    private final HttpResponse response;
    private final RemoteAPI    remoteAPI;

    @Override
    public String toString() {
        return super.toString();
    }

    public RemoteAPIResponse(final HttpResponse response, final RemoteAPI remoteAPI) {
        this.response = response;
        // Remote API requests are available via CORS by default.
        this.getResponseHeaders().add(new HTTPHeader(HEADER_RESPONSE_ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
        this.remoteAPI = remoteAPI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.utils.net.httpserver.responses.HttpResponseInterface#
     * closeConnection()
     */
    @Override
    public void closeConnection() {
        this.response.closeConnection();
    }

    public HttpResponse getHttpResponse() {
        return this.response;
    }

    public OutputStream getOutputStream(final boolean sendResponseHeaders) throws IOException {
        return this.response.getOutputStream(sendResponseHeaders);
    }

    /**
     * @return the remoteAPI
     */
    public RemoteAPI getRemoteAPI() {
        return this.remoteAPI;
    }

    public ResponseCode getResponseCode() {
        return this.response.getResponseCode();
    }

    /**
     * @return the responseHeaders
     */
    public HeaderCollection getResponseHeaders() {
        return this.response.getResponseHeaders();
    }

    public void sendBytes(final RemoteAPIRequest request, final byte[] bytes) throws IOException {
        /* we dont want this api response to get cached */
        if (this.getResponseHeaders().get(HEADER_REQUEST_CACHE_CONTROL) == null) {
            this.getResponseHeaders().add(new HTTPHeader(HEADER_REQUEST_CACHE_CONTROL, "no-store, no-cache"));
        }
        if (this.getResponseHeaders().get(HEADER_RESPONSE_CONTENT_TYPE) == null) {
            this.getResponseHeaders().add(new HTTPHeader(HEADER_RESPONSE_CONTENT_TYPE, "application/json"));
        }
        final boolean gzip = RemoteAPI.gzip(request);
        final boolean deflate = RemoteAPI.gzip(request) && Application.getJavaVersion() >= Application.JAVA16;
        if (gzip == false && deflate == false || bytes.length <= this.MAXUNCOMPRESSED) {
            this.getResponseHeaders().add(new HTTPHeader(HEADER_RESPONSE_CONTENT_LENGTH, bytes.length + ""));
            this.getOutputStream(true).write(bytes);
        } else {
            if (deflate) {
                this.getResponseHeaders().add(new HTTPHeader(HEADER_RESPONSE_CONTENT_ENCODING, "deflate"));
                this.getResponseHeaders().add(new HTTPHeader(HEADER_RESPONSE_TRANSFER_ENCODING, HEADER_RESPONSE_TRANSFER_ENCODING_CHUNKED));
                ChunkedOutputStream cos = null;
                DeflaterOutputStream out = null;
                cos = new ChunkedOutputStream(this.getOutputStream(true));
                out = new DeflaterOutputStream(cos, new Deflater(9, true));
                out.write(bytes);
                out.finish();
                out.flush();
                cos.sendEOF();
            } else {
                this.getResponseHeaders().add(new HTTPHeader(HEADER_RESPONSE_CONTENT_ENCODING, "gzip"));
                this.getResponseHeaders().add(new HTTPHeader(HEADER_RESPONSE_TRANSFER_ENCODING, HEADER_RESPONSE_TRANSFER_ENCODING_CHUNKED));
                ChunkedOutputStream cos = null;
                GZIPOutputStream out = null;
                cos = new ChunkedOutputStream(this.getOutputStream(true));
                out = new GZIPOutputStream(cos);
                out.write(bytes);
                out.finish();
                out.flush();
                cos.sendEOF();
            }
        }

    }

    /**
     * @param responseCode
     *            the responseCode to set
     */
    public void setResponseCode(final ResponseCode responseCode) {
        this.response.setResponseCode(responseCode);
    }

}
