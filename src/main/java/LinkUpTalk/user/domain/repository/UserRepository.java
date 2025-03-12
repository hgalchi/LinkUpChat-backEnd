package LinkUpTalk.user.domain.repository;

import LinkUpTalk.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface UserRepository{

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByEmail(String email);

    User readByEmail(String email);

    User read(Long id);

    boolean existsByEmail(String email);

    Optional<User> findById(Long userId);

    User save(User user);

}
