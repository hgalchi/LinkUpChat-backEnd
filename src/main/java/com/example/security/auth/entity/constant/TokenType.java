package com.example.security.auth.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

    refreshToken,
    accessToken;
}
