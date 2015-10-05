/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.logging
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {

    private static Logger LOGGER;
    /**
     * For shorter access
     */
    public static Logger L = LOGGER;
    private static LogToFileHandler fh;

    /**
     * Create the singleton logger instance
     */
    static {
        LOGGER = Logger.getLogger("org.appwork");
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler cHandler = new ConsoleHandler();
        cHandler.setLevel(Level.ALL);
        cHandler.setFormatter(new LogFormatter());
        LOGGER.addHandler(cHandler);
        try {
            fh = new LogToFileHandler();
            fh.setFormatter(new FileLogFormatter());
            LOGGER.addHandler(fh);
        } catch (Throwable e) {
            exception(e);
        }
        LOGGER.addHandler(LogEventHandler.getInstance());
        LOGGER.setLevel(Level.WARNING);
    }

    public static synchronized void closeLogfile() {
        if (fh != null) {
            fh.flush();
            fh.close();
            LOGGER.removeHandler(fh);
            fh = null;
        }
    }

    /**
     * Adds an exception to the logger. USe this instead of e.printStackTrace if
     * you like the exception appear in log
     *
     * @param level
     * @param e
     */
    public static void exception(Level level, Throwable e) {
        try {
            StackTraceElement[] st = new Exception().getStackTrace();
            int i = 0;
            while (st[i].getClassName().equals(Log.class.getName())) {
                i++;
            }
            LogRecord lr = new LogRecord(level, level.getName() + " Exception occurred");
            lr.setThrown(e);
            lr.setSourceClassName(st[i].getClassName() + "." + st[i].getMethodName());
            lr.setSourceMethodName(st[i].getFileName() + ":" + st[i].getLineNumber());
            getLogger().log(lr);
        } catch (Throwable a1) {
            L.log(level, level.getName() + " Exception occurred", e);
        }
    }

    /**
     * Adds an exception to the logger. USe this instead of e.printStackTrace if
     * you like the exception appear in log
     *
     * @param e
     */
    public static void exception(Throwable e) {
        if (e == null) {
            e = new NullPointerException("e is null");
        }
        Level lvl = null;
        if (e instanceof ExceptionDefaultLogLevel) {
            lvl = ((ExceptionDefaultLogLevel) e).getDefaultLogLevel();
        }
        if (lvl == null) {
            lvl = Level.SEVERE;
        }
        exception(lvl, e);
    }

    public static synchronized void flushLogFile() {
        if (fh != null) {
            fh.flush();
        }
    }

    /**
     * Returns the loggerinstance for logging events
     *
     * @return
     */
    public static Logger getLogger() {
        return LOGGER;
    }

}
