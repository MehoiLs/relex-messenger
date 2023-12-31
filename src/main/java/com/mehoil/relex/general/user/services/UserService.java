package com.mehoil.relex.general.user.services;

import com.mehoil.relex.general.user.data.User;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.features.community.common.services.UserFriendsService;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserFriendsService userFriendsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    public UserService(UserRepository userRepository, UserFriendsService userFriendsService, BCryptPasswordEncoder passwordEncoder, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.userFriendsService = userFriendsService;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    // GETTERS
    public User getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        messageSource.getMessage("error-user-not-found-by-id", new Object[]{id}, Locale.getDefault())
                ));
    }

    public User getUserByLogin(String login) throws UserNotFoundException  {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(
                        messageSource.getMessage("error-user-not-found-by-login", new Object[]{login}, Locale.getDefault())
                ));
    }

    public User getUserByUsername(String username) throws UserNotFoundException  {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        messageSource.getMessage("error-user-not-found-by-username", new Object[]{username}, Locale.getDefault())
                ));
    }

    public User getUserByAuth(Authentication auth) throws UserNotFoundException  {
        if (auth == null) throw new UserNotFoundException(
                messageSource.getMessage("error-user-not-found-by-auth", null, Locale.getDefault())
        );
        User authUser = (User) auth.getPrincipal();
        return getUserByUsername(authUser.getUsername());
    }

    public Set<User> getAllUsers() {
        return new HashSet<>(userRepository.findAll());
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

    public boolean userIsFriendsWith(@NonNull Long userToCheckId, @NonNull Long possibleFriendId) {
        try {
            User userToCheck = getUserById(userToCheckId);
            User userChecking = getUserById(possibleFriendId);
            return userToCheck.getFriendsList().contains(userChecking);
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    // PASSWORD ENCODER
    public boolean passwordMatches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String passwordEncode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
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
