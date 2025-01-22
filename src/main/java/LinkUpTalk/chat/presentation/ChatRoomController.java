package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.application.ChatRoomService;
import LinkUpTalk.chat.presentation.dto.ChatRoomCreateReqDto;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.chat.presentation.dto.ChatRoomModifyReqDto;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.response.SucResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/chat/rooms")
@RequiredArgsConstructor
@Log4j2
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //채팅방 생성
    @PostMapping
    public ResponseEntity<SucResponse<String>> create(@Valid @RequestBody ChatRoomCreateReqDto reqDto) {
        chatRoomService.create(reqDto);
        return SucResponse.ok(ResponseCode.CHATROOM_CREATE);
    }

    //채팅방 단일 조회
    @GetMapping("/{roomId}")
    public ResponseEntity<SucResponse<ChatroomGetResDto>> get(@PathVariable Long roomId) {
        var response = chatRoomService.getChatRoom(roomId);
        return SucResponse.ok(ResponseCode.CHATROOM_READ, response);
    }

    //채팅방 리스트 조회
    @GetMapping
    public ResponseEntity<SucResponse<List<ChatroomGetResDto>>> getList(@PageableDefault(page = 0, size = 10, sort = "count", direction = Sort.Direction.DESC) Pageable pageable,
                                                        String keyWord) {
        var response = chatRoomService.getChatRooms(pageable, keyWord);
        return SucResponse.ok(ResponseCode.CHATROOM_LIST_READ, response);
    }

    @GetMapping("/{roomId}/messages")
    @PreAuthorize("@chatResourceAuthService.isParticipant(authentication,#roomId)")
    public void getMessageHistories(@PathVariable Long roomId) {

    }

    //채팅방 삭제
    @DeleteMapping("/{roomId}")
    @PreAuthorize("@chatResourceAuthService.isHost(authentication,#roomId)")
    public ResponseEntity<SucResponse<String>> delete(@PathVariable Long roomId) {
        chatRoomService.delete(roomId);
        return SucResponse.ok(ResponseCode.CHATROOM_DELETE);
    }

    //채팅방 수정
    @PutMapping("/{roomId}")
    @PreAuthorize("@chatResourceAuthService.isHost(authentication,#roomId)")
    public ResponseEntity<SucResponse<String>> modify(@PathVariable Long roomId, @Valid @RequestBody ChatRoomModifyReqDto reqDto) {
        chatRoomService.modify(reqDto, roomId);
        return SucResponse.ok(ResponseCode.CHATROOM_UPDATE);
    }
}



