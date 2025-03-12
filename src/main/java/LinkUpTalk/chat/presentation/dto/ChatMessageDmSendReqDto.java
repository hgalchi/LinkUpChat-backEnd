package LinkUpTalk.chat.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class ChatMessageDmSendReqDto {

    @NotBlank(message = "수신자 식별자는 필수 입력값입니다.")
    Long receiverId;

    @NotBlank
    String content;
}
