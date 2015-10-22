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

import utils.Exceptions;
import utils.os.CrossSystem;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    /**
     * Date to convert timestamp to a readable format
     */
    private static final Date date = new Date();
    /**
     * Dateformat to convert timestamp to a readable format
     */
    private static final DateFormat timeStamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private static final String NEWLINE = CrossSystem.getNewLine();
    /**
     * For thread controlled logs
     */
    private int lastThreadID;



    @Override
    public final synchronized String format(LogRecord record) {
        /* clear StringBuilder buffer */
        StringBuilder sb = new StringBuilder(0);

        // Minimize memory allocations here.
        date.setTime(record.getMillis());

        String message = formatMessage(record);
        int th = record.getThreadID();


        if (th != lastThreadID) {
            sb.append(NEWLINE).append("THREAD: ").append(th).append(NEWLINE);
        }
        lastThreadID = th;

        sb.append(record.getThreadID())
                .append('|')
                .append(record.getLoggerName())
                .append(' ')
                .append(timeStamp.format(date))
                .append(" - ")
                .append(record.getLevel().getName())
                .append(" [ ");
        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
        } else {
            sb.append(record.getLoggerName());
        }
        if (record.getSourceMethodName() != null) {
            sb.append('(')
                    .append(record.getSourceMethodName())
                    .append(')');
        }

        sb.append(" ] ")
                .append("-> ")
                .append(message)
                .append(NEWLINE);
        Throwable thrown = record.getThrown();
        if (thrown != null) {
            sb.append(Exceptions.getStackTrace(thrown))
                    .append(NEWLINE);
        }
        return sb.toString();
    }
}
