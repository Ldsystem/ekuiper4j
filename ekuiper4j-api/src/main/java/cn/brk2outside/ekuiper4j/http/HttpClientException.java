package cn.brk2outside.ekuiper4j.http;

import lombok.Getter;

/**
 * Exception thrown when HTTP requests fail.
 */
public class HttpClientException extends RuntimeException {

    private final int statusCode;
    
    @Getter
    private final EKuiperErrorResponse eKuiperError;

    /**
     * Creates an exception with a message and status code.
     *
     * @param message    The error message
     * @param statusCode The HTTP status code (-1 if not applicable)
     */
    public HttpClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.eKuiperError = null;
    }

    /**
     * Creates an exception with a message, cause, and status code.
     *
     * @param message    The error message
     * @param cause      The cause of the exception
     * @param statusCode The HTTP status code (-1 if not applicable)
     */
    public HttpClientException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
        this.eKuiperError = null;
    }
    
    /**
     * Creates an exception with a message, cause, status code, and eKuiper error response.
     *
     * @param message      The error message
     * @param cause        The cause of the exception
     * @param statusCode   The HTTP status code (-1 if not applicable)
     * @param ekuiperError The eKuiper error response object
     */
    public HttpClientException(String message, Throwable cause, int statusCode, EKuiperErrorResponse ekuiperError) {
        super(ekuiperError != null ? ekuiperError.getFormattedErrorMessage() : message, cause);
        this.statusCode = statusCode;
        this.eKuiperError = ekuiperError;
    }

    /**
     * Creates a timeout exception.
     *
     * @param message The error message
     * @param cause   The cause of the timeout
     * @return A new HttpClientException representing a timeout
     */
    public static HttpClientException timeout(String message, Throwable cause) {
        return new HttpClientException("Request timed out: " + message, cause, -1);
    }
    
    /**
     * Creates an exception specific to eKuiper API errors.
     *
     * @param errorResponse The parsed eKuiper error response
     * @param statusCode    The HTTP status code
     * @return A new HttpClientException representing an eKuiper API error
     */
    public static HttpClientException ekuiperError(EKuiperErrorResponse errorResponse, int statusCode) {
        return new HttpClientException(
                "eKuiper API Error", 
                null, 
                statusCode,
                errorResponse
        );
    }

    /**
     * Gets the HTTP status code associated with this exception.
     *
     * @return The HTTP status code, or -1 if not applicable
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Checks if this exception contains an eKuiper-specific error.
     *
     * @return True if this exception has eKuiper error information
     */
    public boolean hasEKuiperError() {
        return eKuiperError != null;
    }
    
    /**
     * Checks if this exception represents a specific eKuiper error type.
     *
     * @param errorCode The error code to check against
     * @return True if this exception matches the provided error code
     */
    public boolean isEKuiperErrorType(EKuiperErrorCode errorCode) {
        return hasEKuiperError() && eKuiperError.isErrorType(errorCode);
    }
} 