/**
 * Copyright (c) 2009 - 2014 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.httpserver.requests
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.httpserver.requests;

/**
 * @author thomas
 *
 */
public class KeyValuePair {
    public String key;
    public String value;

    /**
     * @param decode
     * @param object
     */
    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @param string
     */
    public KeyValuePair(String value) {
        this(null, value);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return key + "=" + value;
    }
}
