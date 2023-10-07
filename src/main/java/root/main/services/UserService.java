package root.main.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.main.repositories.UserRepository;

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
        if (!auth.isAuthenticated()) return null;
        return userRepository.findByUsername(auth.getName())
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

    // SETTERS
    public void setActiveSession(User user, boolean isActive) {
        user.setHasActiveSession(isActive);
        save(user);
    }

    public void setLastOnline(User user) {
        user.setLastOnline(new Date());
        save(user);
    }

    // ADDITIONAL
    public void restoreUserAccount(@NotNull User user) {
        user.setLocked(false);
        user.setLastOnline(new Date());
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
        Date now = new Date();
        List<User> usersToDelete = new ArrayList<>();

        getAllUsers().forEach(it -> {
                    long msDifference = now.getTime() - it.getLastOnline().getTime();
                    if(TimeUnit.MILLISECONDS.toDays(msDifference) >= 7)
                        usersToDelete.add(it);
                });
        getAllUsers().forEach(it -> it.getFriendsList().removeAll(usersToDelete));
        usersToDelete.forEach(this::deleteUser);
    }

}
