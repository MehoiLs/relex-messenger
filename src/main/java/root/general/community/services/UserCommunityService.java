package root.general.community.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.community.data.dto.CommunityInfoDTO;
import root.general.community.data.dto.UserProfileDTO;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.main.utils.MapperUtils;
import root.general.messaging.services.ChatMessageService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserCommunityService {

    private final UserService userService;
    private final UserFriendsService userFriendsService;
    private final FriendRequestsService friendRequestsService;
    private final ChatMessageService chatMessageService;

    public UserCommunityService(UserService userService, UserFriendsService userFriendsService, FriendRequestsService friendRequestsService, ChatMessageService chatMessageService) {
        this.userService = userService;
        this.userFriendsService = userFriendsService;
        this.friendRequestsService = friendRequestsService;
        this.chatMessageService = chatMessageService;
    }

    @Transactional
    public CommunityInfoDTO getGeneralInfoAsDto(User user) {
        return new CommunityInfoDTO(
                friendRequestsService.getAllFriendRequestsForUser(user).size(),
                chatMessageService.countAllNewMessages(user.getId())
        );
    }

    public UserProfileDTO getUserProfileInfo(String username) throws UserNotFoundException {
        return MapperUtils.mapUserToUserProfileDto(userService.getUserByUsername(username));
    }

    @Transactional
    public List<String> getUserFriendsList(String username) throws UserNotFoundException {
        User user = userService.getUserByUsername(username);
        if(!user.isFriendsListHidden()) return getAllFriends(user);
        else return Collections.emptyList();
    }

    @Transactional
    public List<String> getAllFriends(User user) {
        Set<User> friends = user.getFriendsList();
        List<String> friendsUsernames = friends.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        return friendsUsernames;
    }

    @Transactional
    public void removeFriend(String username, User requester) throws UserNotFoundException {
        User friend = userService.getUserByUsername(username);
        userFriendsService.deleteFriends(requester, friend);
    }

}
