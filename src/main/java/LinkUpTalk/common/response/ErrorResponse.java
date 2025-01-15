package LinkUpTalk.common.response;

import ch.qos.logback.core.spi.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse  {

    private Integer code;
    private String message;
    private List<Object> errors;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class FieldError {
        private String field;
        private String value;
        private String reason;

        public FieldError of(org.springframework.validation.FieldError fieldError) {
            this.field = fieldError.getField();
            this.value =  fieldError.getRejectedValue().toString();
            this.reason = fieldError.getDefaultMessage();
            return this;
        }
    }

    private ErrorResponse(ResponseCode code,BindingResult bindingResult) {
        this.code = code.getStatus().value();
        this.message = code.getMessage();
        this.errors = bindingResult.getFieldErrors().stream()
                .map(f -> new FieldError().of(f))
                .collect(Collectors.toList());
    }

    private ErrorResponse(ResponseCode code) {
        this.code = code.getStatus().value();
        this.message = code.getMessage();
    }

    public static ErrorResponse of(ResponseCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(errorCode, bindingResult);
    }

    public static ErrorResponse of(ResponseCode errorCode) {
        return new ErrorResponse(errorCode);
    }


}

