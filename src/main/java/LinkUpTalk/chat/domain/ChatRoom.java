package LinkUpTalk.chat.domain;

import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Where(clause = "deleted = false")
public class ChatRoom {

    //todo : 어노테이션 정리
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int count;

    private int maxCount;

    @Builder.Default
    private boolean isDeleted=false;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY,  cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserChatroom> members = new HashSet<>();

    public ChatroomGetResDto toDto(){
        return ChatroomGetResDto.builder()
                .id(id)
                .name(name)
                .curCount(count)
                .maxCount(maxCount)
                .members(members.stream().map(m->m.getUser().getName()).toList())
                .build();
    }

    public void addUser(UserChatroom userChatroom) {
        //todo : 이미 등록되어 있는 사람인 경우 확인하기
        members.add(userChatroom);
    }
    public void removeDetail(UserChatroom userChatroom) {
        members.remove(userChatroom);
    }

    public void setUserCount(int userCount) {

        this.count = userCount;
    }

    //todo : 연관관계 제거
    public void delete() {
        this.isDeleted=true;
    }

    public void updateMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
