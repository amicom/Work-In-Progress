/**
 * Copyright (c) 2009 - 2013 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.remoteapi
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package remoteapi;


import remoteapi.exceptions.BasicRemoteAPIException;

/**
 * @author daniel
 *
 */
public interface RemoteAPISignatureHandler {
    boolean handleRemoteAPISignature(RemoteAPIRequest request, RemoteAPIResponse response) throws BasicRemoteAPIException;
}
