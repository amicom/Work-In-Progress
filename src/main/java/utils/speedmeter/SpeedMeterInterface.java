/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.speedmeter
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.speedmeter;

/**
 * @author daniel
 *
 */
public interface SpeedMeterInterface {

    /**
     * resets the speed meter
     */
    void resetSpeedMeter();

    /**
     * returns speed in byte/s
     *
     * @return
     */
    long getSpeedMeter();

    /**
     * put bytes/time into this speed meter
     *
     * @param bytes
     * @param time
     */
    void putSpeedMeter(long bytes, long time);
}
