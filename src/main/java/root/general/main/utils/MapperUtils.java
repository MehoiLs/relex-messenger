package root.general.main.utils;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import root.general.community.data.dto.UserProfileDTO;
import root.general.main.data.User;
import root.general.main.data.dto.userprofile.UserProfileEditDTO;
import root.general.main.data.dto.userprofile.UserProfileFullDTO;

import java.util.stream.Collectors;

@UtilityClass
public final class MapperUtils {

    public static UserProfileFullDTO mapUserToUserFullProfileInfo(@NonNull User user) {
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

    public static UserProfileEditDTO mapUserToUserEditProfileInfo(@NonNull User user) {
        return new UserProfileEditDTO(
                user.getUsername(),
                user.getPersonalStatus(),
                user.getDescription(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public static User mapUserProfileInfoToExistingUser(@NonNull UserProfileEditDTO profile, @NonNull User user) {
        if(!profile.getUsername().isEmpty()) user.setUsername(profile.getUsername());
        if(!profile.getPersonalStatus().isEmpty()) user.setPersonalStatus(profile.getPersonalStatus());
        if(!profile.getDescription().isEmpty()) user.setDescription(profile.getDescription());
        if(!profile.getFirstName().isEmpty()) user.setFirstName(profile.getFirstName());
        if(!profile.getLastName().isEmpty()) user.setLastName(profile.getLastName());

        return user;
    }

    public static UserProfileDTO mapUserToUserProfileDto(@NonNull User user) {
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

}