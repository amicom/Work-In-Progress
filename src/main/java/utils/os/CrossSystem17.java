/**
 * Copyright (c) 2009 - 2014 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.os
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.os;

import java.io.File;
import java.io.IOException;

/**
 * @author daniel
 *
 */
public class CrossSystem17 {

    public static boolean caseSensitiveFileExists(File file) throws IOException {
        try {
            final java.nio.file.Path filePath = file.toPath().toRealPath(java.nio.file.LinkOption.NOFOLLOW_LINKS);
            final String filePathString = filePath.toString();
            final String fileString = file.getAbsolutePath();
            return filePathString.equals(fileString);
        } catch (java.nio.file.NoSuchFileException e) {
            return false;
        }
    }

}
