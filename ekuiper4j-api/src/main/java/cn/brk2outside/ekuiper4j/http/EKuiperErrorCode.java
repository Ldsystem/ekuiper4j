package cn.brk2outside.ekuiper4j.http;

import lombok.Getter;

/**
 * Enum representing the error codes returned by eKuiper API.
 */
@Getter
public enum EKuiperErrorCode {
    
    UNDEFINED_ERROR(1000, "Undefined error code"),
    RESOURCE_NOT_FOUND(1002, "Resource not found"),
    IO_ERROR(1003, "IO error in Source/Sink"),
    ENCODING_ERROR(1004, "Encoding error"),
    SQL_COMPILATION_ERROR(2001, "SQL compilation error, syntax incorrect"),
    SQL_PLAN_ERROR(2101, "SQL plan error, cannot generate execution plan"),
    SQL_EXECUTOR_ERROR(2201, "SQL executor error, cannot generate executor"),
    STREAM_TABLE_ERROR(3000, "Stream table related error"),
    RULE_ERROR(4000, "Rule related error"),
    CONFIGURATION_ERROR(5000, "Configuration related error"),
    UNKNOWN(-1, "Unknown error");
    
    private final int code;
    private final String description;
    
    EKuiperErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * Find the error code enum by its numeric value.
     *
     * @param code The numeric error code
     * @return The matching EKuiperErrorCode or UNKNOWN if not found
     */
    public static EKuiperErrorCode fromCode(int code) {
        for (EKuiperErrorCode errorCode : values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        return UNKNOWN;
    }
} 