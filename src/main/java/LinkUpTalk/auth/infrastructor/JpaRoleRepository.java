package LinkUpTalk.auth.infrastructor;

import LinkUpTalk.auth.domain.Repository.RoleRepository;
import LinkUpTalk.auth.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//todo : 모놀리식 멀티 모듈로 구조 변경해보기
@Repository
public interface JpaRoleRepository extends JpaRepository<Roles, Long>, RoleRepository {
}
