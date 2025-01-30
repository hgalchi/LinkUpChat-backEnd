package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.application.ChatRoomService;
import LinkUpTalk.chat.presentation.dto.ChatMessageResDto;
import LinkUpTalk.chat.presentation.dto.ChatRoomCreateReqDto;
import LinkUpTalk.chat.presentation.dto.ChatroomGetResDto;
import LinkUpTalk.chat.presentation.dto.ChatRoomModifyReqDto;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.response.SucResponse;
import LinkUpTalk.user.presentation.dto.UserGetResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/chat/rooms")
@RequiredArgsConstructor
@Log4j2
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //그룹 채팅방 생성
    @PostMapping("/group")
    public ResponseEntity<SucResponse<String>> create(@Valid @RequestBody ChatRoomCreateReqDto reqDto) {
        chatRoomService.createGroup(reqDto);
        return SucResponse.ok(ResponseCode.CHATROOM_CREATE);
    }

    //개인 채팅방 생성
    @PostMapping("/dm/{receiverId}")
    public ResponseEntity<SucResponse<String>> createDm(@PathVariable Long receiverId, Authentication authentication) {
        chatRoomService.createDm(receiverId,authentication.getName());
        return SucResponse.ok(ResponseCode.CHATROOM_CREATE);
    }

    //채팅방 리스트 조회
    @GetMapping
    public ResponseEntity<SucResponse<List<ChatroomGetResDto>>> getList(@PageableDefault(page = 0, size = 10, sort = "count", direction = Sort.Direction.DESC) Pageable pageable,
                                                        String keyWord) {
        var response = chatRoomService.getChatRooms(pageable, keyWord);
        return SucResponse.ok(ResponseCode.CHATROOM_LIST_READ, response);
    }

    //채팅방 단일 조회
    @GetMapping("/{roomId}")
    public ResponseEntity<SucResponse<ChatroomGetResDto>> get(@PathVariable Long roomId) {
        var response = chatRoomService.getChatRoom(roomId);
        return SucResponse.ok(ResponseCode.CHATROOM_READ, response);
    }

    //채팅에 참여한 유저 리스트 조회
    @GetMapping("/{roomId}/participants")
    @PreAuthorize("@chatResourceAuthService.isParticipant(authentication,#roomId)")
    public ResponseEntity<SucResponse<List<UserGetResDto>>> getUsers(@PathVariable Long roomId){
        var response = chatRoomService.getParticipants(roomId);
        return SucResponse.ok(ResponseCode.CHATROOM_PARTICIPANTS_READ, response);
    }

    //채팅 메시지 기록 조회
    @GetMapping("/{roomId}/messages")
    @PreAuthorize("@chatResourceAuthService.isParticipant(authentication,#roomId)")
    public ResponseEntity<SucResponse<List<ChatMessageResDto>>> getMessageHistories(@PathVariable Long roomId) {
        var response = chatRoomService.getMessageHistory(roomId);
        return SucResponse.ok(ResponseCode.CHATROOM_MESSAGE_HISTORY_READ, response);
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



