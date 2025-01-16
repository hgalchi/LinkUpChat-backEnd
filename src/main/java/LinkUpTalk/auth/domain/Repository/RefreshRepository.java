package LinkUpTalk.auth.domain.Repository;

import LinkUpTalk.auth.domain.Refresh;
import LinkUpTalk.auth.infrastructor.JpaRefreshRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh, Long>, JpaRefreshRepository {

}
