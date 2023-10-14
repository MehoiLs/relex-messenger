package root.general.community.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import root.general.TestUtils;
import root.general.community.data.FriendRequest;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.messaging.services.ChatMessageService;

import java.io.NotActiveException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCommunityServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserFriendsService userFriendsService;

    @InjectMocks
    private UserCommunityService userCommunityService;

    @Test
    void testGetUserFriendsListByUsername () throws UserNotFoundException {
        List<User> users = TestUtils.getNewUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);
        user1.getFriendsList().add(user2);
        user2.getFriendsList().add(user1);
        user1.getFriendsList().add(user3);
        user3.getFriendsList().add(user1);
        user1.setFriendsListHidden(true);

        when(userService.getUserByUsername(user1.getUsername())).thenReturn(user1);
        List<String> result = userCommunityService.getUserFriendsList(user1.getUsername());

        assertEquals(List.of(), result);
    }

    @Test
    void testGetAllFriends() throws UserNotFoundException {
        List<User> users = TestUtils.getNewUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);
        user1.getFriendsList().add(user2);
        user2.getFriendsList().add(user1);
        user1.getFriendsList().add(user3);
        user3.getFriendsList().add(user1);

        when(userService.getUserByUsername(user1.getUsername())).thenReturn(user1);
        List<String> result = userCommunityService.getUserFriendsList(user1.getUsername());

        assertTrue(result.containsAll(List.of(user2.getUsername(), user3.getUsername()))
                && result.size() == 2);
    }

    @Test
    void testRemoveFriend() throws UserNotFoundException {
        List<User> users = TestUtils.getNewUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);
        user1.getFriendsList().add(user2);
        user2.getFriendsList().add(user1);
        user1.getFriendsList().add(user3);
        user3.getFriendsList().add(user1);

        when(userService.getUserByUsername(user2.getUsername())).thenReturn(user2);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            User friend = invocation.getArgument(1);
            user.getFriendsList().remove(friend);
            friend.getFriendsList().remove(user);
            return null;
        }).when(userFriendsService).deleteFriends(user1, user2);

        assertDoesNotThrow(() -> userCommunityService.removeFriend(user2.getUsername(), user1));

        assertFalse(user1.getFriendsList().contains(user2));
        assertEquals(1, user1.getFriendsList().size());
    }

    @Test
    void testRemoveFriendWrongUsername() throws UserNotFoundException {
        List<User> users = TestUtils.getNewUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);
        user1.getFriendsList().add(user2);
        user2.getFriendsList().add(user1);
        user1.getFriendsList().add(user3);
        user3.getFriendsList().add(user1);

        when(userService.getUserByUsername("fakeusername")).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class,
                () -> userCommunityService.removeFriend("fakeusername", user1));
    }
}