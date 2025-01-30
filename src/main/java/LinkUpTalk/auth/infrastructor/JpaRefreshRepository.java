package LinkUpTalk.auth.infrastructor;

import LinkUpTalk.auth.domain.Refresh;
import LinkUpTalk.auth.domain.Repository.RefreshRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRefreshRepository extends JpaRepository<Refresh, Long>, RefreshRepository {

}
