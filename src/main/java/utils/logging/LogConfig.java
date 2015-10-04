/**
 * Copyright (c) 2009 - 2012 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.logging2
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.logging;


import storage.config.ConfigInterface;
import storage.config.annotations.*;

/**
 * @author daniel
 *
 */
public interface LogConfig extends ConfigInterface {

    @AboutConfig
    @DefaultIntValue(2)
    @SpinnerValidator(min = 0, max = Integer.MAX_VALUE)
    @DescriptionForConfigEntry("Automatic remove logs older than x days")
    @RequiresRestart("A JDownloader Restart is Required")
    int getCleanupLogsOlderThanXDays();

    void setCleanupLogsOlderThanXDays(int x);

    @AboutConfig
    @DefaultIntValue(60)
    @SpinnerValidator(min = 30, max = Integer.MAX_VALUE)
    @DescriptionForConfigEntry("Timeout in secs after which the logger will be flushed/closed")
    @RequiresRestart("A JDownloader Restart is Required")
    int getLogFlushTimeout();

    void setLogFlushTimeout(int t);

    @AboutConfig
    @DefaultIntValue(5)
    @SpinnerValidator(min = 1, max = Integer.MAX_VALUE)
    @DescriptionForConfigEntry("Max number of logfiles for each logger")
    @RequiresRestart("A JDownloader Restart is Required")
    int getMaxLogFiles();

    void setMaxLogFiles(int m);

    @AboutConfig
    @DefaultIntValue(10 * 1024 * 1024)
    @SpinnerValidator(min = 0, max = Integer.MAX_VALUE)
    @DescriptionForConfigEntry("Max logfile size in bytes. Size <100Kbyte will disable logfiles")
    @RequiresRestart("A JDownloader Restart is Required")
    int getMaxLogFileSize();

    void setMaxLogFileSize(int s);

    @AboutConfig
    @DefaultBooleanValue(false)
    @DescriptionForConfigEntry("Enable debug mode, nearly everything will be logged!")
    @RequiresRestart("A JDownloader Restart is Required")
    boolean isDebugModeEnabled();

    void setDebugModeEnabled(boolean b);
}
