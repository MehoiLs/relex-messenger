package com.mehoil.relex.general.features.community.common.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

@Data
public class CommunityInfoDTO {

    @JsonProperty("friend_requests")
    private final int friendRequests;
    @JsonProperty("unread_messages")
    private final long unreadMessages;

}
