package LinkUpTalk.common.exception;

import LinkUpTalk.common.response.ErrorResponse;
import LinkUpTalk.common.response.ResponseCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.ElementCollection;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.security.SignatureException;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage());

        ErrorResponse response = ErrorResponse.of(ResponseCode.INVALID_INPUT_VALUE, ex.getBindingResult());
        return ResponseEntity.status(status)
                .body(response);
    }

    @Override
    @ResponseStatus
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage());

        ErrorResponse response = ErrorResponse.of(ResponseCode.METHOD_NOT_ALLOW);
        return ResponseEntity.status(status)
                .body(response);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex, WebRequest request) {
        log.error(ex.getMessage());

        ErrorResponse response = ErrorResponse.of(ResponseCode.SIGNATURE_JWT);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
        log.error(ex.getMessage());

        ErrorResponse response = ErrorResponse.of(ResponseCode.MALFORMED_JWT);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        log.error(ex.getMessage());
        ErrorResponse response = ErrorResponse.of(ResponseCode.EXPIRED_JWT);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeResponse(AccessDeniedException ex) {
        log.error(ex.getMessage());
        ErrorResponse response = ErrorResponse.of(ResponseCode.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error(ex.getMessage());

        ErrorResponse response = ErrorResponse.of(ex.getErrorcode());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error(ex.getMessage());
        return ErrorResponse.of(ResponseCode.SYSTEM_ERROR);
    }

    @MessageExceptionHandler(BusinessException.class)
    public void handleMessageBusinessException(Message<?> message) {
        log.error("Error occurred : {}", message);
    }
}
