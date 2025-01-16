package LinkUpTalk.user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateReqDto {

    @Size(min = 2, message = "이름은 2자리 이상 입력해주세요")
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @Size(min = 8, max = 16, message = "비밀번호는 8~16자리로 입력해주세요")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @Email
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

}
