package LinkUpTalk.auth.domain.Repository;

import LinkUpTalk.auth.domain.Roles;

import java.util.Optional;

public interface RoleRepository {
    Optional<Roles> findByCode(String code);

}
