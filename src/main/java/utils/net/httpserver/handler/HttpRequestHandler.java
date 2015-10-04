/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpserver
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpserver.handler;


import remoteapi.exceptions.BasicRemoteAPIException;
import utils.net.httpserver.requests.GetRequest;
import utils.net.httpserver.requests.PostRequest;
import utils.net.httpserver.responses.HttpResponse;

/**
 * @author daniel
 *
 */
public interface HttpRequestHandler {

    boolean onGetRequest(final GetRequest request, final HttpResponse response) throws BasicRemoteAPIException;

    boolean onPostRequest(final PostRequest request, final HttpResponse response) throws BasicRemoteAPIException;

}
