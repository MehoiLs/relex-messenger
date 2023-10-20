package com.mehoil.relex.general.features.community.common.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class FriendRequestListDTO {
    @JsonProperty("friend_requests")
    private final List<FriendRequestDTO> friendRequests;
}
