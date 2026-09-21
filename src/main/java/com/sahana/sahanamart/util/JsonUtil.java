package com.sahana.sahanamart.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStream;
import java.io.Writer;

public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonUtil() {}

    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }

    public static void writeJson(Writer writer, Object object) {
        try {
            OBJECT_MAPPER.writeValue(writer, object);
        } catch (Exception e) {
            throw new RuntimeException("Error writing object as JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON to " + clazz.getSimpleName(), e);
        }
    }

    public static <T> T fromJson(InputStream is, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(is, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing stream to " + clazz.getSimpleName(), e);
        }
    }

    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }
}
