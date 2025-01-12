package com.example.security.user.controller;

import com.example.security.common.codes.ResponseCode;
import com.example.security.common.dto.UserGetResDto;
import com.example.security.common.dto.UserModifyReqDto;
import com.example.security.common.dto.UserPasswordModifyReqDto;
import com.example.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * 사용자 단건 조회 : 코드 키 값을 기반으로 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable("userId") Long userId) {
        var response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 수정
     */
    @PreAuthorize("hasRole('ADMIN') or @resourceAuthService.isUserOwner(authentication, #userId)")
    @PostMapping("/profile/{userId}")
    public ResponseEntity<ResponseCode> modify(@PathVariable Long userId, @RequestBody UserModifyReqDto reqDto) {
        userService.modify(userId,reqDto);
        return ResponseEntity.ok(ResponseCode.STATE_SUCC);
    }

    /**
     * 사용자 비밀번호 수정
     */
    @PreAuthorize("hasRole('ADMIN') or @resourceAuthService.isUserOwner(authentication, #userId)")
    @PostMapping("/profile/password/{userId}")
    public ResponseEntity<ResponseCode> modifyPassword(@PathVariable Long userId, @RequestBody UserPasswordModifyReqDto reqDto) {
        userService.modifyPassword(userId,reqDto);
        return ResponseEntity.ok(ResponseCode.STATE_SUCC);
    }

}
