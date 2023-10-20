package com.mehoil.relex.general.features.community.userprofile.data.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
public class UserProfilePrivacyDTO {

    @JsonProperty("hide_friends_list")
    private final boolean hideFriendsList;
    @JsonProperty("messages_friends_only")
    private final boolean messagesFriendsOnly;

}
