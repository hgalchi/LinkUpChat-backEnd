package LinkUpTalk.common.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class SuccResponse<T> {

    private Map<String, Object> header = new HashMap<>();
    private T data;
    private String message;

    public SuccResponse(ResponseCode code, T data) {
        header.put("status", code.getStatus());
        header.put("message", code.name());
        this.data = data;
        this.message = code.getMessage();
    }

}
