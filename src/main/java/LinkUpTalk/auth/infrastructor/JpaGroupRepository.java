package LinkUpTalk.auth.infrastructor;

import LinkUpTalk.auth.domain.Roles;

import java.util.Optional;

public interface JpaGroupRepository {
    Optional<Roles> findByCode(String code);

}
