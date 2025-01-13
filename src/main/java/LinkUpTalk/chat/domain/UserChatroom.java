package LinkUpTalk.chat.domain;

import LinkUpTalk.chat.domain.constant.ChatRoomRole;
import LinkUpTalk.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserChatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatroom_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ChatRoomRole role;

    public static UserChatroom of(ChatRoom chatRoom, User user, ChatRoomRole chatRoomRole) {
        return UserChatroom.builder()
                .chatRoom(chatRoom)
                .user(user)
                .role(chatRoomRole)
                .build();
    }
}
