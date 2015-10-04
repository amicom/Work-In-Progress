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


import events.DefaultEvent;

@SuppressWarnings("deprecation")
public class LogEvent extends DefaultEvent {
    /**
     * parameter is of type LogRecord
     */
    public static final int NEW_RECORD = 0;

    public LogEvent(final Object caller) {
        super(caller);

    }

}
