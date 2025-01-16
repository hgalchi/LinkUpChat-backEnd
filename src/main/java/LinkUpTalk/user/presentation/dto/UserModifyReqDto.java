package LinkUpTalk.user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModifyReqDto {

    @Size(min = 2, message = "이름은 2자리 이상 입력해주세요")
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @Email
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;
}
