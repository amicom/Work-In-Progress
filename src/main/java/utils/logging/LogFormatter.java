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
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {
    /**
     * Date to convert timestamp to a readable format
     */
    private final Date date = new Date();
    /**
     * Dateformat to convert timestamp to a readable format
     */
    private final DateFormat longTimestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private final String NEWLINE = CrossSystem.getNewLine();
    /**
     * For thread controlled logs
     */
    private int lastThreadID;

    @Override
    public synchronized String format(final LogRecord record) {
        /* clear StringBuilder buffer */
        final StringBuilder sb = new StringBuilder();

        // Minimize memory allocations here.
        this.date.setTime(record.getMillis());

        final String message = this.formatMessage(record);
        final int th = record.getThreadID();

        if (th != this.lastThreadID) {
            sb.append(NEWLINE);
            sb.append("THREAD: ");
            sb.append(th);

            sb.append(NEWLINE);
        }
        this.lastThreadID = th;

        sb.append(record.getThreadID());
        sb.append('|');
        sb.append(record.getLoggerName());
        sb.append(' ');
        sb.append(this.longTimestamp.format(this.date));
        sb.append(" - ");
        sb.append(record.getLevel().getName());
        sb.append(" [ ");
        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
        } else {
            sb.append(record.getLoggerName());
        }
        if (record.getSourceMethodName() != null) {
            sb.append('(');
            sb.append(record.getSourceMethodName());
            sb.append(')');
        }

        sb.append(" ] ");

        sb.append("-> ");
        sb.append(message);
        sb.append(NEWLINE);
        if (record.getThrown() != null) {
            sb.append(Exceptions.getStackTrace(record.getThrown()));
            sb.append(NEWLINE);
        }
        return sb.toString();
    }
}
