package LinkUpTalk.auth.infrastructor;

import LinkUpTalk.auth.domain.Group;

import java.util.Optional;

public interface JpaGroupRepository {
    Optional<Group> findByCode(String code);

}
