package com.example.security.chat.controller;

import com.example.security.chat.dto.GroupChatRequestDto;
import com.example.security.chat.service.GroupChatService;
import com.example.security.common.codes.ResponseCode;
import com.example.security.common.codes.SuccResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/chat/rooms")
@RequiredArgsConstructor
@Log4j2
public class GroupChatController {

    private final GroupChatService groupChatService;

    //채팅방 생성
    @PostMapping
    public SuccResponse<List<String>> create(@Valid @RequestBody GroupChatRequestDto reqDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        groupChatService.createGroupChat(email, reqDto);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_CREATE_SUCC, Arrays.asList());
    }

    //채팅방 리스트 조회 - 검색 추가 페이징 처리, 데이터를 추가로 넘겨줘야하는데 일관된 response를 넘겨줄 수 없다.
    @GetMapping
    public SuccResponse<Map<String, Object>> getList(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "") String search) {
        Map<String, Object> list = groupChatService.getListGroupChat(page, search);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_LIST_READ_SUCC, list);
    }

    //채팅방 삭제
    @DeleteMapping("/{roomId}")
    @PreAuthorize("isAuthenticated() and @mySecurityService.isRoomOwner(authentication,#roomId)")
    public SuccResponse<Long> delete(@PathVariable Long roomId) {
        groupChatService.deleteGroupChat(roomId);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_DELETE_SUCC, roomId);
    }

    //채팅방 수정
    @PutMapping("/{roomId}")
    @PreAuthorize("isAuthenticated() and @mySecurityService.isRoomOwner(authentication,#roomId)")
    public SuccResponse<Long> modify(@PathVariable Long roomId, @Valid @RequestBody GroupChatRequestDto reqDto) {
        groupChatService.modifyGroupChat(reqDto, roomId);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_UPDATE_SUCC, roomId);
    }

    //접속상태 조회
    @GetMapping("/{roomId}/onlineList")
    @PreAuthorize("isAuthenticated() and @mySecurityService.isRoomMemeber(authentication,#roomId)")
    public SuccResponse<Set<String>> getOnlineList(@PathVariable Long roomId) {
        Set<String> list = groupChatService.getOnlineUsers(roomId);
        return new SuccResponse<>(ResponseCode.GROUP_CHAT_ONLINE_USERS_READ_SUCC, list);
    }
}



