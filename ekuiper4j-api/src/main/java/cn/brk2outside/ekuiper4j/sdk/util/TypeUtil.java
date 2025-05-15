package cn.brk2outside.ekuiper4j.sdk.util;


import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * <p>Utility for handling type references and conversions for API responses</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
public interface TypeUtil {

    /**
     * Create a ParameterizedTypeReference from a TypeReference
     * @param typeReference the TypeReference
     * @param <T> the type
     * @return a ParameterizedTypeReference of the same type
     */
    static <T> ParameterizedTypeReference<T> parameterizedType(TypeReference<T> typeReference) {
        return ParameterizedTypeReference.forType(typeReference.getType());
    }
    
    /**
     * Create a ParameterizedTypeReference for a simple class type
     * @param clazz the class
     * @param <T> the type
     * @return a ParameterizedTypeReference of the type
     */
    static <T> ParameterizedTypeReference<T> of(Class<T> clazz) {
        return ParameterizedTypeReference.forType(clazz);
    }
    
    /**
     * Create a ParameterizedTypeReference for a List of specified type
     * @param elementType the element class
     * @param <T> the element type
     * @return a ParameterizedTypeReference of List&lt;T&gt;
     */
    static <T> ParameterizedTypeReference<List<T>> listOf(Class<T> elementType) {
        Type type = new ParameterizedTypeImpl(List.class, elementType);
        return ParameterizedTypeReference.forType(type);
    }
    
    /**
     * Create a ParameterizedTypeReference for a Map with specified key and value types
     * @param keyType the key class
     * @param valueType the value class
     * @param <K> the key type
     * @param <V> the value type
     * @return a ParameterizedTypeReference of Map&lt;K, V&gt;
     */
    static <K, V> ParameterizedTypeReference<Map<K, V>> mapOf(Class<K> keyType, Class<V> valueType) {
        Type type = new ParameterizedTypeImpl(Map.class, keyType, valueType);
        return ParameterizedTypeReference.forType(type);
    }
    
    /**
     * Simple implementation of ParameterizedType
     */
    class ParameterizedTypeImpl implements ParameterizedType {
        private final Type rawType;
        private final Type[] actualTypeArguments;
        
        public ParameterizedTypeImpl(Type rawType, Type... actualTypeArguments) {
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
        }
        
        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }
        
        @Override
        public Type getRawType() {
            return rawType;
        }
        
        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
