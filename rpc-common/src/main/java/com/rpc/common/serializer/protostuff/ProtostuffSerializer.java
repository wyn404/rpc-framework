package com.rpc.common.serializer.protostuff;

import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.rpc.common.serializer.Serializer;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import javax.xml.validation.Schema;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerialize extends Serializer {

    private Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private Objenesis objenesis = new ObjenesisStd(true);

    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> cls) {
        // for thread-safe
        return (Schema<T>) cachedSchema.computeIfAbsent(cls, RuntimeSchema::createFrom);
    }

    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> cla) {
        return null;
    }
}
