package com.example.security.common.dto;

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
public class UserPasswordModifyReqDto {

    @Size(min = 8, max = 16, message = "비밀번호는 8~16자리로 입력해주세요")
    @NotBlank(message = "기본 비밀번호는 필수 입력값입니다.")
    private String password;

    @Size(min = 8, max = 16, message = "비밀번호는 8~16자리로 입력해주세요")
    @NotBlank(message = "변경 비밀번호는 필수 입력값입니다.")
    private String newPassword;
}
