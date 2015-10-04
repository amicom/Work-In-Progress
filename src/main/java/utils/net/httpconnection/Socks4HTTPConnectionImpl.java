/**
 * Copyright (c) 2009 - 2012 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpconnection
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpconnection;

import utils.net.socketconnection.Socks4SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;


/**
 * @author daniel
 *
 */
public class Socks4HTTPConnectionImpl extends SocksHTTPconnection {

    public Socks4HTTPConnectionImpl(final URL url, final HTTPProxy proxy) {
        super(url, proxy);
        if (this.proxy == null || !HTTPProxy.TYPE.SOCKS4.equals(this.proxy.getType())) {
            throw new IllegalArgumentException("proxy must be of type socks4");
        }
    }

    @Override
    protected Socket establishConnection() throws IOException {
        final Socks4SocketConnection socket = new Socks4SocketConnection(this.getProxy(), DESTTYPE.DOMAIN);
        socket.connect(this.proxyInetSocketAddress = new InetSocketAddress(this.httpHost, this.httpPort), this.getConnectTimeout(), this.proxyRequest);
        return socket;
    }

}
