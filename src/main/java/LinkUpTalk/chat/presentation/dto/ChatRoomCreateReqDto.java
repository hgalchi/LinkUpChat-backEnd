package LinkUpTalk.chat.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChatRoomCreateReqDto {

    @NotBlank(message = "유저식별자는 필수 입력값입니다.")
    private Long userId;

    @Size(min = 1, max =20 ,message="1~20자리로 입력해주세요")
    @NotBlank(message = "채팅방 이름은 필수 입력값입니다.")
    private String name;

    @Min(3)@Max(10)
    @NotBlank(message = "최대인원 수는 필수 입력값입니다.")
    private int capacity;
}
