package LinkUpTalk.common.exception;

import LinkUpTalk.common.response.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ResponseCode errorcode;

    public BusinessException(ResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorcode = errorCode;
    }

    public BusinessException(ResponseCode errorCode, String message) {
        super(message);
        this.errorcode = errorCode;
    }
}
