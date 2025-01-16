package LinkUpTalk.chat.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatroomGetResDto {
    private Long id;

    private String name;

    private int curCount;

    private int maxCount;

    private List<String> members;
}
