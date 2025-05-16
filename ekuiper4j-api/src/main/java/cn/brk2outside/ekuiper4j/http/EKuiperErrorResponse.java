package cn.brk2outside.ekuiper4j.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an error response from the eKuiper API.
 * Sample error response:
 * {"error":1003,"message":"found error when connecting for tcp://localhost:1883..."}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EKuiperErrorResponse {
    
    @JsonProperty("error")
    private int errorCode;
    
    @JsonProperty("message")
    private String message;
    
    /**
     * Get the typed error code enum from the numeric error code.
     *
     * @return The corresponding EKuiperErrorCode
     */
    public EKuiperErrorCode getErrorCodeEnum() {
        return EKuiperErrorCode.fromCode(errorCode);
    }
    
    /**
     * Check if this response represents a specific error type.
     *
     * @param errorCode The error code to check against
     * @return True if this error matches the provided error code
     */
    public boolean isErrorType(EKuiperErrorCode errorCode) {
        return this.errorCode == errorCode.getCode();
    }
    
    /**
     * Format the error message with error code information for better debugging.
     *
     * @return A formatted error message string
     */
    public String getFormattedErrorMessage() {
        EKuiperErrorCode code = getErrorCodeEnum();
        return String.format("[%d - %s] %s", 
                code.getCode(), 
                code.getDescription(), 
                this.message);
    }
} 