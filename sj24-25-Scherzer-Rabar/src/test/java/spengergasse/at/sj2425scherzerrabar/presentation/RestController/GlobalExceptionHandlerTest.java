package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolationException;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleParseExceptions_ShouldReturnBadRequest() {
        ParseException exception = new ParseException("Invalid date format", 0);
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleParseExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Parse Error", response.getBody().getTitle());
        assertEquals("Invalid date format", response.getBody().getDetail());
    }

    @Test
    void handleBadRequestException_ShouldReturnBadRequest() {
        BadRequestException exception = new BadRequestException("Invalid request");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleHttpStatusCodeException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().getTitle());
        assertEquals("Invalid request", response.getBody().getDetail());
    }

    @Test
    void handleDataIntegrityViolationException_ShouldReturnConflict() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Duplicate entry");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleDataIntegrityViolationException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Data Integrity Violation", response.getBody().getTitle());
        assertEquals("Duplicate entry or constraint violation.", response.getBody().getDetail());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        BindException bindException = new BindException(new Object(), "testObject");
        bindException.addError(new FieldError("testObject", "fieldName", "must not be null"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindException);
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains("fieldName: must not be null"));
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequest() {
        ConstraintViolationException exception = new ConstraintViolationException("Invalid input", null);
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleConstraintViolationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Constraint Violation", response.getBody().getTitle());
        assertEquals("Invalid input", response.getBody().getDetail());
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbidden() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleAccessDeniedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied", response.getBody().getTitle());
        assertEquals("You are not allowed to access this resource.", response.getBody().getDetail());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error occurred");
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleThrowable(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getTitle());
        assertEquals("Something unexpected went wrong! We are currently hackeling, don't worry! ('be happy :)')",
                response.getBody().getDetail());
    }
}
