package com.mehoil.relex.general.features.community.utils;

import com.mehoil.relex.general.features.community.common.data.FriendRequest;
import com.mehoil.relex.shared.utils.AppUtils;
import lombok.experimental.UtilityClass;

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
