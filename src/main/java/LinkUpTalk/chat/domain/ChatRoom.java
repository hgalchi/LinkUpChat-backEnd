package LinkUpTalk.chat.domain;

import LinkUpTalk.chat.domain.constant.ChatRoomType;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.common.domain.BaseEntity;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@Where(clause = "is_deleted = false")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private int participantCount=0;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ChatRoomType chatRoomType;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private Set<ChatRoomDetail> participants = new HashSet<>();

    public static ChatRoom ofGroup(String name, int capacity) {
        return ChatRoom.builder()
                .name(name)
                .capacity(capacity)
                .chatRoomType(ChatRoomType.GROUP)
                .build();
    }

    public static ChatRoom ofDm() {
        return ChatRoom.builder()
                .capacity(2)
                .chatRoomType(ChatRoomType.DM)
                .build();
    }

    public ChatroomGetResDto toDto(){
        return ChatroomGetResDto.builder()
                .id(id)
                .name(name)
                .participantCount(participantCount)
                .capacity(capacity)
                .participants(participants.stream().map(m->m.getUser().getName()).toList())
                .build();
    }

    public void addUser(ChatRoomDetail chatroomDetail) {
        participants.add(chatroomDetail);
        participantCount++;
    }

    public void removeUser(ChatRoomDetail chatroomDetail) {
        participants.remove(chatroomDetail);
        participantCount--;
    }

    public void update(String name, int capacity) {
        if (this.participantCount >capacity) {
            throw new BusinessException(ResponseCode.INVALID_INPUT_VALUE);
        }
        this.name = name;
        this.capacity = capacity;
    }

    public void delete() {
        this.isDeleted =true;
        this.deleteEntity(LocalDateTime.now());
        this.participants.forEach(ChatRoomDetail::delete);
    }
}
