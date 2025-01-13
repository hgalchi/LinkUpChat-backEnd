package LinkUpTalk.user.presentation;

import LinkUpTalk.common.response.ResponseCode;
import LinkUpTalk.user.dto.UserModifyReqDto;
import LinkUpTalk.user.dto.UserPasswordModifyReqDto;
import LinkUpTalk.user.application.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseCode> modify(@PathVariable Long userId, @Valid @RequestBody UserModifyReqDto reqDto) {
        userService.modify(userId,reqDto);
        return ResponseEntity.ok(ResponseCode.STATE_SUCC);
    }

    /**
     * 사용자 비밀번호 수정
     */
    @PreAuthorize("hasRole('ADMIN') or @resourceAuthService.isUserOwner(authentication, #userId)")
    @PostMapping("/profile/password/{userId}")
    public ResponseEntity<ResponseCode> modifyPassword(@PathVariable Long userId,@Valid @RequestBody UserPasswordModifyReqDto reqDto) {
        userService.modifyPassword(userId,reqDto);
        return ResponseEntity.ok(ResponseCode.STATE_SUCC);
    }

}
