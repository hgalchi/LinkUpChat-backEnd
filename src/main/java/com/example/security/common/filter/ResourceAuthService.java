package com.example.security.common.filter;

import com.example.security.user.entity.User;
import com.example.security.chat.Entity.Chatroom;
import com.example.security.chat.repository.ChatroomRepository;
import com.example.security.chat.repository.UserChatroomRepository;
import com.example.security.common.codes.ResponseCode;
import com.example.security.common.exception.BusinessException;
import com.example.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 리소스의 접근권한 인증 서비스
 */
@Service
@RequiredArgsConstructor
public class ResourceAuthService {

    private final ChatroomRepository chatroomRepository;
    private final UserRepository userRepository;
    private final UserChatroomRepository userChatroomRepository;

    /**
     * 사용자가 리소스의 주인임을 체크
     */
    public boolean isRoomOwner(Authentication authentication, Long roomId) {
        Long userId = chatroomRepository.findById(roomId).get().getOwner();
        User user=userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
        return user.getId().equals(userId);
    }

    public boolean isRoomMemeber(Authentication authentication, Long roomId) {
        Chatroom chatroom=chatroomRepository.findById(roomId).get();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
        return userChatroomRepository.existsByUserAndChatRoom(user, chatroom);
    }

    /**
     * 사용자리소스 주인만 접근
     */
    public boolean isUserOwner(Authentication authentication, Long userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOT_FOUND));
        return authentication.getName().equals(user.getEmail());
    }


}
