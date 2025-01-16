package LinkUpTalk.auth.domain.Repository;

import LinkUpTalk.auth.domain.Group;
import LinkUpTalk.auth.infrastructor.JpaGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
//todo : 모놀리식 멀티 모듈로 구조 변경해보기
@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, JpaGroupRepository {
}
