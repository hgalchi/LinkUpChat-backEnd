package com.example.security.common.dto;

import com.example.security.auth.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
public class UserGetResDto {
    Long id;
    String name;
    String email;
    List<String> groups;
}
