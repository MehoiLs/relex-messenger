package root.general.main.services.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import root.general.community.services.UserFriendsService;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFriendsService userFriendsService;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserByLogin() {
        User user = getUser();
        when(userRepository.findByLogin("login")).thenReturn(Optional.of(user));

        User resultUser = userService.getUserByLogin("login");
        assertEquals(user, resultUser);

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByLogin("somenonsense"));
    }

    @Test
    void testGetUserByUsername() {

        User user = getUser();
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        User resultUser = userService.getUserByUsername("username");
        assertEquals(user, resultUser);

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsername("somenonsense"));
    }

    @Test
    void testGetUserByAuth() {
        User user = getUser();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        User resultUser = userService.getUserByAuth(auth);

        assertEquals(user, resultUser);
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByAuth(null));
    }

    @Test
    void testGetAllUsers() {
        Iterable<User> usersIterable = getUsers();
        when(userRepository.findAll()).thenReturn(usersIterable);
        Set<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetUserFriendList() {
        List<User> users = getUsers().stream().toList();
        User user1 = users.get(0);
        User user2 = users.get(1);
        user1.getFriendsList().add(user2);
        user2.getFriendsList().add(user1);

        Set<User> result1 = userService.getUserFriendList(user1);
        Set<User> result2 = userService.getUserFriendList(user2);

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertTrue(result1.contains(user2));
        assertTrue(result2.contains(user1));
    }

    @Test
    void testRestoreUserAccount() {
        User user = getUser();
        user.setLocked(true);
        userService.restoreUserAccount(user);

        assertFalse(user.isLocked());
    }

    @Test
    void testAddFriend() {
        List<User> users = getUsers().stream().toList();
        User user = users.get(0);
        User friend = users.get(1);

        userService.addFriend(user, friend);

        assertTrue(user.getFriendsList().contains(friend));
        assertTrue(friend.getFriendsList().contains(user));
    }

    @Test
    void testDeleteAllLockedUsers() {
        performDeleteAllLockedUsersTest(LocalDateTime.now().minusDays(8), true);
        performDeleteAllLockedUsersTest(LocalDateTime.now().minusDays(5), false);
    }

    private void performDeleteAllLockedUsersTest(LocalDateTime lastOnline, boolean shouldBeDeleted) {
        List<User> usersFakeRepository = new ArrayList<>(getUsers().stream().toList());
        User commonUser = usersFakeRepository.get(0);
        User lockedUser = usersFakeRepository.get(1);

        lockedUser.setLocked(true);
        lockedUser.setLastOnline(lastOnline);

        lockedUser.getFriendsList().add(commonUser);
        commonUser.getFriendsList().add(lockedUser);

        User lockedUserArtefact = lockedUser;

        when(userRepository.findAll()).thenReturn(usersFakeRepository::iterator);
        lenient().doAnswer(invocation -> {
            User userToDelete = invocation.getArgument(0);
            usersFakeRepository.remove(userToDelete);
            return null;
        }).when(userRepository).delete(lockedUser);
        lenient().doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            List<User> usersFriends = user.getFriendsList().stream().toList();
            usersFriends.forEach(friend -> {
                friend.getFriendsList().remove(user);
            });
            user.getFriendsList().clear();
            return null;
        }).when(userFriendsService).deleteAllFriendsOfUser(lockedUser);

        userService.deleteAllLockedUsers();

        assertEquals(shouldBeDeleted, !usersFakeRepository.contains(lockedUserArtefact));
        assertEquals(shouldBeDeleted, !commonUser.getFriendsList().contains(lockedUserArtefact));
    }

    private Set<User> getUsers() {
        User user1 = new User("email@website.com", "login", "password",
                "username", "Firstname", "Lastname");
        User user2 = new User("otheremail@website.org", "somelogin", "apassword",
                "nickname", "Name", "Surname");
        return Set.of(user1, user2);
    }

    private User getUser() {
        return new User("email@website.com", "login", "password",
                "username", "Firstname", "Lastname");
    }
}