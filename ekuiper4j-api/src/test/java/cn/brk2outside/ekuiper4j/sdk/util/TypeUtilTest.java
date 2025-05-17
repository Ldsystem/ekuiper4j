package cn.brk2outside.ekuiper4j.sdk.util;

import cn.brk2outside.ekuiper4j.Ekuiper4jApiApplicationTests;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TypeUtil methods
 */
public class TypeUtilTest {

    @Test
    void testSimpleTypeReference() {
        ParameterizedTypeReference<String> stringType = TypeUtil.of(String.class);
        
        assertNotNull(stringType);
        assertEquals(String.class, stringType.getType());
    }
    
    @Test
    void testListTypeReference() {
        ParameterizedTypeReference<List<String>> listType = TypeUtil.listOf(String.class);
        
        assertNotNull(listType);
        assertTrue(listType.getType() instanceof java.lang.reflect.ParameterizedType);
        
        java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) listType.getType();
        assertEquals(List.class, paramType.getRawType());
        assertEquals(String.class, paramType.getActualTypeArguments()[0]);
    }
    
    @Test
    void testMapTypeReference() {
        ParameterizedTypeReference<Map<String, Integer>> mapType = TypeUtil.mapOf(String.class, Integer.class);
        
        assertNotNull(mapType);
        assertTrue(mapType.getType() instanceof java.lang.reflect.ParameterizedType);
        
        java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) mapType.getType();
        assertEquals(Map.class, paramType.getRawType());
        assertEquals(String.class, paramType.getActualTypeArguments()[0]);
        assertEquals(Integer.class, paramType.getActualTypeArguments()[1]);
    }
    
    @Test
    void testOriginalMethod() {
        // Test the original method with TypeReference
        TypeReference<Map<String, List<Integer>>> complexType = new TypeReference<>() {};
        ParameterizedTypeReference<Map<String, List<Integer>>> reference = TypeUtil.parameterizedType(complexType);
        
        assertNotNull(reference);
        assertTrue(reference.getType() instanceof java.lang.reflect.ParameterizedType);
        
        java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) reference.getType();
        assertEquals(Map.class, paramType.getRawType());
        
        // Verify the List type parameter
        Type listType = paramType.getActualTypeArguments()[1];
        assertTrue(listType instanceof java.lang.reflect.ParameterizedType);
        java.lang.reflect.ParameterizedType listParamType = (java.lang.reflect.ParameterizedType) listType;
        assertEquals(List.class, listParamType.getRawType());
        assertEquals(Integer.class, listParamType.getActualTypeArguments()[0]);
    }
    
/*    @Test
    void testCompareOldAndNewApproach() {
        // Old approach
        TypeReference<List<String>> oldRef = new TypeReference<>() {};
        ParameterizedTypeReference<List<String>> oldType = TypeUtil.parameterizedType(oldRef);
        
        // New approach
        ParameterizedTypeReference<List<String>> newType = TypeUtil.listOf(String.class);
        
        // Both should represent the same type
        assertEquals(oldType.getType().toString(), newType.getType().toString());
    }*/
} 