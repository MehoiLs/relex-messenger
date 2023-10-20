package com.mehoil.relex.shared.сomponents;

import com.mehoil.relex.general.features.community.common.data.FriendRequest;
import com.mehoil.relex.general.features.community.common.data.dto.FriendRequestDTO;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.features.messaging.data.ChatMessage;
import com.mehoil.relex.general.features.messaging.data.dto.ChatMessageDTO;
import com.mehoil.relex.shared.utils.AppUtils;
import com.mehoil.relex.shared.utils.CryptoUtils;
import lombok.NonNull;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfileDTO;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfileEditDTO;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfileFullDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserProfileFullDTO mapUserToUserFullProfileInfo(@NonNull User user) {
        return new UserProfileFullDTO(
                user.getId(),
                user.getUsername(),
                user.getPersonalStatus(),
                user.getDescription(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                user.isLocked()
        );
    }

    public UserProfileEditDTO mapUserToUserEditProfileInfo(@NonNull User user) {
        return new UserProfileEditDTO(
                user.getUsername(),
                user.getPersonalStatus(),
                user.getDescription(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public User mapUserProfileInfoToExistingUser(@NonNull UserProfileEditDTO profile, @NonNull User user) {
        if(!profile.getUsername().isEmpty()) user.setUsername(profile.getUsername());
        if(!profile.getPersonalStatus().isEmpty()) user.setPersonalStatus(profile.getPersonalStatus());
        if(!profile.getDescription().isEmpty()) user.setDescription(profile.getDescription());
        if(!profile.getFirstName().isEmpty()) user.setFirstName(profile.getFirstName());
        if(!profile.getLastName().isEmpty()) user.setLastName(profile.getLastName());

        return user;
    }

    public UserProfileDTO mapUserToUserProfileDto(@NonNull User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getPersonalStatus(),
                user.getDescription(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public FriendRequestDTO mapFriendRequestToDto(@NonNull FriendRequest friendRequest) {
        return new FriendRequestDTO(
                friendRequest.getSender().getUsername(),
                AppUtils.formatLocalDateTime(friendRequest.getSentAt())
        );
    }

    public ChatMessageDTO mapChatMessageToDto(@NonNull ChatMessage chatMessage) {
        return new ChatMessageDTO(
                chatMessage.getSenderName(),
                CryptoUtils.decryptPlainText(chatMessage.getContent())
        );
    }

    public List<String> mapUserListToUsernameList (@NonNull List<User> users) {
        return users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

}
