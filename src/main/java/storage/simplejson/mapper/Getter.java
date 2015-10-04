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


import storage.config.annotations.JSonFieldName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author thomas
 *
 */
public class Getter {

    private final String key;
    private final Method method;


    public Getter(final String name, final Method m) {
        JSonFieldName jsFieldName = m.getAnnotation(JSonFieldName.class);
        if (jsFieldName != null) {
            key = jsFieldName.value();
        } else {
            this.key = name;
        }
        this.method = m;
        m.setAccessible(true);
    }

    public String getKey() {
        return this.key;
    }

    public Method getMethod() {
        return this.method;
    }

    /**
     * @param obj
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public Object getValue(final Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        return this.method.invoke(obj);
    }

}
