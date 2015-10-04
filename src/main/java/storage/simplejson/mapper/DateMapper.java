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


import storage.simplejson.JSonNode;
import storage.simplejson.JSonValue;

import java.util.Date;

/**
 * @author thomas
 *
 */
public class DateMapper extends TypeMapper<Date> {

    /* (non-Javadoc)
     * @see org.appwork.storage.simplejson.mapper.TypeMapper#mapObject(java.lang.Object)
     */
    @Override
    public JSonNode obj2Json(Date obj) {

        return new JSonValue(obj.getTime());
    }

    /* (non-Javadoc)
     * @see org.appwork.storage.simplejson.mapper.TypeMapper#json2Obj(org.appwork.storage.simplejson.JSonNode)
     */
    @Override
    public Date json2Obj(JSonNode json) {

        return new Date((Long) ((JSonValue) json).getValue());
    }

}
