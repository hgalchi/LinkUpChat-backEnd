package LinkUpTalk.chat.domain;

import LinkUpTalk.annotation.UnitTest;
import LinkUpTalk.common.exception.BusinessException;
import LinkUpTalk.common.response.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ChatRoomTest {

    private ChatRoom chatRoom;

    @BeforeEach
    void setUp(){
        chatRoom = ChatRoom.ofGroup("test room", 10);
    }

    @UnitTest
    @DisplayName("채팅방에 유저를 추가하면 참가자 수가 증가해야한다.")
    void addUser_suc(){
        //given
        ChatRoomDetail chatRoomDetail = mock(ChatRoomDetail.class);

        //when
        chatRoom.addUser(chatRoomDetail);

        //then
        assertThat(chatRoom.getParticipantCount()).isEqualTo(1);
        assertThat(chatRoom.getParticipants()).contains(chatRoomDetail);
    }

    @UnitTest
    @DisplayName("채팅방에 유저를 제거하면 참가자 수가 감소해야한다.")
    void removeUser_suc() {
        //given
        ChatRoomDetail chatRoomDetail = mock(ChatRoomDetail.class);
        chatRoom.addUser(chatRoomDetail);

        //when
        chatRoom.removeUser(chatRoomDetail);

        //then
        assertThat(chatRoom.getParticipantCount()).isEqualTo(0);
        assertThat(chatRoom.getParticipants()).doesNotContain(chatRoomDetail);
    }

    @UnitTest
    @DisplayName("채팅방의 정보를 업데이트해야한다.")
    void updateChatRoom_suc(){
        //given
        String chatRoomName = "test chatRoom";
        int capacity = 10;

        //when
        chatRoom.update(chatRoomName, capacity);

        //then
        assertThat(chatRoom.getCapacity()).isEqualTo(capacity);
        assertThat(chatRoom.getName()).isEqualTo(chatRoomName);
    }

    @UnitTest
    @DisplayName("참가자보다 적은 capacity로 변경하면 예외가 발생해야한다.")
    void updateChatRoom_failWithCapacityMoreThanParticipantCount(){
        //given
        ChatRoomDetail user1 = mock(ChatRoomDetail.class);
        ChatRoomDetail user2 = mock(ChatRoomDetail.class);
        chatRoom.addUser(user1);
        chatRoom.addUser(user2);
        int capacity = 1;

        //when
        assertThatThrownBy(()->chatRoom.update("test", capacity))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ResponseCode.INVALID_INPUT_VALUE.getMessage());
    }

    @UnitTest
    @DisplayName("채팅방을 삭제하면 isDeleted값이 true가 되어야한다.")
    void deleteChatRoom_suc(){
        //given
        ChatRoomDetail user = mock(ChatRoomDetail.class);
        chatRoom.addUser(user);

        //when
        chatRoom.delete();

        //then
        assertThat(chatRoom.isDeleted()).isTrue();
    }
}