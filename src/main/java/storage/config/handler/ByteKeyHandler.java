/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.storage.config
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package storage.config.handler;


import storage.config.ValidationException;
import storage.config.annotations.DefaultByteValue;
import storage.config.annotations.LookUpKeys;
import storage.config.annotations.SpinnerValidator;

import java.lang.annotation.Annotation;

/**
 * @author Thomas
 *
 */
public class ByteKeyHandler extends KeyHandler<Byte> {

    private SpinnerValidator validator;
    private byte min;
    private byte max;

    /**
     * @param storageHandler
     * @param key
     */
    public ByteKeyHandler(final StorageHandler<?> storageHandler, final String key) {
        super(storageHandler, key);
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends Annotation>[] getAllowedAnnotations() {
        // final java.util.List<Class<? extends Annotation>> list = new
        // ArrayList<Class<? extends Annotation>>();
        //
        // list.add(SpinnerValidator.class);

        // return (Class<? extends Annotation>[]) list.toArray(new Class<?>[]
        // {});
        //

        return (Class<? extends Annotation>[]) new Class<?>[]{LookUpKeys.class, SpinnerValidator.class};
    }

    @Override
    protected Class<? extends Annotation> getDefaultAnnotation() {
        return DefaultByteValue.class;
    }

    @Override
    protected void initDefaults() throws Throwable {
        this.setDefaultValue((byte) 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.storage.config.KeyHandler#initHandler()
     */
    @Override
    protected void initHandler() {
        this.validator = this.getAnnotation(SpinnerValidator.class);
        if (this.validator != null) {
            this.min = (byte) this.validator.min();
            this.max = (byte) this.validator.max();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.appwork.storage.config.KeyHandler#putValue(java.lang.Object)
     */
    @Override
    protected void putValue(final Byte object) {
        this.storageHandler.getPrimitiveStorage().put(this.getKey(), object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.appwork.storage.config.KeyHandler#validateValue(java.lang.Object)
     */
    @Override
    protected void validateValue(final Byte object) throws Throwable {
        if (this.validator != null) {
            final byte v = object.byteValue();
            if (v < this.min || v > this.max) {
                throw new ValidationException();
            }
        }
    }

}
