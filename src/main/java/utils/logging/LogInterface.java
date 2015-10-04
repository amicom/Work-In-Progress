/**
 * Copyright (c) 2009 - 2012 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.logging2
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.logging;

/**
 * @author Thomas
 *
 */
public interface LogInterface {

    /**
     * @param string
     */
    void info(String msg);

    /**
     * @param e
     */
    void log(Throwable e);

    /**
     * @param string
     */
    void fine(String string);

}
