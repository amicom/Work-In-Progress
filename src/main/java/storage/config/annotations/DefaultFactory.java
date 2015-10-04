package storage.config.annotations;


import storage.config.defaults.AbstractDefaultFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DefaultFactory {
    Class<? extends AbstractDefaultFactory<?>> value();
}
