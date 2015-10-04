/**
 * Copyright (c) 2009 - 2014 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.sunwrapper.sun.swing
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package sunwrapper.sun.swing;


import utils.logging.extmanager.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * @author Thomas
 *
 */
public class DefaultLookupWrapper {


    public static Color getColor(JComponent comp, ComponentUI ui, String key) {
        try {
            return sun.swing.DefaultLookup.getColor(comp, ui, key);
        } catch (final NoClassDefFoundError e) {
            LoggerFactory.I().getLogger(DefaultLookupWrapper.class.getName()).log(e);
            // DefaultLookupWrapper is sun.swing, any may not be
            // available
            // e.gh. in 1.6.0_01-b06
            return (Color) UIManager.get("TableHeader.focusCellForeground", comp.getLocale());

        }

    }

    public static Border getBorder(JComponent comp, ComponentUI ui, String key) {
        try {
            return sun.swing.DefaultLookup.getBorder(comp, ui, key);

        } catch (final NoClassDefFoundError e) {
            // DefaultLookupWrapper is sun.swing, any may not be available
            // e.gh. in 1.6.0_01-b06

            return (Border) UIManager.get("TableHeader.focusCellBorder", comp.getLocale());

        }
    }

}
