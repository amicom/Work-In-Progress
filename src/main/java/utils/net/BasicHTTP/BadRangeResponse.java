/**
 * Copyright (c) 2009 - 2013 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.BasicHTTP
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.BasicHTTP;

import utils.net.httpconnection.HTTPConnection;

import java.io.IOException;


/**
 * @author Thomas
 *
 */
public class BadRangeResponse extends IOException {

    private HTTPConnection connection;

    /**
     * @param connection
     */
    public BadRangeResponse(final HTTPConnection connection) {
        super("Got Non Range Response for a Range Request");
        this.connection = connection;
    }

    public HTTPConnection getConnection() {
        return connection;
    }

}
