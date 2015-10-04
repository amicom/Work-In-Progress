/**
 * Copyright (c) 2009 - 2012 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.net.throttledconnection
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.throttledconnection;

import java.util.List;

/**
 * @author daniel
 *
 */
public interface ThrottledConnectionHandler {

    void addThrottledConnection(ThrottledConnection con);

    List<ThrottledConnection> getConnections();

    int getLimit();

    void setLimit(int limit);

    int getSpeed();

    long getTraffic();

    void removeThrottledConnection(ThrottledConnection con);

    int size();

}
