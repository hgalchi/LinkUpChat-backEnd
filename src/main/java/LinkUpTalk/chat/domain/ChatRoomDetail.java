package LinkUpTalk.chat.domain;

import LinkUpTalk.chat.domain.constant.ChatRoomRoleType;
import LinkUpTalk.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@Where(clause = "deleted = false")
public class ChatRoomDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatroom_id",nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ChatRoomRoleType role;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted=false;


    public static ChatRoomDetail of(ChatRoom chatRoom, User user, ChatRoomRoleType chatRoomRole) {
        return ChatRoomDetail.builder()
                .chatRoom(chatRoom)
                .user(user)
                .role(chatRoomRole)
                .build();
    }

    void delete(){
        this.isDeleted = true;
    }
}
