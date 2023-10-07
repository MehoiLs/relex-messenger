package root.main.utils;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import root.main.data.User;
import root.main.data.dto.userprofile.UserProfileEditInfoDTO;
import root.main.data.dto.userprofile.UserProfileFullInfoDTO;

import java.util.stream.Collectors;

@UtilityClass
public final class MapperUtils {

    public static UserProfileFullInfoDTO mapUserToUserFullProfileInfo(@NotNull User user) {
        return new UserProfileFullInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getPersonalStatus(),
                user.getDescription(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                user.getFriendsList()
                        .stream().map(User::getUsername).collect(Collectors.toList()),
                user.isLocked()
        );
    }

    public static UserProfileEditInfoDTO mapUserToUserEditProfileInfo(@NotNull User user) {
        return new UserProfileEditInfoDTO(
                user.getUsername(),
                user.getPersonalStatus(),
                user.getDescription(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public static User mapUserProfileInfoToExistingUser(@NotNull UserProfileEditInfoDTO profile, @NotNull User user) {
        if(!profile.getUsername().isEmpty()) user.setUsername(profile.getUsername());
        if(!profile.getPersonalStatus().isEmpty()) user.setPersonalStatus(profile.getPersonalStatus());
        if(!profile.getDescription().isEmpty()) user.setDescription(profile.getDescription());
        if(!profile.getFirstName().isEmpty()) user.setFirstName(profile.getFirstName());
        if(!profile.getLastName().isEmpty()) user.setLastName(profile.getLastName());

        return user;
    }

}
