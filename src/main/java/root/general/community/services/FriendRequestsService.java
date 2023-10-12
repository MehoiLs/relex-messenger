package root.general.community.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.community.data.dto.FriendRequestDTO;
import root.general.community.exception.FriendRequestException;
import root.general.community.repositories.FriendRequestsRepository;
import root.general.community.utils.RequestsUtils;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FriendRequestsService {

    private final FriendRequestsRepository friendRequestsRepository;
    private final UserService userService;

    @Autowired
    public FriendRequestsService(FriendRequestsRepository friendRequestsRepository, UserService userService) {
        this.friendRequestsRepository = friendRequestsRepository;
        this.userService = userService;
    }

    public Set<FriendRequestDTO> getAllFriendRequestsForUser (User user) {
        return StreamSupport
                .stream(friendRequestsRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    public String getAllFriendRequestsForUserAsString(User user) {
        Set<FriendRequestDTO> requestsSet = StreamSupport
                .stream(friendRequestsRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
        return RequestsUtils.convertAllFriendRequestsToString(requestsSet);
    }

    public void addFriend(String username, User requester) throws UserNotFoundException, FriendRequestException {
        User recipientUser = userService.getUserByUsername(username);
        if(!friendRequestsRepository.existsBySenderAndRecipient(requester, recipientUser))
            save(new FriendRequestDTO(null, requester, recipientUser));
        //TODO ALREADY FRIENDS
        else throw new FriendRequestException("You have already sent the request to: " + username);
    }

    public void removeFriend(String username, User requester) throws UserNotFoundException {
        User friendToRemove = userService.getUserByUsername(username);
        userService.removeFriend(requester, friendToRemove);
    }

    @Transactional
    public void acceptFriendRequest(String senderUsername, User acceptorUser) throws UserNotFoundException {
        User senderUser = userService.getUserByUsername(senderUsername);
        Optional<FriendRequestDTO> request =
                friendRequestsRepository.findBySenderAndRecipient(senderUser, acceptorUser);
        if(request.isPresent()) {
            userService.addFriend(acceptorUser, senderUser);
            friendRequestsRepository.delete(request.get());
        }
        else throw new UserNotFoundException("User: " + senderUsername + " has not sent you a friend request.");
    }

    @Transactional
    public void acceptAllFriendRequests(User acceptorUser) {
        Set<FriendRequestDTO> requests = getAllFriendRequestsForUser(acceptorUser);
        requests.forEach(request -> userService.addFriend(acceptorUser, request.getSender()));
        friendRequestsRepository.deleteAllByRecipient(acceptorUser);
    }

    @Transactional
    public void denyFriendRequest(String senderUsername, User acceptorUser) throws UserNotFoundException {
        User senderUser = userService.getUserByUsername(senderUsername);
        Optional<FriendRequestDTO> request = friendRequestsRepository.findBySenderAndRecipient(senderUser, acceptorUser);
        if(request.isPresent()) friendRequestsRepository.delete(request.get());
        else throw new UserNotFoundException("User: " + senderUsername + " has not sent you a friend request.");
    } //TODO DB CLEANUP

    @Transactional
    public void denyAllFriendRequests(User acceptorUser) {
        friendRequestsRepository.deleteAllByRecipient(acceptorUser);
    }

    public FriendRequestDTO save(FriendRequestDTO friendRequestDTO) {
        return friendRequestsRepository.save(friendRequestDTO);
    }

    public void delete(FriendRequestDTO friendRequestDTO) {
        friendRequestsRepository.delete(friendRequestDTO);
    }

}
