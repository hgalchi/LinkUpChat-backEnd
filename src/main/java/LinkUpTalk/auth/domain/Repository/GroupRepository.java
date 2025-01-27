package LinkUpTalk.auth.domain.Repository;

import LinkUpTalk.auth.domain.Roles;
import LinkUpTalk.auth.infrastructor.JpaGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//todo : 모놀리식 멀티 모듈로 구조 변경해보기
@Repository
public interface GroupRepository extends JpaRepository<Roles, Long>, JpaGroupRepository {
}
