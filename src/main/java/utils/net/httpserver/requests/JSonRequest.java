/**
 * Copyright (c) 2009 - 2013 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpserver.requests
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpserver.requests;

import storage.Storable;
import storage.TypeRef;

/**
 * @author daniel
 *
 */
public class JSonRequest implements Storable {

    public static final TypeRef<JSonRequest> TYPE_REF = new TypeRef<JSonRequest>() {
        public java.lang.reflect.Type getType() {
            return JSonRequest.class;
        }

        ;
    };
    private String url;
    private long rid;
    private Object[] params;

    public JSonRequest(/* Storable */) {
    }

    public Object[] getParams() {
        return this.params;
    }

    public void setParams(final Object[] params) {
        this.params = params;
    }

    public long getRid() {
        return this.rid;
    }

    public void setRid(final long timestamp) {
        this.rid = timestamp;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
