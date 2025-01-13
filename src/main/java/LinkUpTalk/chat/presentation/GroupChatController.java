package LinkUpTalk.chat.presentation;

import LinkUpTalk.chat.dto.ChatroomGetResDto;
import LinkUpTalk.chat.dto.GroupChatRequestDto;
import LinkUpTalk.chat.application.GroupChatService;
import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.common.response.SuccResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/rooms")
@RequiredArgsConstructor
@Log4j2
public class GroupChatController {

    private final GroupChatService groupChatService;

    //채팅방 생성
    @PostMapping
    public SuccResponse<Map<String,Long>> create(@Valid @RequestBody GroupChatRequestDto reqDto) {
        var chatRoomId = groupChatService.createGroupChat(reqDto);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_CREATE_SUCC, Map.of("chatRoomId",chatRoomId));
    }

    //채팅방 단일 조회
    @GetMapping("/{roomId}")
    public SuccResponse<ChatroomGetResDto> get(@PathVariable Long roomId) {
        var response = groupChatService.getGroupChat(roomId);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_READ_SUCC, response);
    }
    //채팅방 리스트 조회
    @GetMapping
    public SuccResponse<List<ChatroomGetResDto>> getList(@PageableDefault(page = 0, size = 10, sort = "count", direction = Sort.Direction.DESC) Pageable pageable,
                                                          String keyWord
                                                          ) {
        var response = groupChatService.getListGroupChat(pageable, keyWord);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_LIST_READ_SUCC, response);
    }

    //채팅방 삭제
    @DeleteMapping("/{roomId}")
    @PreAuthorize("@ResourceAuthService.isRoomOwner(authentication,#roomId)")
    public SuccResponse<Long> delete(@PathVariable Long roomId) {
        groupChatService.deleteGroupChat(roomId);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_DELETE_SUCC, roomId);
    }

    //채팅방 수정
    @PutMapping("/{roomId}")
    @PreAuthorize("@ResourceAuthService.isRoomOwner(authentication,#roomId)")
    public SuccResponse<Long> modify(@PathVariable Long roomId, @Valid @RequestBody GroupChatRequestDto reqDto) {
        groupChatService.modifyGroupChat(reqDto, roomId);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_UPDATE_SUCC, roomId);
    }

    /*//접속상태 조회
    @GetMapping("/{roomId}/onlineList")
    @PreAuthorize("isAuthenticated() and @mySecurityService.isRoomMemeber(authentication,#roomId)")
    public SuccResponse<Set<String>> getOnlineList(@PathVariable Long roomId) {
        Set<String> list = groupChatService.getOnlineUsers(roomId);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_ONLINE_USERS_READ_SUCC, list);
    }*/
}



