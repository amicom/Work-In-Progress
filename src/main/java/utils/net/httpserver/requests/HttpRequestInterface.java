/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpserver.requests
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpserver.requests;

import utils.net.HeaderCollection;

import java.io.IOException;
import java.util.List;


/**
 * @author daniel
 *
 */
public interface HttpRequestInterface {

    public String getRequestedPath();

    public String getParameterbyKey(String key) throws IOException;

    public String getRequestedURL();

    /**
     * @return the requestedURLParameters
     */
    public List<KeyValuePair> getRequestedURLParameters();

    public HeaderCollection getRequestHeaders();

}
