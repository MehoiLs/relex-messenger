package root.main.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.main.data.dto.UserProfileEditInfoDTO;
import root.main.exceptions.UserProfileEditException;
import root.main.repositories.UserRepository;
import root.main.services.email.EmailTokenChangeService;
import root.main.utils.MapperUtils;
import root.main.utils.ValidationUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    public Iterable<User> getAllUsers() { return userRepository.findAll(); }

    public Set<User> getUserFriendList(User user) {
        if (user == null) return Collections.emptySet();
        return new HashSet<>(user.getFriendsList());
    }

    // SETTERS
    public void setActiveSession(User user, boolean isActive) {
        user.setHasActiveSession(isActive);
        save(user);
    }

    // SAVE & DELETE
    public User save(@NotNull User user) {
        return userRepository.save(user);
    }

    public void deleteUser(@NotNull User user) {
        userRepository.delete(user);
    }

}
