/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.storage
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package storage;

/**
 * @author thomas
 *
 */
public interface JSONMapper {
    <T> void addSerializer(Class<T> clazz, final JsonSerializer<T> jsonSerializer);

    String objectToString(Object o) throws JSonMapperException;

    <T> T stringToObject(String jsonString, Class<T> clazz) throws JSonMapperException;

    <T> T convert(Object jsonString, TypeRef<T> type) throws JSonMapperException;

    <T> T stringToObject(String jsonString, TypeRef<T> type) throws JSonMapperException;
}
