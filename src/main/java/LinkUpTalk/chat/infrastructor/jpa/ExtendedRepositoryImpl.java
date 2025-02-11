package LinkUpTalk.chat.infrastructor.jpa;

import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.StringUtils;

import java.io.Serializable;

public class ExtendedRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ExtendedRepository<T, ID> {

    private final EntityManager entityManager;

    public ExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }
    @Override
    public T read(ID id) {
        Class<T> domainType = getDomainClass();
        T result = entityManager.find(domainType, id);

        if (result == null) {
            System.out.println("찾지 못함.");
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return result;
    }

    private String getClassName(){
        Class<T> domainType = getDomainClass();
        return StringUtils.capitalize(domainType.getSimpleName())
                .replaceAll("(.)(\\p{javaUpperCase})", "$1_$2")
                .toUpperCase();
    }
}
