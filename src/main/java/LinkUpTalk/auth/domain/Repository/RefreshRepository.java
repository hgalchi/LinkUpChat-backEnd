package LinkUpTalk.auth.domain.Repository;

import LinkUpTalk.auth.domain.Refresh;

public interface RefreshRepository {

    Boolean existsByRefresh(String refresh);

    void deleteByRefresh(String refresh);

    Refresh save(Refresh refresh);

    void flush();
}
