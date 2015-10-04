/**
 * Copyright (c) 2009 - 2015 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.os
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.os;

import java.util.ArrayList;

/**
 * @author thomas
 *
 */
public class SecuritySoftwareResponse extends ArrayList<SecuritySoftwareInfo> {
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
