package com.mehoil.relex.general.features.community.common.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

@Data
public class FriendRequestDTO {

    @JsonProperty("sender_username")
    private final String senderUsername;
    @JsonProperty("timestamp")
    private final String timestamp;

}
