/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpserver.test
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpserver.session;

import remoteapi.RemoteAPIInterface;
import remoteapi.RemoteAPIRequest;
import remoteapi.annotations.ApiDoc;
import remoteapi.annotations.ApiNamespace;
import remoteapi.exceptions.AuthException;

/**
 * @author daniel
 *
 */
@ApiNamespace("session")
public interface LoginAPIInterface extends RemoteAPIInterface {

    @ApiDoc("invalides the current token")
    boolean disconnect(final RemoteAPIRequest request);

    @ApiDoc("returns an un/authenticated token for given username and password or \"error\" in case login failed")
    String handshake(final RemoteAPIRequest request, String user, String password) throws AuthException;
}
