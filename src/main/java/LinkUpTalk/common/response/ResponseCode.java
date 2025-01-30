package LinkUpTalk.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    //400
    NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 유저를 찾을 수 없습니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채팅룸을 찾을 수 없습니다."),
    METHOD_NOT_ALLOW(HttpStatus.METHOD_NOT_ALLOWED, "허락되지 않은 요청"),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효성 검증 오류"),
    INVALID_DESTINATION(HttpStatus.BAD_REQUEST, "유효하지 않은 경로입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "올바르지 않은 토큰입니다."),

    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    MALFORMED_JWT(HttpStatus.UNAUTHORIZED, "올바르지 않은 토큰입니다."),
    SIGNATURE_JWT(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    CATEGORY_NOT_REFRESH(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 아닙니다."),
    TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "사용자 인증 실패"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "리소스 접근권한이 없습니다."),


    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "가입된 사용자 입니다."),


    CHATROOM_USER_NOT_FOUND_IN(HttpStatus.NOT_FOUND, "해당 유저를 채팅방에서 찾을 수 없습니다."),
    CHATROOM_EXCEEDED(HttpStatus.CONFLICT, "채팅방이 인원이 초과되었습니다."),
    STOMP_NOT_FOUND_DESTINATION(HttpStatus.NOT_FOUND, "목적지를 찾을 수 없습니다."),

    //500
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류"),
    
    
    //200
    STATE_SUC(HttpStatus.OK, "요청 처리 성공"),
    USER_LOGIN_SUC(HttpStatus.OK, "사용자 로그인 성공"),

    CHATROOM_CREATE(HttpStatus.OK, "채팅방 생성 성공"),
    CHATROOM_READ(HttpStatus.OK,"채팅방 단일 조회 성공"),
    CHATROOM_LIST_READ(HttpStatus.OK,"채팅방 리스트 조회 성공"),
    CHATROOM_MESSAGE_HISTORY_READ(HttpStatus.OK,"채팅방 메시지 내역 조회 성공"),
    CHATROOM_PARTICIPANTS_READ(HttpStatus.OK,"채팅방 유저 조회 성공"),
    CHATROOM_UPDATE(HttpStatus.OK,"채팅방 수정 성공"),
    CHATROOM_DELETE(HttpStatus.OK,"채팅방 삭제 성공");

    private final HttpStatusCode status;
    private final String message;

}
