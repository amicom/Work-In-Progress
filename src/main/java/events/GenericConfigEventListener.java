package events;


import storage.config.ValidationException;
import storage.config.handler.KeyHandler;

import java.util.EventListener;

public interface GenericConfigEventListener<T> extends EventListener {
    void onConfigValidatorError(KeyHandler<T> keyHandler, T invalidValue, ValidationException validateException);

    void onConfigValueModified(KeyHandler<T> keyHandler, T newValue);
}
