/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.storage.simplejson.mapper
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package storage.simplejson.mapper;

import org.appwork.storage.simplejson.JSonNode;
import org.appwork.storage.simplejson.JSonValue;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author thomas
 *
 */
public class URLMapper extends TypeMapper<URL> {

    /* (non-Javadoc)
     * @see org.appwork.storage.simplejson.mapper.TypeMapper#mapObject(java.lang.Object)
     */
    @Override
    public JSonNode obj2Json(URL obj) {
        return new JSonValue(obj.toExternalForm());
    }

    /* (non-Javadoc)
     * @see org.appwork.storage.simplejson.mapper.TypeMapper#json2Obj(org.appwork.storage.simplejson.JSonNode)
     */
    @Override
    public URL json2Obj(JSonNode json) {
        // TODO Auto-generated method stub
        try {
            return new URL((String) ((JSonValue) json).getValue());
        } catch (MalformedURLException e) {

        }
        return null;
    }

}
