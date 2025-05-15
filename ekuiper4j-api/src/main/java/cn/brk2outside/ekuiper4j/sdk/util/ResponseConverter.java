package cn.brk2outside.ekuiper4j.sdk.util;

import cn.brk2outside.ekuiper4j.constants.ResponseFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for converting API responses to target formats.
 * Handles complex nested structures and various combinations of response formats.
 */
public final class ResponseConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ResponseConverter() {
        // Utility class, do not instantiate
    }

    /**
     * Converts a complex response that could be nested with arrays, maps, and POJOs.
     * This method recursively processes the response based on the format flags.
     *
     * @param response Raw response object from HTTP client
     * @param targetClass The class to convert elements to
     * @param responseFormat Format flags from ResponseFormat
     * @param <T> Target type
     * @return Converted response object
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object response, Class<T> targetClass, byte responseFormat) {
        if (ResponseFormat.isVoid(responseFormat) || response == null) {
            return null;
        }

        // Return as-is if compatible
        if (targetClass.isInstance(response)) {
            return (T) response;
        }

        // Process array first if format indicates array
        if (ResponseFormat.isArray(responseFormat)) {
            if (response instanceof Collection<?> collection) {
                List<Object> result = new ArrayList<>(collection.size());
                for (Object item : collection) {
                    // Process each element based on the format
                    result.add(processElement(item, targetClass, responseFormat));
                }
                return (T) result;
            } else if (response instanceof Object[] array) {
                List<Object> result = new ArrayList<>(array.length);
                for (Object item : array) {
                    result.add(processElement(item, targetClass, responseFormat));
                }
                return (T) result;
            }
            // If response is not a collection but format indicates array, try to wrap it
            else if (!Collection.class.isAssignableFrom(targetClass)) {
                List<Object> result = new ArrayList<>(1);
                result.add(processElement(response, targetClass, responseFormat));
                return (T) result;
            }
        }

        // Process map if format indicates map
        if (ResponseFormat.isMap(responseFormat)) {
            if (response instanceof Map<?, ?> map) {
                Map<Object, Object> result = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    // Process each value based on the format
                    result.put(entry.getKey(), processElement(entry.getValue(), targetClass, responseFormat));
                }
                return (T) result;
            }
        }

        // As a fallback, try a direct conversion
        return processElement(response, targetClass, responseFormat);
    }

    /**
     * Processes a single element based on the format flags.
     */
    private static <T> T processElement(Object element, Class<T> targetClass, byte responseFormat) {
        if (ResponseFormat.isPojo(responseFormat)) {
            return convertToPojo(element, targetClass);
        } else {
            return convertToPrimaryType(element, targetClass);
        }
    }

    /**
     * Converts an object to a target POJO class using Jackson.
     */
    private static <T> T convertToPojo(Object response, Class<T> targetClass) {
        System.out.println(response);
        try {
            return OBJECT_MAPPER.convertValue(response, targetClass);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert response to " + targetClass.getName(), e);
        }
    }

    /**
     * Converts an object to a primary type or returns as-is if compatible.
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertToPrimaryType(Object value, Class<T> targetClass) {
        if (value == null) {
            return null;
        }
        
        if (targetClass.isInstance(value)) {
            return (T) value;
        }
        
        // Handle primitive type conversions
        if (targetClass == String.class) {
            return (T) value.toString();
        } else if (targetClass == Integer.class || targetClass == int.class) {
            return (T) (Integer) (value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString()));
        } else if (targetClass == Long.class || targetClass == long.class) {
            return (T) (Long) (value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString()));
        } else if (targetClass == Double.class || targetClass == double.class) {
            return (T) (Double) (value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(value.toString()));
        } else if (targetClass == Boolean.class || targetClass == boolean.class) {
            return (T) (value instanceof Boolean ? value : Boolean.parseBoolean(value.toString()));
        }
        
        // For collections and maps, try to use Jackson for conversion
        return convertToPojo(value, targetClass);
    }
} 