/**
 * Copyright (c) 2009 - 2012 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
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
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author daniel
 *
 */
public interface DesktopSupport {

    void browseURL(URL url) throws IOException, URISyntaxException;

    boolean isBrowseURLSupported();

    boolean isOpenFileSupported();

    void openFile(File file) throws IOException;

    boolean shutdown(boolean force);

    boolean standby();

    boolean hibernate();

    String getDefaultDownloadDirectory();

}
