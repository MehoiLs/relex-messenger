package com.mehoil.relex.general.common.services.user;

import com.mehoil.relex.general.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import com.mehoil.relex.general.TestUtils;
import com.mehoil.relex.general.features.community.common.services.UserFriendsService;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.repositories.UserRepository;

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

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserByLogin() throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        when(userRepository.findByLogin("login")).thenReturn(Optional.of(user));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");

        User resultUser = userService.getUserByLogin("login");
        assertEquals(user, resultUser);

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByLogin("somenonsense"));
    }

    @Test
    void testGetUserByUsername() throws UserNotFoundException {

        User user = TestUtils.getNewDefaultUser();
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");

        User resultUser = userService.getUserByUsername("username");
        assertEquals(user, resultUser);

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsername("somenonsense"));
    }

    @Test
    void testGetUserByAuth() throws UserNotFoundException {
        User user = TestUtils.getNewDefaultUser();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");

        User resultUser = userService.getUserByAuth(auth);

        assertEquals(user, resultUser);
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByAuth(null));
    }

    @Test
    void testGetAllUsers() {
        List<User> userList = TestUtils.getNewUsers();
        when(userRepository.findAll()).thenReturn(userList);
        Set<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(TestUtils.getNewUsers().size(), result.size());
    }

    @Test
    void testGetUserFriendList() {
        List<User> users = TestUtils.getNewUsers();
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
        User user = TestUtils.getNewDefaultUser();
        user.setLocked(true);
        userService.restoreUserAccount(user);

        assertFalse(user.isLocked());
    }

    @Test
    void testAddFriend() {
        List<User> users = TestUtils.getNewUsers();
        User user = users.get(0);
        User friend = users.get(1);

        userService.addFriend(user, friend);

        assertTrue(user.getFriendsList().contains(friend));
        assertTrue(friend.getFriendsList().contains(user));
    }

    @Test
    void testDeleteAllLockedUsers() {
        // Последний раз онлайн больше недели назад
        performDeleteAllLockedUsersTest(LocalDateTime.now().minusDays(8), true);
        // Последний раз онлайн меньше недели назад
        performDeleteAllLockedUsersTest(LocalDateTime.now().minusDays(5), false);
    }

    private void performDeleteAllLockedUsersTest(LocalDateTime lastOnline, boolean shouldBeDeleted) {
        List<User> usersFakeRepository = new ArrayList<>(TestUtils.getNewUsers());
        User commonUser = usersFakeRepository.get(0);
        User lockedUser = usersFakeRepository.get(1);

        lockedUser.setLocked(true);
        lockedUser.setLastOnline(lastOnline);

        lockedUser.getFriendsList().add(commonUser);
        commonUser.getFriendsList().add(lockedUser);

        User lockedUserArtefact = lockedUser;

        when(userRepository.findAll()).thenReturn(usersFakeRepository);
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

}