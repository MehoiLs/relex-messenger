package com.mehoil.relex.general.features.messaging.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ChatMessageListDTO {

    @JsonProperty("chat_messages")
    private final List<ChatMessageDTO> chatMessages;
}
