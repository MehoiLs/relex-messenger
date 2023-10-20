package com.mehoil.relex.general.features.community.common.services;

import com.mehoil.relex.general.features.community.common.data.dto.CommunityInfoDTO;
import com.mehoil.relex.general.features.community.common.data.dto.UserFriendListDTO;
import com.mehoil.relex.general.features.community.common.exceptions.CommunityException;
import com.mehoil.relex.general.features.community.common.exceptions.UserPrivacyException;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.features.messaging.services.ChatMessageService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.features.community.userprofile.data.userprofile.UserProfileDTO;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.shared.сomponents.UserMapper;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommunityService {

    private final UserService userService;
    private final UserFriendsService userFriendsService;
    private final FriendRequestsService friendRequestsService;
    private final ChatMessageService chatMessageService;
    private final MessageSource messageSource;
    private final UserMapper userMapper;

    public CommunityService(UserService userService,
                            UserFriendsService userFriendsService,
                            FriendRequestsService friendRequestsService,
                            ChatMessageService chatMessageService,
                            MessageSource messageSource,
                            UserMapper userMapper) {
        this.userService = userService;
        this.userFriendsService = userFriendsService;
        this.friendRequestsService = friendRequestsService;
        this.chatMessageService = chatMessageService;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
    }

    @Transactional
    public CommunityInfoDTO getGeneralInfoAsDto(@NonNull User user) {
        return new CommunityInfoDTO(
                friendRequestsService.getAllFriendRequestsForUser(user).size(),
                chatMessageService.countAllNewMessages(user.getId())
        );
    }

    public UserProfileDTO getUserProfileInfoAsDto(@NonNull String username) throws UserNotFoundException {
        return userMapper.mapUserToUserProfileDto(userService.getUserByUsername(username));
    }

    @Transactional
    public UserFriendListDTO getUserFriendsByUsernameAsDto(@NonNull String username) throws UserNotFoundException, UserPrivacyException {
        User user = userService.getUserByUsername(username);
        if(!user.isFriendsListHidden()) return getUserFriendsAsDto(user);
        else throw new UserPrivacyException(
                messageSource.getMessage("user-friend-list-hidden", new Object[]{username}, Locale.getDefault()
        ));
    }

    @Transactional
    public UserFriendListDTO getUserFriendsAsDto(@NonNull User user) {
        Set<User> friends = user.getFriendsList();
        return new UserFriendListDTO(
                friends.stream()
                .map(User::getUsername)
                .collect(Collectors.toList())
        );
    }

    @Transactional
    public String removeFriend(@NonNull String username, @NonNull User requester) throws UserNotFoundException, CommunityException {
        User friend = userService.getUserByUsername(username);
        if(!userService.userIsFriendsWith(friend.getId(), requester.getId()))
            throw new CommunityException(messageSource.getMessage("user-not-friends-with", new Object[]{username}, Locale.getDefault()));
        userFriendsService.deleteFriends(requester, friend);
        return messageSource.getMessage("user-friend-removed", new Object[]{friend.getUsername()}, Locale.getDefault());
    }

}
