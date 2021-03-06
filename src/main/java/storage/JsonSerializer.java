/**
 * Copyright (c) 2009 - 2013 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.storage
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package storage;

/**
 * @author Thomas
 *
 */
public interface JsonSerializer<T> {

    /**
     * @param list
     * @return
     */
    String toJSonString(T list);

    /**
     * @param arg0
     * @return
     */
    boolean canSerialize(Object arg0);

}
