/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.os.mime
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.os.mime;

import sunwrapper.sun.awt.shell.ShellFolderWrapper;
import utils.Application;
import utils.ImageProvider.ImageProvider;
import utils.StringUtils;
import utils.images.IconIO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.prefs.Preferences;

public class MimeWindows extends MimeDefault {

    @Override
    public Icon getFileIcon(final String extension, final int width, final int height) throws IOException {
        final String iconKey = super.getIconKey(extension, width, height);
        Icon ret = super.getCacheIcon(iconKey);
        if (ret != null) {
            return ret;
        }
        if (!registryContainsFileIcon(extension)) {
            ret = super.getFileIcon(extension, width, height);
        } else {
            final File path = Application.getTempResource("images/" + extension + ".png");
            if (path.getParentFile().isDirectory()) {
                // woraround a bug we had until 24.06.2013.. created folders
                // instead of files
                path.getParentFile().delete();
            }
            if (!path.getParentFile().exists()) {
                path.getParentFile().mkdirs();
            }
            try {
                if (path.exists() && path.isFile()) {
                    ret = new ImageIcon(ImageProvider.read(path));
                } else {
                    File tempFile = null;
                    FileOutputStream fos = null;
                    try {
                        tempFile = File.createTempFile("icon", "." + extension);
                        final Image image = ShellFolderWrapper.getIcon(tempFile);
                        ret = new ImageIcon(image);
                        fos = new FileOutputStream(path);
                        ImageIO.write((RenderedImage) image, "png", fos);
                    } catch (final Throwable e) {
                        e.printStackTrace();
                        // http://www.oracle.com/technetwork/java/faq-sun-packages-142232.html
                        ret = ImageProvider.toImageIcon(FileSystemView.getFileSystemView().getSystemIcon(tempFile));
                    } finally {
                        try {
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (final Throwable e) {
                        }
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                }
            } catch (final Throwable e) {
            }
        }
        if (ret == null || ret.getIconWidth() < width || ret.getIconHeight() < height) {
            ret = super.getFileIcon(extension, width, height);
        }
        ret = IconIO.getScaledInstance(ret, width, height);
        super.saveIconCache(iconKey, ret);
        return ret;
    }

    /**
     * https://msdn.microsoft.com/en-us/library/windows/desktop/hh127427%28v=vs.85%29.aspx
     *
     * TODO: check ROOT_Class
     */
    private boolean registryContainsFileIcon(final String extension) {
        if (StringUtils.isNotEmpty(extension)) {
            try {
                final Preferences userRoot = Preferences.userRoot();
                final Class<?> clz = userRoot.getClass();
                final Method openKey = clz.getDeclaredMethod("openKey", byte[].class, int.class, int.class);
                openKey.setAccessible(true);
                final Method closeKey = clz.getDeclaredMethod("closeKey", int.class);
                closeKey.setAccessible(true);
                final String key = "Software\\Classes\\." + extension.toLowerCase(Locale.ENGLISH);
                Integer handle = null;
                try {
                    handle = (Integer) openKey.invoke(userRoot, toCstr(key), 0x20019, 0x20019);
                    return true;
                } finally {
                    if (handle != null) {
                        closeKey.invoke(userRoot, handle);
                    }
                }
            } catch (final Throwable e) {
            }
        }
        return false;
    }

    private byte[] toCstr(final String str) {
        final byte[] result = new byte[str.length() + 1];
        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }
}