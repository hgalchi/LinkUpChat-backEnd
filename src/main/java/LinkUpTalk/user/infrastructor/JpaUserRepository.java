package LinkUpTalk.user.infrastructor;

import LinkUpTalk.chat.infrastructor.jpa.ExtendedRepository;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface JpaUserRepository extends ExtendedRepository<User, Long>, UserRepository {

    default User readByEmail(String email) {
        return findByEmail(email).orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
    }
}
