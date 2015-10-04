/**
 * Copyright (c) 2009 - 2013 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.utils.logging2.extmanager
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.logging.extmanager;

import utils.logging.Log;
import utils.logging.LogSource;
import utils.logging.LogSourceProvider;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Thomas
 *
 */
public class LoggerFactory extends LogSourceProvider {
    private static final LoggerFactory INSTANCE = new LoggerFactory();

    static {
        System.setProperty("java.util.logging.manager", ExtLogManager.class.getName());
    }

    static {

        try {
            // the logmanager should not be initialized here. so setting the
            // property should tell the logmanager to init a ExtLogManager
            // instance.
            System.setProperty("java.util.logging.manager", ExtLogManager.class.getName());

            ((ExtLogManager) java.util.logging.LogManager.getLogManager()).setLoggerFactory(INSTANCE);
        } catch (final Throwable e) {
            e.printStackTrace();
            final java.util.logging.LogManager lm = java.util.logging.LogManager.getLogManager();
            System.err.println("Logmanager: " + lm);
            try {
                if (lm != null) {
                    // seems like the logmanager has already been set, and is
                    // not of type ExtLogManager. try to fix this here
                    // we experiences this bug once on a mac system. may be
                    // caused by mac jvm, or the mac install4j launcher

                    // 12.11:
                    // a winxp user had this problem with install4j (exe4j) as
                    // well.
                    // seems like 4xeej sets a logger before our main is
                    // reached.
                    final Field field = java.util.logging.LogManager.class.getDeclaredField("manager");
                    field.setAccessible(true);
                    final ExtLogManager manager = new ExtLogManager();

                    field.set(null, manager);
                    final Field rootLogger = java.util.logging.LogManager.class.getDeclaredField("rootLogger");
                    rootLogger.setAccessible(true);
                    final Logger rootLoggerInstance = (Logger) rootLogger.get(lm);
                    rootLogger.set(manager, rootLoggerInstance);
                    manager.addLogger(rootLoggerInstance);

                    // Adding the global Logger. Doing so in the Logger.<clinit>
                    // would deadlock with the LogManager.<clinit>.

                    final Method setLogManager = Logger.class.getDeclaredMethod("setLogManager", java.util.logging.LogManager.class);
                    setLogManager.setAccessible(true);
                    setLogManager.invoke(Logger.global, manager);

                    final Enumeration<String> names = lm.getLoggerNames();
                    while (names.hasMoreElements()) {
                        manager.addLogger(lm.getLogger(names.nextElement()));

                    }
                }
            } catch (final Throwable e1) {
                e1.printStackTrace();
            }
            // catch (final IllegalAccessException e1) {
            // e1.printStackTrace();
            // }
        }
    }

    private LogSource defaultLogger;

    public LoggerFactory() {
        super(System.currentTimeMillis());
        try {
            Log.closeLogfile();
        } catch (final Throwable e) {
        }
        try {
            for (final Handler handler : Log.L.getHandlers()) {
                Log.L.removeHandler(handler);
            }
        } catch (final Throwable e) {
        }
        Log.L.setUseParentHandlers(true);
        Log.L.setLevel(Level.ALL);
        defaultLogger = getLogger("Log.L");
        Log.L.addHandler(new Handler() {

            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }

            @Override
            public void publish(final LogRecord record) {
                final LogSource logger = defaultLogger;
                logger.log(record);
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                final LogSource logger = getLogger("UncaughtExceptionHandler");
                logger.severe("Uncaught Exception in: " + t.getId() + "=" + t.getName());
                logger.log(e);
                logger.close();
            }
        });
    }

    public static LoggerFactory I() {
        return INSTANCE;
    }

}
