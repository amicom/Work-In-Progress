/**
 * Copyright (c) 2009 - 2014 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.sunwrapper.sun.awt.image
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package sunwrapper.sun.awt.image;


import utils.logging.extmanager.LoggerFactory;

import java.awt.*;

/**
 * @author Thomas
 *
 */
public class ToolkitImageWrapper {

    /**
     * @param image
     * @return
     */
    public static boolean isInstanceOf(Image image) {
        try {
            return image instanceof sun.awt.image.ToolkitImage;
        } catch (NoClassDefFoundError e) {
            LoggerFactory.I().getLogger(ToolkitImageWrapper.class.getName()).log(e);
        }
        return false;
    }

}
