package root.general.main.data.dto.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserProfilePrivacyDTO {

    @JsonProperty("hide_friends_list")
    boolean isFriendsListHidden;
    @JsonProperty("messages_friends_only")
    boolean isFriendsOnlyMessages;

    public UserProfilePrivacyDTO() {
        this.isFriendsListHidden = false;
        this.isFriendsOnlyMessages = false;
    }
}
