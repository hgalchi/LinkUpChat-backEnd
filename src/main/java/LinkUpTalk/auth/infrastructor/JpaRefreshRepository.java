package LinkUpTalk.auth.infrastructor;

public interface JpaRefreshRepository {

    Boolean existsByRefresh(String refresh);

    void deleteByRefresh(String refresh);
}
