package LinkUpTalk.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse  {

    private Map<String, Object> header=new HashMap<>();
    private List<Object> errors;
    private String message;

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

    public ErrorResponse (ResponseCode errorCode, BindingResult e) {
        header.put("status", errorCode.getStatus());
        header.put("message", errorCode.name());
        this.errors=e.getFieldErrors().stream()
                .map(f->new FieldError().of(f))
                .collect(Collectors.toList());
        this.message = errorCode.getMessage();

    }

    public ErrorResponse(ResponseCode errorCode, String descrption) {
        header.put("status", errorCode.getStatus());
        header.put("message", errorCode.name());
        this.errors = Arrays.asList(descrption);
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ResponseCode errorCode) {
        header.put("status", errorCode.getStatus());
        header.put("message", errorCode.name());
        this.errors = new ArrayList<>();
        this.message = errorCode.getMessage();
    }

}

