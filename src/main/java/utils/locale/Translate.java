/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.utils.locale
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.locale;

/**
 * @author thomas
 * 
 */
public interface Translate {
    String getDefaultTranslation();

    /**
     * @return
     */
    int getWildCardCount();

    String s();

    String toString();

    String s(Object... args);
}
