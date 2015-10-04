/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpconnection
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpconnection;

import java.io.IOException;

/**
 * @author daniel
 *
 */
public abstract class HTTPProxyException extends IOException {

    private static final long serialVersionUID = -7826780596815416403L;
    protected HTTPProxy proxy = null;

    public HTTPProxyException() {
        super();
    }

    public HTTPProxyException(final String message) {
        super(message);
    }

    public HTTPProxyException(final Throwable cause) {
        super(cause);
    }

    public HTTPProxy getProxy() {
        return this.proxy;
    }

}
