/**
 * Copyright (c) 2009 - 2014 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.swing.sunwrapper
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package sunwrapper.sun.swing;

import utils.logging.extmanager.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author Thomas
 *
 */
public class SwingUtilities2Wrapper {

    private static boolean CLIP_STRING_IF_NECESSARY_OK = true;


    public static String clipStringIfNecessary(JComponent component, FontMetrics fontMetrics, String str, int availableWidth) {
        try {
            if (CLIP_STRING_IF_NECESSARY_OK) {
                return sun.swing.SwingUtilities2.clipStringIfNecessary(component, fontMetrics, str, availableWidth);

            }
        } catch (NoClassDefFoundError e) {
            CLIP_STRING_IF_NECESSARY_OK = false;
            LoggerFactory.I().getLogger(SwingUtilities2Wrapper.class.getName()).log(e);

        }
        System.err.println("sun.swing.SwingUtilities2.clipStringIfNecessary failed");
        return str;
    }

    /**
     * @param dispatchComponent
     * @param i
     */
    public static void setSkipClickCount(Component dispatchComponent, int i) {
        try {
            sun.swing.SwingUtilities2.setSkipClickCount(dispatchComponent, i);

        } catch (NoClassDefFoundError e) {

            LoggerFactory.I().getLogger(SwingUtilities2Wrapper.class.getName()).log(e);

        }
    }

}
