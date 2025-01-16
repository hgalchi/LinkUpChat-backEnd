package LinkUpTalk.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class UserGetResDto {
    Long id;
    String name;
    String email;
    List<String> groups;
}
