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


import storage.Storable;

/**
 * @author daniel
 *         {"password":null,"address":null,"connectMethodPrefered":false,"port"
 *         :-1,"type":null,"preferNativeImplementation":false,"username":null}
 */
public class HTTPProxyStorable implements Storable {

    private String username = null;
    private String password = null;
    private int port = -1;
    private String address = null;
    private TYPE type = null;
    private boolean useConnectMethod = false;
    private boolean preferNativeImplementation = false;

    public HTTPProxyStorable(/* storable */) {
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public TYPE getType() {
        return this.type;
    }

    public void setType(final TYPE type) {
        this.type = type;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public boolean isConnectMethodPrefered() {
        return this.useConnectMethod;
    }

    public void setConnectMethodPrefered(final boolean value) {
        this.useConnectMethod = value;
    }

    public boolean isPreferNativeImplementation() {
        return this.preferNativeImplementation;
    }

    public void setPreferNativeImplementation(final boolean preferNativeImplementation) {
        this.preferNativeImplementation = preferNativeImplementation;
    }

    public static enum TYPE {
        NONE,
        DIRECT,
        SOCKS4,
        SOCKS5,
        HTTP
    }
}
