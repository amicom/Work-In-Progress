/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.storage.config
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package storage.config.handler;


import storage.config.annotations.DefaultStringArrayValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Thomas
 *
 */
public class StringListHandler extends ListHandler<String[]> {

    /**
     * @param storageHandler
     * @param key
     * @param type
     */
    public StringListHandler(final StorageHandler<?> storageHandler, final String key, final Type type) {
        super(storageHandler, key, type);
    }

    @Override
    protected Class<? extends Annotation> getDefaultAnnotation() {
        return DefaultStringArrayValue.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.appwork.storage.config.KeyHandler#validateValue(java.lang.Object)
     */
    @Override
    protected void validateValue(final String[] object) throws Throwable {
    }

}
