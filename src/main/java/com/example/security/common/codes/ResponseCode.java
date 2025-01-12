package com.example.security.common.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    //400
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효성 검증 오류"),
    INVALID_DESTINATION(HttpStatus.BAD_REQUEST, "유효하지 않은 경로입니다."),

    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    MALFORMED_JWT(HttpStatus.UNAUTHORIZED, "올바르지 않은 토큰입니다."),
    SIGNATURE_JWT(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    CATEGORY_NOT_REFRESH(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 아닙니다."),
    TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 존재하지 않습니다."),

    NOT_FOUND(HttpStatus.NOT_FOUND, "요청 URL 오류"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "리소스 접근권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "사용자 인증 실패"),
    METHOD_NOT_ALLOW(HttpStatus.METHOD_NOT_ALLOWED, "허락되지 않은  요청"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "가입된 사용자 입니다."),
    //200
    STATE_SUCC(HttpStatus.OK, "요청 처리 성공"),
    USER_LOGIN_SUCC(HttpStatus.OK, "사용자 로그인 성공"),

    GROUP_CHAT_CREATE_SUCC(HttpStatus.OK, "채팅방 생성 성공"),
    GROUP_CHAT_LIST_READ_SUCC(HttpStatus.OK,"채팅방 리스트 조회 성공"),
    GROUP_CHAT_UPDATE_SUCC(HttpStatus.OK,"채팅방 수정 성공"),
    GROUP_CHAT_DELETE_SUCC(HttpStatus.OK,"채팅방 삭제 성공"),
    GROUP_CHAT_ONLINE_USERS_READ_SUCC(HttpStatus.OK,"채팅방 실시간 유저 조회 성공");

    private HttpStatusCode status;
    private String message;

    ResponseCode(String message) {

    }
}
