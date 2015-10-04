/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpserver.responses
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpserver.responses;

import http.HTTPConstants;
import utils.net.HeaderCollection;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author daniel
 *
 */
public interface HttpResponseInterface {
    public void closeConnection();

    public OutputStream getOutputStream(boolean sendResponseHeaders) throws IOException;

    /**
     * @return the responseCode
     */
    public HTTPConstants.ResponseCode getResponseCode();

    /**
     * @param responseCode
     *            the responseCode to set
     */
    public void setResponseCode(final HTTPConstants.ResponseCode responseCode);

    /**
     * @return the responseHeaders
     */
    public HeaderCollection getResponseHeaders();
}
