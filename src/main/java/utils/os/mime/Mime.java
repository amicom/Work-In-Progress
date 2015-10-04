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

import javax.swing.*;
import java.io.IOException;

public interface Mime {

    public Icon getFileIcon(String extension, int width, int height) throws IOException;

    public String getMimeDescription(String mimetype);
}