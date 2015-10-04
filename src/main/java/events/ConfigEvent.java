/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.storage.config
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package events;


import storage.config.handler.KeyHandler;

/**
 * @author thomas
 *
 */
public class ConfigEvent extends SimpleEvent<KeyHandler<?>, Object, ConfigEvent.Types> {
    public ConfigEvent(final Types type, KeyHandler<?> caller, final Object parameter) {
        super(caller, type, parameter);
        // TODO Auto-generated constructor stub
    }


    public enum Types {
        VALUE_UPDATED,
        /**
         * Parameter[0] = Throwable from value validator<br>
         * Parameter[1] = Methodhandler
         */
        VALIDATOR_ERROR

    }

}
