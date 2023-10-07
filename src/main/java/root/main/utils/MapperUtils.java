package root.main.utils;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import root.main.data.User;
import root.main.data.dto.userprofile.UserProfileEditDTO;
import root.main.data.dto.userprofile.UserProfileFullDTO;

import java.util.stream.Collectors;

@UtilityClass
public final class MapperUtils {

    public static UserProfileFullDTO mapUserToUserFullProfileInfo(@NotNull User user) {
        return new UserProfileFullDTO(
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

    public static UserProfileEditDTO mapUserToUserEditProfileInfo(@NotNull User user) {
        return new UserProfileEditDTO(
                user.getUsername(),
                user.getPersonalStatus(),
                user.getDescription(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public static User mapUserProfileInfoToExistingUser(@NotNull UserProfileEditDTO profile, @NotNull User user) {
        if(!profile.getUsername().isEmpty()) user.setUsername(profile.getUsername());
        if(!profile.getPersonalStatus().isEmpty()) user.setPersonalStatus(profile.getPersonalStatus());
        if(!profile.getDescription().isEmpty()) user.setDescription(profile.getDescription());
        if(!profile.getFirstName().isEmpty()) user.setFirstName(profile.getFirstName());
        if(!profile.getLastName().isEmpty()) user.setLastName(profile.getLastName());

        return user;
    }

}
