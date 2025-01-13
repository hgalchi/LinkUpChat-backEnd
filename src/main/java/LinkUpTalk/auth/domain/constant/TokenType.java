package LinkUpTalk.auth.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

    refreshToken,
    accessToken;
}
