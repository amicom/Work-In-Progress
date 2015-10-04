/**
 * Copyright (c) 2009 - 2012 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net;

/**
 * @author daniel
 *
 */
public interface UploadProgress {

    public long getTotal();

    public void setTotal(long total);

    public long getUploaded();

    public void setUploaded(long loaded);

    public void increaseUploaded(long increase);

    public void onBytesUploaded(byte[] b, int len);
}
