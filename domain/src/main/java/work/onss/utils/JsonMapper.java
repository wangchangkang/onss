package work.onss.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JsonMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static  {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }


    public static  <T> String toJson(T t) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T fromJson(String json, Class<T> t) {
        T result = null;
        try {
            result = objectMapper.readValue(json, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static <T> List<T> fromCollectionJson(String json, Class<Collection<T>> collectionClass,Class<T> tClass) {
        List<T> result = null;
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(collectionClass,tClass);
            result = objectMapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <K,V> Map<K,V> fromJson(String json, Class<K> key, Class<V> value) {
        Map<K,V> result = null;
        try {
            JavaType type = objectMapper.getTypeFactory().constructMapLikeType(Map.class, key, value);
            result = objectMapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
