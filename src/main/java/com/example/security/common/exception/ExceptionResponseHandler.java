package com.example.security.common.exception;

import com.example.security.common.codes.ErrorResponse;
import com.example.security.common.codes.ResponseCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.SignatureException;


@RestControllerAdvice
@Log4j2
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    /*
    @Valid 유효성 검증 exception 시 발생
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.error("handlerInvalideExcecption", ex);
        ErrorResponse response = new ErrorResponse(ResponseCode.INVALID_INPUT_VALUE, ex.getBindingResult());
        return ResponseEntity.status(status)
                .body(response);
    }

    /*
    지원하지않는 HTTP Method 호출 시 발생
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("handleHttpRequestMethodNotsuportedException", ex);
        ErrorResponse response = new ErrorResponse(ResponseCode.METHOD_NOT_ALLOW);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
    JWT 토큰이 유효하지 않을 경우 발생
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex, WebRequest request) {
        logger.error("handleSignatureException", ex);
        ErrorResponse response = new ErrorResponse(ResponseCode.SIGNATURE_JWT);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

   //NotFoundException

    /*
    JWT 토큰이 변조되었을 경우 발생
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
        logger.error("handleMalformedJwtException", ex);
        ErrorResponse response = new ErrorResponse(ResponseCode.MALFORMED_JWT);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /*
    JWT 토큰이 만료되었을 경우 발생
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        logger.error("handleExpiredJwtException", ex);
        ErrorResponse response = new ErrorResponse(ResponseCode.EXPIRED_JWT);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /*
    요구사항에 맞지 않은 경우 발생
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException (BusinessException ex) {
        log.error("handleBusinessException", ex);
        ResponseCode errorCode = ex.getErrorcode();
        ErrorResponse errorResponse = ex.getMessage() == null ? new ErrorResponse(ex.getErrorcode()) :
                new ErrorResponse(ex.getErrorcode(), ex.getMessage());

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
