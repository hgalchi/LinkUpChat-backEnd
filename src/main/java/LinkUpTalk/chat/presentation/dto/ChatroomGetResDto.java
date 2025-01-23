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

    private int participantCount;

    private int capacity;

    private List<String> participants;
}
