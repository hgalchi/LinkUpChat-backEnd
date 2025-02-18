package LinkUpTalk.user.domain;

import LinkUpTalk.auth.domain.Roles;
import LinkUpTalk.common.domain.BaseEntity;
import LinkUpTalk.chat.domain.ChatRoomDetail;
import LinkUpTalk.user.presentation.dto.UserGetResDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Where(clause = "isDeleted = false")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private boolean isDeleted = false;

    @ManyToMany(cascade = {CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @Builder.Default
    private Set<ChatRoomDetail> chatRooms = new HashSet<>();

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
                        .map(Roles::getName).toList())
                .build();
    }
    public void addRole(Roles roles) {
        this.roles.add(roles);
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
        this.isDeleted=true;
        this.deleteEntity(LocalDateTime.now());
        this.chatRooms.forEach(ChatRoomDetail::delete);
    }
}
