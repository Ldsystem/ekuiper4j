package cn.brk2outside.ekuiper4j.sdk.util;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;

/**
 * <p> </p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
public class EnumSerializer {

    public static class EnumNameDirectSerializer extends JsonSerializer<Enum<?>> {
        @Override
        public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (null == value) {
                gen.writeNull();
            } else {
                gen.writeString(value.name());
            }
        }
    }

    public static class EnumNameDirectDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {
        private Class<? extends Enum> enumClass;

        public EnumNameDirectDeserializer() {
            // Default constructor
        }
        
        private EnumNameDirectDeserializer(Class<? extends Enum> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            if (enumClass == null) {
                throw new IllegalStateException("Enum class not specified for deserialization");
            }
            
            String enumName = p.getValueAsString();
            if (enumName == null || enumName.isEmpty()) {
                return null;
            }
            
            try {
                return Enum.valueOf(enumClass, enumName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid enum value '" + enumName + "' for enum class " + enumClass.getName(), e);
            }
        }
        
        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
            JavaType type = property != null ? property.getType() : ctxt.getContextualType();
            if (type != null && type.isEnumType()) {
                return new EnumNameDirectDeserializer(type.getRawClass().asSubclass(Enum.class));
            }
            return this;
        }
    }

    public static class EnumOrdinalSerializer extends JsonSerializer<Enum<?>> {

        @Override
        public void serialize(Enum<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (null == value) {
                gen.writeNull();
            } else {
                gen.writeNumber(value.ordinal());
            }
        }
    }

    public static class EnumOrdinalDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {
        private Class<? extends Enum> enumClass;
        
        public EnumOrdinalDeserializer() {
            // Default constructor
        }
        
        private EnumOrdinalDeserializer(Class<? extends Enum> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            if (enumClass == null) {
                throw new IllegalStateException("Enum class not specified for deserialization");
            }
            
            int ordinal = p.getValueAsInt();
            Enum<?>[] constants = enumClass.getEnumConstants();
            
            if (ordinal < 0 || ordinal >= constants.length) {
                throw new IOException("Invalid ordinal value " + ordinal + " for enum class " + enumClass.getName() + 
                                     ". Expected value between 0 and " + (constants.length - 1));
            }
            
            return constants[ordinal];
        }
        
        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
            JavaType type = property != null ? property.getType() : ctxt.getContextualType();
            if (type != null && type.isEnumType()) {
                return new EnumOrdinalDeserializer(type.getRawClass().asSubclass(Enum.class));
            }
            return this;
        }
    }
}
