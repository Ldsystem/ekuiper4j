package cn.brk2outside.ekuiper4j.http.examples;

import cn.brk2outside.ekuiper4j.http.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

import static cn.brk2outside.ekuiper4j.http.EKuiperErrorCode.*;

/**
 * Example class demonstrating how to use the enhanced error handling capabilities
 * with eKuiper API error codes.
 */
public class ErrorHandlingExample {

    /**
     * Demonstrates how to handle different eKuiper error types.
     */
    public static void main(String[] args) {
        // Create HTTP client
        HttpClient client = new RestTemplateHttpClient("localhost", 9081);
        
        try {
            // Try to access a resource or perform an operation
            // This would typically trigger an API call that could return an error
            Map<String, Object> result = client.get("/mqtt/stream", 
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
            System.out.println("Operation succeeded: " + result);
            
        } catch (HttpClientException e) {
            // Handle the exception with enhanced error information
            if (e.hasEKuiperError()) {
                EKuiperErrorResponse error = e.getEKuiperError();
                EKuiperErrorCode errorCode = error.getErrorCodeEnum();
                
                System.out.println("eKuiper Error: " + error.getFormattedErrorMessage());
                
                // Special handling based on error type
                if (e.isEKuiperErrorType(IO_ERROR)) {
                    System.out.println("IO Error detected. Check connection parameters and MQTT broker status.");
                } else if (e.isEKuiperErrorType(EKuiperErrorCode.RESOURCE_NOT_FOUND)) {
                    System.out.println("Resource not found. Check if the stream exists.");
                } else if (e.isEKuiperErrorType(SQL_COMPILATION_ERROR)) {
                    System.out.println("SQL syntax error. Please review your SQL statement.");
                } else {
                    System.out.println("Unexpected eKuiper error: " + errorCode.getDescription());
                }
            } else {
                // Standard HTTP exception handling
                System.out.println("Error: " + e.getMessage());
                System.out.println("Status code: " + e.getStatusCode());
            }
        }
    }
    
    /**
     * Example of how to use try-with-resources with different error handling strategies.
     */
    public static void exampleWithErrorHandlingStrategies() {
        HttpClient client = new RestTemplateHttpClient("localhost", 9081);
        
        try {
            // Attempt to create a rule
            client.post("/rules/my_rule", 
                    Map.of("sql", "SELECT * FROM stream", "actions", List.of()), 
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            
        } catch (HttpClientException e) {
            if (e.hasEKuiperError()) {
                switch (e.getEKuiperError().getErrorCodeEnum()) {
                    case IO_ERROR:
                        // Handle IO errors - maybe retry with backoff
                        handleIOError(e);
                        break;
                    case SQL_COMPILATION_ERROR:
                    case SQL_PLAN_ERROR:
                    case SQL_EXECUTOR_ERROR:
                        // Handle SQL-related errors
                        handleSQLError(e);
                        break;
                    case CONFIGURATION_ERROR:
                        // Handle configuration errors
                        handleConfigError(e);
                        break;
                    default:
                        // Generic error handling
                        System.err.println("Unexpected error: " + e.getMessage());
                        break;
                }
            } else {
                // Handle standard HTTP errors
                System.err.println("HTTP Error: " + e.getMessage());
            }
        }
    }
    
    private static void handleIOError(HttpClientException e) {
        System.err.println("IO Error occurred: " + e.getMessage());
        // Implement retry logic, connection verification, etc.
    }
    
    private static void handleSQLError(HttpClientException e) {
        System.err.println("SQL Error occurred: " + e.getMessage());
        // Log detailed SQL error information, suggest fixes, etc.
    }
    
    private static void handleConfigError(HttpClientException e) {
        System.err.println("Configuration Error occurred: " + e.getMessage());
        // Check configuration settings, permissions, etc.
    }
} 