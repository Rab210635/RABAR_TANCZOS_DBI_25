package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import spengergasse.at.sj2425scherzerrabar.service.*;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(LoggingController.class);


    @ExceptionHandler(BookService.BookServiceException.class)
    public ResponseEntity<ProblemDetail> handleBookServiceException(BookService.BookServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleBookServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Book Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorService.AuthorServiceException.class)
    public ResponseEntity<ProblemDetail> handleAuthorServiceException(AuthorService.AuthorServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleAuthorServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Author Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BorrowingService.BorrowingServiceException.class)
    public ResponseEntity<ProblemDetail> handleBorrowingServiceException(BorrowingService.BorrowingServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleBorrowingServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Borrowing Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BranchService.BranchServiceException.class)
    public ResponseEntity<ProblemDetail> handleBranchServiceException(BranchService.BranchServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleBranchServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Branch Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BuyableBookService.BuyableBookServiceException.class)
    public ResponseEntity<ProblemDetail> handleBuyableBookServiceException(BuyableBookService.BuyableBookServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleBuyableBookServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("BuyableBook Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CopyService.CopyServiceException.class)
    public ResponseEntity<ProblemDetail> handleCopyServiceException(CopyService.CopyServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleCopyServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Copy Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerService.CustomerServiceException.class)
    public ResponseEntity<ProblemDetail> handleCustomerServiceException(CustomerService.CustomerServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleCustomerServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Customer Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LibraryService.LibraryServiceException.class)
    public ResponseEntity<ProblemDetail> handleLibraryServiceException(LibraryService.LibraryServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleLibraryServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Library Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderService.OrderServiceException.class)
    public ResponseEntity<ProblemDetail> handleOrderServiceException(OrderService.OrderServiceException ex) {
        logger.debug("entererd globalexceptionhandler handleOrderServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Order Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PublisherService.PublisherServiceException.class)
    public ResponseEntity<ProblemDetail> handlePublisherServiceException(PublisherService.PublisherServiceException ex) {
        logger.debug("entererd globalexceptionhandler handlePublisherServiceException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Publisher Service Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ParseException.class)
    public ResponseEntity<ProblemDetail> handleParseExceptions(ParseException ex){
        logger.debug("entererd globalexceptionhandler handleParseExceptions");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Parse Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleHttpStatusCodeException(BadRequestException ex){
        logger.debug("entererd globalexceptionhandler handleHttpStatusCodeException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request");
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.debug("entererd globalexceptionhandler handleDataIntegrityViolationException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Duplicate entry or constraint violation.");
        problemDetail.setTitle("Data Integrity Violation");
        return new ResponseEntity<>(problemDetail, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        logger.debug("entererd globalexceptionhandler handleValidationException");
        String errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorDetails);
        problemDetail.setTitle("Validation Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.debug("entererd globalexceptionhandler handleConstraintViolationException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Constraint Violation");
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException ex) {
        logger.debug("entererd globalexceptionhandler handleAccessDeniedException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You are not allowed to access this resource.");
        problemDetail.setTitle("Access Denied");
        return new ResponseEntity<>(problemDetail, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFoundException(NoResourceFoundException ex) {
        logger.debug("entererd globalexceptionhandler noResourceFoundException");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "This resource does not exist.");
        problemDetail.setTitle("Not Found");
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ProblemDetail> handleThrowable(Throwable ex) {
        logger.debug("entererd globalexceptionhandler handleThrowable");
        logger.error("Fehler: "+ex.getMessage());
        Arrays.stream(ex.getStackTrace()).map((trace)->{
            logger.trace(trace.toString());
            return null;
        });
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Something unexpected went wrong! We are currently hackeling, don't worry! ('be happy :)')");
        problemDetail.setTitle("Internal Server Error");
        return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
