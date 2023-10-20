package com.mehoil.relex.general.features.messaging.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ChatMessageDTO {

    @JsonProperty("sender")
    private final String sender;
    @JsonProperty("content")
    private final String content;

}
