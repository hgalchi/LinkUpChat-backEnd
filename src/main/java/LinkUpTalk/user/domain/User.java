package LinkUpTalk.user.domain;

import LinkUpTalk.auth.domain.Group;
import LinkUpTalk.chat.domain.UserChatroom;
import LinkUpTalk.user.dto.UserGetResDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Where(clause = "deleted = false")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    //todo : 유니크 속성 추가
    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private boolean deleted = false;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Group> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserChatroom> chatRooms = new HashSet<>();

    public static User of(String name, String password, String email){
        return User.builder()
                .name(name)
                .password(password)
                .email(email)
                .build();
    }

    public UserGetResDto toDto() {
        return UserGetResDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .groups(roles.stream()
                        .map(Group::getName).toList())
                .build();
    }
    public void addUserGroups(Group group) {
        roles.add(group);
    }

    public void encodePassword(String encodePassword) {
        this.password = encodePassword;
    }

    public void update(String name, String email) {
        this.name=name;
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.deleted=true;
    }
}
