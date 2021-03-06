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

import utils.logging.Log;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MimeLinux extends MimeDefault {

    @Override
    public String getMimeDescription(String mimeType) {
        if (super.getMimeDescriptionCache(mimeType) != null) {
            return super.getMimeDescriptionCache(mimeType);
        }

        File file = new File("/usr/share/mime/" + mimeType + ".xml");

        if (!file.exists()) {
            return "Unknown";
        }

        String mime = "Unkown";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            String line;

            while ((line = in.readLine()) != null) {
                if (line.contains("<comment>")) {
                    Matcher m = Pattern.compile("<comment>(.*?)</comment>").matcher(line.trim());
                    m.find();
                    mime = m.group(1);
                }
            }

        } catch (FileNotFoundException e) {
            Log.exception(e);
        } catch (IOException e) {
            Log.exception(e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (final Throwable e) {
            }
        }
        super.saveMimeDescriptionCache(mimeType, mime);

        return mime;
    }
}