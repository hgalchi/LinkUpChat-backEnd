package LinkUpTalk.chat.infrastructor.config;

import LinkUpTalk.chat.infrastructor.jpa.ExtendedRepositoryImpl;
import LinkUpTalk.chat.infrastructor.mongo.MongoChatMessageRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "LinkUpTalk",
        repositoryBaseClass = ExtendedRepositoryImpl.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = MongoChatMessageRepository.class
        )
)
public class JpaConfig {
}
