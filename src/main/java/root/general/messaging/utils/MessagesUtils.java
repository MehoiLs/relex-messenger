package root.general.messaging.utils;

import lombok.experimental.UtilityClass;
import root.general.main.utils.AppUtils;
import root.general.main.utils.CryptoUtils;
import root.general.messaging.data.ChatMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@UtilityClass
public final class MessagesUtils {

    public static String getChatIdBySenderAndRecipient(Long senderId, Long recipientId) {
        List<Long> sortedIds = Arrays.asList(senderId, recipientId);
        Collections.sort(sortedIds);
        return String.format("%s_%s", sortedIds.get(0), sortedIds.get(1));
    }

    public static String buildStringFromMessagesList(List<ChatMessage> chatMessages) {
        if (chatMessages == null) return "";
        StringBuilder output = new StringBuilder();
        chatMessages.forEach(msg -> {
            output.append("(").append(AppUtils.formatLocalDateTime(msg.getTimestamp())).append(")");
            output.append("[").append(msg.getSenderName()).append("]: ");
            output.append(CryptoUtils.decryptPlainText(msg.getContent())).append("\n");
        });
        return output.toString();
    }

}
