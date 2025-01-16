package LinkUpTalk.chat.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GroupChatResponseDto {

    private Long id;

    private String name;

    private int count;

    private int maxCount;

}
