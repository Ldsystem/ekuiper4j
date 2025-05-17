package cn.brk2outside.ekuiper4j.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Tests for the enhanced error handling in RestTemplateHttpClient.
 */
public class ErrorHandlingTest extends BaseEKuiperTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private RestTemplateHttpClient mockClient;
    private RestTemplateHttpClient realClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create a client with mock RestTemplate for unit tests
        mockClient = new RestTemplateHttpClient("localhost", 9081, mockRestTemplate);
        
        // Create a client using the real container for integration tests
        realClient = new RestTemplateHttpClient(
                EKUIPER.getEkuiperHost(), 
                EKUIPER.getEkuiperPort());
    }

    // Unit tests with mocked RestTemplate
    
    @Test
    public void testIOErrorHandlingMocked() {
        // Prepare error response JSON
        Map<String, Object> body = Map.of(
                "error", 1003,
                "message", "found error when connecting for tcp://localhost:1883: network Error : dial tcp 127.0.0.1:1883: connect: connection refused"
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create a ResponseEntity with Bad Request status
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(body);
        
        // Mock RestTemplate to return the error response - using exact parameter matcher
        when(mockRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET), 
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(responseEntity);
        
        // Invoke client and expect exception with proper error code
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            mockClient.get("/mqtt/sources", new ParameterizedTypeReference<Map<String, Object>>() {});
        });
        
        // Assert eKuiper error is present and correctly parsed
        assertTrue(exception.hasEKuiperError());
        assertNotNull(exception.getEKuiperError());
        assertEquals(1003, exception.getEKuiperError().getErrorCode());
        assertTrue(exception.isEKuiperErrorType(EKuiperErrorCode.IO_ERROR));
        assertTrue(exception.getMessage().contains("IO error in Source/Sink"));
    }
    
    @Test
    public void testResourceNotFoundErrorHandlingMocked() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create a ResponseEntity with Not Found status
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(headers)
                .body(Map.of(
                        "error", 1002,
                        "message", "Resource /rules/non_existent not found"
                )); // Using null body as the RestTemplate would normally do
        
        // Mock RestTemplate to return the error response
        when(mockRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET), 
                any(HttpEntity.class), 
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(responseEntity);
        
        // Invoke client and expect exception with proper error code
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            mockClient.get("/rules/non_existent", new ParameterizedTypeReference<Map<String, Object>>() {});
        });
        
        // Assert error gets proper status code
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
    }
    
    @Test
    public void testSQLCompilationErrorHandlingMocked() {
        // Prepare error response JSON
        String errorJson = "{\"error\":2001,\"message\":\"SQL syntax error in 'SELECT * FORM stream'\"}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create a ResponseEntity with Internal Server Error status
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(headers)
                .body(Map.of("error", 2001, "message", "SQL syntax error in 'SELECT * FORM stream'"));
        
        // Mock RestTemplate to return the error response
        when(mockRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST), 
                any(HttpEntity.class), 
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(responseEntity);
        
        // Invoke client and expect an exception for SQL compilation error
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            mockClient.post("/rules/new_rule", 
                    Map.of("sql", "SELECT * FORM stream"), 
                    new ParameterizedTypeReference<Map<String, Object>>() {});
        });
        
        // Assert eKuiper error is present and correctly parsed
        assertTrue(exception.hasEKuiperError());
        assertNotNull(exception.getEKuiperError());
        assertEquals(2001, exception.getEKuiperError().getErrorCode());
        assertTrue(exception.isEKuiperErrorType(EKuiperErrorCode.SQL_COMPILATION_ERROR));
        assertTrue(exception.getMessage().contains("SQL compilation error"));
    }
    
    @Test
    public void testNonEKuiperErrorMocked() {
        // Prepare error response that is not an eKuiper error
        Map<String, Object> errorBody = Map.of("status", "error", "message", "Something went wrong");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create a ResponseEntity with Bad Request status
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(errorBody);
        
        // Mock RestTemplate to return the error response
        when(mockRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET), 
                any(HttpEntity.class), 
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(responseEntity);
        
        // Invoke client and expect a standard exception (not eKuiper error)
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            mockClient.get("/something", new ParameterizedTypeReference<Map<String, Object>>() {});
        });
        // Assert this is handled as a regular error (not eKuiper-specific)
        assertFalse(exception.hasEKuiperError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Something went wrong"));
    }
    
    // Integration tests with real eKuiper container
    
    @Test
    public void testResourceNotFoundWithRealContainer() {
        // Generate a unique non-existent rule name
        String nonExistentRule = "test_rule_" + UUID.randomUUID().toString().replace("-", "");
        
        // Try to get a non-existent rule, which should trigger a 1002 Resource not found error
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            realClient.get("/rules/" + nonExistentRule, new ParameterizedTypeReference<Map<String, Object>>() {});
        });
        
        // Verify the error is properly handled
        assertTrue(exception.hasEKuiperError(), "Expected an eKuiper error");
        assertNotNull(exception.getEKuiperError());
        assertTrue(exception.isEKuiperErrorType(EKuiperErrorCode.RESOURCE_NOT_FOUND), 
                "Expected a RESOURCE_NOT_FOUND error but got: " + 
                (exception.hasEKuiperError() ? exception.getEKuiperError().getErrorCodeEnum() : "non-eKuiper error"));
    }

} 