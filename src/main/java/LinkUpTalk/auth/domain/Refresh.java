package LinkUpTalk.auth.domain;

import LinkUpTalk.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 1024)
    private String refresh;

    private String expiration;

    public void update(String refresh, String expiration) {
        this.refresh = refresh;
        this.expiration = expiration;
    }
}
