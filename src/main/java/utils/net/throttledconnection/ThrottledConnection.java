/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.throttledconnection
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.throttledconnection;

/**
 * @author daniel
 *
 */
public interface ThrottledConnection {

    public ThrottledConnectionHandler getHandler();

    /**
     * set a new ThrottledConnectionHandler
     *
     * @param manager
     */
    public void setHandler(ThrottledConnectionHandler manager);

    /**
     * get current limit
     *
     * @return
     */
    public int getLimit();

    /**
     * sets limit 0: no limit >0: use limit
     *
     * @param kpsLimit
     */
    public void setLimit(int kpsLimit);

    /**
     * return how many bytes this ThrottledConnection has transfered
     *
     * @return
     */
    public long transfered();

}
