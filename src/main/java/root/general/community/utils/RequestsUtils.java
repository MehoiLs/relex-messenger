package root.general.community.utils;

import lombok.experimental.UtilityClass;
import root.general.community.data.FriendRequest;
import root.general.main.utils.AppUtils;

import java.util.Collection;

@UtilityClass
public final class RequestsUtils {

    public static String convertAllFriendRequestsToString(Collection<FriendRequest> requests) {
        StringBuilder output = new StringBuilder();
        output.append("Currently you have: ").append(requests.size()).append(" friends requests.\n\n");
        if(requests.isEmpty()) return output.toString();
        requests.stream().forEach(request -> {
            output.append("[").append(request.getSender().getUsername())
                    .append("] at ")
                    .append(AppUtils.formatLocalDateTime(request.getSentAt()))
                    .append("\n");
        });
        return output.toString();
    }

}
