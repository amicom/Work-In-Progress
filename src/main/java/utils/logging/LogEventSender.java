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


import events.Eventsender;

public class LogEventSender extends Eventsender<LogListener, LogEvent> {

    @Override
    protected void fireEvent(LogListener listener, LogEvent event) {
        listener.onLogEvent(event);

    }

}
