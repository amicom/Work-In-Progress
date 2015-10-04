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

import java.io.File;

/**
 * @author thomas
 *
 */
public class FileMapper extends TypeMapper<File> {

    /* (non-Javadoc)
     * @see org.appwork.storage.simplejson.mapper.TypeMapper#mapObject(java.lang.Object)
     */
    @Override
    public JSonNode obj2Json(File obj) {

        return new JSonValue(obj.getAbsolutePath());
    }

    /* (non-Javadoc)
     * @see org.appwork.storage.simplejson.mapper.TypeMapper#json2Obj(org.appwork.storage.simplejson.JSonNode)
     */
    @Override
    public File json2Obj(JSonNode json) {

        return new File((String) ((JSonValue) json).getValue());
    }

}
