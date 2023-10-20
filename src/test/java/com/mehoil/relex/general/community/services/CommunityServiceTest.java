package com.mehoil.relex.general.community.services;

import com.mehoil.relex.general.TestUtils;
import com.mehoil.relex.general.features.community.common.data.dto.UserFriendListDTO;
import com.mehoil.relex.general.features.community.common.exceptions.UserPrivacyException;
import com.mehoil.relex.general.features.community.common.services.CommunityService;
import com.mehoil.relex.general.features.community.common.services.UserFriendsService;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserFriendsService userFriendsService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CommunityService communityService;

    @Test
    void testGetUserFriendsListByUsername () throws UserNotFoundException, UserPrivacyException {
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
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");

        assertThrowsExactly(UserPrivacyException.class,
                () -> communityService.getUserFriendsByUsernameAsDto(user1.getUsername()));
    }

    @Test
    void testGetAllFriends() throws UserNotFoundException, UserPrivacyException {
        List<User> users = TestUtils.getNewUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);
        user1.getFriendsList().add(user2);
        user2.getFriendsList().add(user1);
        user1.getFriendsList().add(user3);
        user3.getFriendsList().add(user1);

        when(userService.getUserByUsername(user1.getUsername())).thenReturn(user1);
        UserFriendListDTO result = communityService.getUserFriendsByUsernameAsDto(user1.getUsername());

        assertTrue(result.getFriendsList().containsAll(List.of(user2.getUsername(), user3.getUsername()))
                && result.getFriendsList().size() == 2);
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
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");
        when(userService.userIsFriendsWith(any(), any())).thenReturn(true);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            User friend = invocation.getArgument(1);
            user.getFriendsList().remove(friend);
            friend.getFriendsList().remove(user);
            return null;
        }).when(userFriendsService).deleteFriends(user1, user2);

        assertDoesNotThrow(() -> communityService.removeFriend(user2.getUsername(), user1));

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
                () -> communityService.removeFriend("fakeusername", user1));
    }
}