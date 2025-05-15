package cn.brk2outside.ekuiper4j.http;

/**
 * Exception thrown when HTTP requests fail.
 */
public class HttpClientException extends RuntimeException {

    private final int statusCode;

    /**
     * Creates an exception with a message and status code.
     *
     * @param message    The error message
     * @param statusCode The HTTP status code (-1 if not applicable)
     */
    public HttpClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
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
     * Gets the HTTP status code associated with this exception.
     *
     * @return The HTTP status code, or -1 if not applicable
     */
    public int getStatusCode() {
        return statusCode;
    }
} 