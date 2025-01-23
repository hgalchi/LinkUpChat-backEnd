package LinkUpTalk.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SucResponse<T> {

    //todo : code값을 따로 보내기
    private T data;
    private String message;

    public SucResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public static <T> ResponseEntity<SucResponse<T>> ok(ResponseCode code,T data) {
        return ResponseEntity.ok(new SucResponse<>(data, code.getMessage()));
    }

    public static <T> ResponseEntity<SucResponse<T>> ok(ResponseCode code) {
        return ResponseEntity.ok(new SucResponse<>(null, code.getMessage()));
    }

}
