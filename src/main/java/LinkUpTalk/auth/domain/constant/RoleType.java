package LinkUpTalk.auth.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleType {
    CUSTOMER("ROLE_CUSTOMER"),
    AMDIN("ROLE_ADMIN");

    private String role;
}
