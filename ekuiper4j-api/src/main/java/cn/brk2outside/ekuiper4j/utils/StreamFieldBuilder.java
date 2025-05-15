package cn.brk2outside.ekuiper4j.utils;

import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Utility class to build StreamField objects</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamFieldBuilder {

    /**
     * Create a bigint field
     *
     * @param name field name
     * @return StreamField
     */
    public static StreamField createBigintField(String name) {
        return createField(name, StreamConstants.DataType.BIGINT);
    }

    /**
     * Create a float field
     *
     * @param name field name
     * @return StreamField
     */
    public static StreamField createFloatField(String name) {
        return createField(name, StreamConstants.DataType.FLOAT);
    }

    /**
     * Create a string field
     *
     * @param name field name
     * @return StreamField
     */
    public static StreamField createStringField(String name) {
        return createField(name, StreamConstants.DataType.STRING);
    }

    /**
     * Create a datetime field
     *
     * @param name field name
     * @return StreamField
     */
    public static StreamField createDatetimeField(String name) {
        return createField(name, StreamConstants.DataType.DATETIME);
    }

    /**
     * Create a boolean field
     *
     * @param name field name
     * @return StreamField
     */
    public static StreamField createBooleanField(String name) {
        return createField(name, StreamConstants.DataType.BOOLEAN);
    }

    /**
     * Create a bytea field
     *
     * @param name field name
     * @return StreamField
     */
    public static StreamField createByteaField(String name) {
        return createField(name, StreamConstants.DataType.BYTEA);
    }

    /**
     * Create an array field
     *
     * @param name field name
     * @param elementType the type of elements in the array
     * @return StreamField
     */
    public static StreamField createArrayField(String name, StreamConstants.DataType elementType) {
        StreamField field = new StreamField();
        field.setName(name);
        
        StreamField.FieldType fieldType = new StreamField.FieldType();
        fieldType.setType(StreamConstants.DataType.ARRAY);
        
        StreamField.FieldType elementFieldType = new StreamField.FieldType();
        elementFieldType.setType(elementType);
        fieldType.setElementType(elementFieldType);
        
        field.setFieldType(fieldType);
        
        return field;
    }

    /**
     * Create a struct field
     *
     * @param name field name
     * @param structFields the fields within the struct
     * @return StreamField
     */
    public static StreamField createStructField(String name, List<StreamField> structFields) {
        StreamField field = new StreamField();
        field.setName(name);
        
        StreamField.FieldType fieldType = new StreamField.FieldType();
        fieldType.setType(StreamConstants.DataType.STRUCT);
        fieldType.setFields(structFields);
        
        field.setFieldType(fieldType);
        
        return field;
    }

    /**
     * Create a struct field
     *
     * @param name field name
     * @param structFields the fields within the struct as varargs
     * @return StreamField
     */
    public static StreamField createStructField(String name, StreamField... structFields) {
        return createStructField(name, Arrays.asList(structFields));
    }

    /**
     * Create a field with specified type
     *
     * @param name field name
     * @param type field type
     * @return StreamField
     */
    public static StreamField createField(String name, StreamConstants.DataType type) {
        StreamField field = new StreamField();
        field.setName(name);
        
        StreamField.FieldType fieldType = new StreamField.FieldType();
        fieldType.setType(type);
        field.setFieldType(fieldType);
        
        return field;
    }

    /**
     * Create a list of multiple fields
     *
     * @param fields varargs of StreamField objects
     * @return List of StreamField
     */
    public static List<StreamField> createFields(StreamField... fields) {
        List<StreamField> fieldList = new ArrayList<>();
        for (StreamField field : fields) {
            fieldList.add(field);
        }
        return fieldList;
    }
} 