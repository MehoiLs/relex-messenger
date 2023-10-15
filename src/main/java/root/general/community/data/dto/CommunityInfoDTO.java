package root.general.community.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CommunityInfoDTO {

    @JsonProperty("friend_requests")
    int friendRequests;
    @JsonProperty("unread_messages")
    long unreadMessages;

    public CommunityInfoDTO() {
        this.friendRequests = 0;
        this.unreadMessages = 0L;
    }
}
