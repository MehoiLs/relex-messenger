package root.general.community.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class FriendRequestDTO {

    @JsonProperty("sender_username")
    String senderUsername;
    @JsonProperty("timestamp")
    String timestamp;

    public FriendRequestDTO() {
        this.senderUsername = "";
        this.timestamp = "";
    }
}
