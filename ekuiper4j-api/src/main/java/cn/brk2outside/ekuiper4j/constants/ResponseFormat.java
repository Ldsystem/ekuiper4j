package cn.brk2outside.ekuiper4j.constants;


/**
 * <p>Response format definition interface for API responses</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/12
 */
public interface ResponseFormat {

    byte IS_ARRAY   = 0b0001;
    byte IS_MAP     = 0b0010;
    byte IS_POJO    = 0b0100;
    byte IS_PRIMARY = 0b1000;
    byte IS_VOID    = 0b0000;

    static boolean isVoid(byte format) {
        return format == IS_VOID;
    }

    static boolean isArray(byte format) {
        return (format & IS_ARRAY) == IS_ARRAY;
    }
    
    static boolean isMap(byte format) {
        return (format & IS_MAP) == IS_MAP;
    }
    
    static boolean isPojo(byte format) {
        return (format & IS_POJO) == IS_POJO;
    }
    
    static boolean isPrimary(byte format) {
        return (format & IS_PRIMARY) == IS_PRIMARY;
    }

    static byte combine(byte... formats) {
        byte result = IS_VOID;
        for (byte format : formats) {
            result |= format;
        }
        return result;
    }

}
