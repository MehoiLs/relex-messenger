package root.general.community.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.community.data.dto.FriendRequestDTO;
import root.general.community.data.dto.UserProfileDTO;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.main.utils.MapperUtils;
import root.general.messaging.services.ChatMessageService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserCommunityService {

    private final UserService userService;
    private final FriendRequestsService friendRequestsService;
    private final ChatMessageService chatMessageService;

    @Autowired
    public UserCommunityService(UserService userService, FriendRequestsService friendRequestsService, ChatMessageService chatMessageService) {
        this.userService = userService;
        this.friendRequestsService = friendRequestsService;
        this.chatMessageService = chatMessageService;
    }

    public String getGeneralInfo(User user) {
        StringBuilder output = new StringBuilder();
        output
                .append("Currently you have...\n\t")
                .append(friendRequestsService.getAllFriendRequestsForUser(user).size())
                .append(" friends requests.\n\t")
                .append(chatMessageService.countAllNewMessages(user.getId()))
                .append(" new messages.\n\t");

        return output.toString();
    }

    public UserProfileDTO getUserProfileInfo(String username) throws UserNotFoundException {
        return MapperUtils.mapUserToUserProfileDto(userService.getUserByUsername(username));
    }

    public List<String> getUserFriendsList(String username) throws UserNotFoundException {
        User user = userService.getUserByUsername(username);
        if(!user.isFriendsListHidden()) return getAllFriends(user);
        else return Collections.emptyList();
    }

    public List<String> getAllFriends(User user) {
        return user.getFriendsList().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

}
