package LinkUpTalk.user.infrastructor;

import LinkUpTalk.auth.domain.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    Boolean existsByRefresh(String refresh);

    void deleteByRefresh(String refresh);
}
