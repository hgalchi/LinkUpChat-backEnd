package LinkUpTalk.user.infrastructor;

import LinkUpTalk.user.domain.User;
import LinkUpTalk.user.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

}
