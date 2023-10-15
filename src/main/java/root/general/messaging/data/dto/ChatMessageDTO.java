package root.general.messaging.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ChatMessageDTO {

    @JsonProperty("sender")
    String sender;
    @JsonProperty("content")
    String content;

    public ChatMessageDTO() {
        this.sender = "";
        this.content = "";
    }
}
