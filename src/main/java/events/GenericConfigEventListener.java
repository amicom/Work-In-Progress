package events;


import storage.config.ValidationException;
import storage.config.handler.KeyHandler;

import java.util.EventListener;

public interface GenericConfigEventListener<T> extends EventListener {
    public void onConfigValidatorError(KeyHandler<T> keyHandler, T invalidValue, ValidationException validateException);

    public void onConfigValueModified(KeyHandler<T> keyHandler, T newValue);
}
