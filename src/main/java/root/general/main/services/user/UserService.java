package root.general.main.services.user;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import root.general.community.services.UserFriendsService;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserFriendsService userFriendsService;

    public UserService(UserRepository userRepository, UserFriendsService userFriendsService) {
        this.userRepository = userRepository;
        this.userFriendsService = userFriendsService;
    }

    // GETTERS
    public User getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user by id: " + id));
    }

    public User getUserByLogin(String login) throws UserNotFoundException  {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user by login: " + login));
    }

    public User getUserByUsername(String username) throws UserNotFoundException  {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user by username: " + username));
    }

    public User getUserByAuth(Authentication auth) throws UserNotFoundException  {
        if (auth == null) throw new UserNotFoundException("Cannot find user by authentication if it's null.");
        User authUser = (User) auth.getPrincipal();
        return getUserByUsername(authUser.getUsername());
    }

    public Set<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    public Set<User> getUserFriendList(@NonNull User user) {
        return new HashSet<>(user.getFriendsList());
    }

    public boolean userExistsById(Long id) {
        return userRepository.existsById(id);
    }

    // SETTERS
    public void setActiveSession(@NonNull User user, boolean isActive) {
        user.setHasActiveSession(isActive);
        save(user);
    }

    public void setLastOnline(@NonNull User user) {
        user.setLastOnline(LocalDateTime.now());
        save(user);
    }

    // ADDITIONAL
    public void restoreUserAccount(@NonNull User user) {
        user.setLocked(false);
        user.setLastOnline(LocalDateTime.now());
        save(user);
    }

    @Transactional
    public void addFriend(@NonNull User user, @NonNull User friend) {
        user.getFriendsList().add(friend);
        friend.getFriendsList().add(user);
        userRepository.save(user);
        userRepository.save(friend);
    }


    public boolean userAccessibilityIsFriendsOnly(@NonNull Long userId) {
        try {
            return getUserById(userId).isAccessibilityFriendsOnly();
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    public boolean userIsFriendsWith(@NonNull Long userToCheckId, @NonNull Long userId) {
        try {
            User userToCheck = getUserById(userToCheckId);
            User userChecking = getUserById(userId);
            return userToCheck.getFriendsList().contains(userChecking);
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    // SAVE & DELETE
    public User save(@NonNull User user) {
        return userRepository.save(user);
    }

    public void forceDeleteUser(@NonNull User user) {
        userRepository.delete(user);
    }

    public void deleteAllLockedUsers() {
        List<User> usersToDelete = new ArrayList<>();

        getAllUsers().forEach(user -> {
                    if(user.getLastOnline().plusDays(7).isBefore(LocalDateTime.now()))
                        usersToDelete.add(user);
                });
        usersToDelete.forEach(userFriendsService::deleteAllFriendsOfUser);
        usersToDelete.forEach(this::forceDeleteUser);
    }

}
