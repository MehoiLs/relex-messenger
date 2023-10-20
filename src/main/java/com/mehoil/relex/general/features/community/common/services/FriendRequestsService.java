package com.mehoil.relex.general.features.community.common.services;

import com.mehoil.relex.general.features.community.common.data.FriendRequest;
import com.mehoil.relex.general.features.community.common.data.dto.FriendRequestListDTO;
import jakarta.transaction.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.features.community.common.data.dto.FriendRequestDTO;
import com.mehoil.relex.general.features.community.common.exceptions.FriendRequestException;
import com.mehoil.relex.general.features.community.common.repositories.FriendRequestsRepository;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.shared.сomponents.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FriendRequestsService {

    private final FriendRequestsRepository friendRequestsRepository;
    private final UserService userService;
    private final MessageSource messageSource;
    private final UserMapper userMapper;

    public FriendRequestsService(FriendRequestsRepository friendRequestsRepository, UserService userService, MessageSource messageSource, UserMapper userMapper) {
        this.friendRequestsRepository = friendRequestsRepository;
        this.userService = userService;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
    }

    public Set<FriendRequest> getAllFriendRequests () {
        return new HashSet<>(friendRequestsRepository.findAll());
    }

    public Set<FriendRequest> getAllFriendRequestsForUser (User user) {
        return new HashSet<>(friendRequestsRepository.findByRecipient(user));
    }

    public FriendRequestListDTO getAllFriendRequestsForUserAsDtoList(User user) {
        List<FriendRequestDTO> dtoList =
                friendRequestsRepository.findByRecipient(user).stream()
                .map(userMapper::mapFriendRequestToDto)
                .collect(Collectors.toList());
        return new FriendRequestListDTO(dtoList);
    }

    public String sendFriendRequest(String username, User requester) throws UserNotFoundException, FriendRequestException {
        User recipientUser = userService.getUserByUsername(username);
        if(requester.getFriendsList().contains(recipientUser))
            throw new FriendRequestException("You are already friends with " + username + ".");
        if(!friendRequestsRepository.existsBySenderAndRecipient(requester, recipientUser))
            save(new FriendRequest(requester, recipientUser));
        else throw new FriendRequestException("You have already sent the request to: " + username);
        return messageSource.getMessage("user-friend-request-sent-to-user", new Object[]{username}, Locale.getDefault());
    }

    @Transactional
    public String acceptFriendRequest(String senderUsername, User acceptorUser)
            throws UserNotFoundException, FriendRequestException {
        User senderUser = userService.getUserByUsername(senderUsername);
        Optional<FriendRequest> request =
                friendRequestsRepository.findBySenderAndRecipient(senderUser, acceptorUser);
        if(request.isPresent()) {
            userService.addFriend(acceptorUser, senderUser);
            friendRequestsRepository.delete(request.get());
        }
        else throw new FriendRequestException("User: " + senderUsername + " has not sent you a friend request.");
        return messageSource.getMessage("user-friend-added", new Object[]{senderUsername}, Locale.getDefault());
    }

    @Transactional
    public String acceptAllFriendRequests(User acceptorUser) {
        List<FriendRequest> requests = friendRequestsRepository.findByRecipient(acceptorUser);
        requests.forEach(request -> userService.addFriend(acceptorUser, request.getSender()));
        friendRequestsRepository.deleteAllByRecipient(acceptorUser);
        return messageSource.getMessage("user-friend-added-all", null, Locale.getDefault());
    }

    @Transactional
    public String denyFriendRequest(String senderUsername, User acceptorUser) throws UserNotFoundException {
        User senderUser = userService.getUserByUsername(senderUsername);
        Optional<FriendRequest> request = friendRequestsRepository.findBySenderAndRecipient(senderUser, acceptorUser);
        if(request.isPresent()) friendRequestsRepository.delete(request.get());
        else throw new UserNotFoundException("User: " + senderUsername + " has not sent you a friend request.");
        return messageSource.getMessage("user-friend-request-denied", new Object[]{senderUsername}, Locale.getDefault());
    }

    @Transactional
    public String denyAllFriendRequests(User acceptorUser) {
        friendRequestsRepository.deleteAllByRecipient(acceptorUser);
        return messageSource.getMessage("user-friend-request-denied-all", null, Locale.getDefault());
    }

    public FriendRequest save(FriendRequest friendRequest) {
        return friendRequestsRepository.save(friendRequest);
    }

    public void delete(FriendRequest friendRequest) {
        friendRequestsRepository.delete(friendRequest);
    }

}
