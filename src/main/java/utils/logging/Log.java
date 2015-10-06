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

import org.slf4j.LoggerFactory;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public  enum Log {;


    public static final Logger L;

    public static final org.slf4j.Logger log;

    private static final ThreadLocal<LogToFileHandler> fh = new ThreadLocal<>();
    private static final String EXCEPTION_MESSAGE = " Exception occurred";

    static {
        L = Logger.getLogger("logger");
        log =  LoggerFactory.getLogger(Log.class);
        L.setUseParentHandlers(false);
        ConsoleHandler cHandler = new ConsoleHandler();
        cHandler.setLevel(Level.ALL);
        cHandler.setFormatter(new LogFormatter());
        L.addHandler(cHandler);
        try {
            fh.set(new LogToFileHandler());
            fh.get().setFormatter(new FileLogFormatter());
            L.addHandler(fh.get());
        } catch (Throwable e) {
            exception(e);
        }
        L.addHandler(LogEventHandler.getInstance());
        L.setLevel(Level.WARNING);
    }

    public static synchronized void closeLogfile() {
        if (fh.get() != null) {
            fh.get().flush();
            fh.get().close();
            L.removeHandler(fh.get());
            fh.set(null);
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
            LogRecord lr = new LogRecord(level, level.getName() + EXCEPTION_MESSAGE);
            lr.setThrown(e);
            lr.setSourceClassName(st[i].getClassName() + '.' + st[i].getMethodName());
            lr.setSourceMethodName(st[i].getFileName() + ':' + st[i].getLineNumber());
            L.log(lr);
        } catch (Throwable ignored) {
            L.log(level, level.getName() + EXCEPTION_MESSAGE, e);
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
        if (fh.get() != null) {
            fh.get().flush();
        }
    }

    /**
     * Returns the loggerinstance for logging events
     *
     * @return
     */
    public static Logger getL() {
        return L;
    }

}
