package root.main.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.main.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GETTERS
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    public User getUserByAuth(Authentication auth) {
        if (auth == null) return null;
        User authUser = (User) auth.getPrincipal();
        return userRepository.findByUsername(authUser.getUsername())
                .orElse(null);
    }

    public Set<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    public Set<User> getUserFriendList(User user) {
        if (user == null) return Collections.emptySet();
        return new HashSet<>(user.getFriendsList());
    }

    public boolean userExistsById(Long id) {
        return userRepository.existsById(id);
    }

    // SETTERS
    public void setActiveSession(User user, boolean isActive) {
        user.setHasActiveSession(isActive);
        save(user);
    }

    public void setLastOnline(User user) {
        user.setLastOnline(LocalDateTime.now());
        save(user);
    }

    // ADDITIONAL
    public void restoreUserAccount(@NotNull User user) {
        user.setLocked(false);
        user.setLastOnline(LocalDateTime.now());
        save(user);
    }

    // SAVE & DELETE
    public User save(@NotNull User user) {
        return userRepository.save(user);
    }

    public void deleteUser(@NotNull User user) {
        userRepository.delete(user);
    }

    public void deleteAllLockedUsers() {
        List<User> usersToDelete = new ArrayList<>();

        getAllUsers().forEach(user -> {
                    if(user.getLastOnline().plusDays(7).isBefore(LocalDateTime.now()))
                        usersToDelete.add(user);
                });
        getAllUsers().forEach(it -> it.getFriendsList().removeAll(usersToDelete));
        usersToDelete.forEach(this::deleteUser);
    }

}
