package LinkUpTalk.user.application;

import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserResourceAuthService {

    private final UserRepository userRepository;

    public boolean isResourceOwner(Authentication authentication, Long userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
        return authentication.getName().equals(user.getEmail());
    }

}
