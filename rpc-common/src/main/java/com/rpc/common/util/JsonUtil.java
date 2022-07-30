package com.rpc.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 *  Json Util
 */
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        objectMapper.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        objectMapper.disable(SerializationFeature.CLOSE_CLOSEABLE);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
        objectMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
    }

    public static <T> byte[] serialize(T obj){
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return bytes;
    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        T obj = null;
        try {
            obj = objectMapper.readValue(data, cls);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return obj;
    }

    public static <type> type jsonToObject(String json, Class<?> cls) {
        type obj = null;
        JavaType javaType = objectMapper.getTypeFactory().constructType(cls);
        try {
            obj = objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return obj;
    }

    public static <type> type jsonToObjectList(String json, Class<?> collectionClass, Class<?>... elementClass) {
        type obj = null;
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(
                collectionClass, elementClass);
        try {
            obj = objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return obj;
    }

    public static <type> type jsonToObjectHashMap(String json,
                                                  Class<?> keyClass, Class<?> valueClass) {
        type obj = null;
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(HashMap.class, keyClass, valueClass);
        try {
            obj = objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return obj;
    }

    public static String objectToJson(Object o) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return json;
    }
}
