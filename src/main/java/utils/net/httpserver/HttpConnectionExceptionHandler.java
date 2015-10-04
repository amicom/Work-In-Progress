/**
 * Copyright (c) 2009 - 2014 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpserver
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpserver;

import utils.net.httpserver.responses.HttpResponse;

import java.io.IOException;


/**
 * @author daniel
 *
 */
public interface HttpConnectionExceptionHandler {
    public boolean handle(final HttpResponse response) throws IOException;
}
