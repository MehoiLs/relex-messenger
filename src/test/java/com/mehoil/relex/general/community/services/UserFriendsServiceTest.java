package com.mehoil.relex.general.community.services;

import com.mehoil.relex.general.features.community.common.services.UserFriendsService;
import com.mehoil.relex.general.user.data.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.mehoil.relex.general.TestUtils;
import com.mehoil.relex.general.features.community.common.repositories.UserFriendsRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class UserFriendsServiceTest {

    @Mock
    private UserFriendsRepository userFriendsRepository;

    @InjectMocks
    private UserFriendsService userFriendsService;

    @Test
    void deleteFriends() {
        List<User> users = TestUtils.getNewUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            User friend = invocation.getArgument(1);
            user.getFriendsList().remove(friend);
            friend.getFriendsList().remove(user);
            return null;
        }).when(userFriendsRepository).deleteByUserAndFriend(user1, user2);

        userFriendsService.deleteFriends(user1, user2);

        assertFalse(user1.getFriendsList().contains(user2));
        assertFalse(user2.getFriendsList().contains(user1));
    }

    @Test
    void deleteAllFriendsOfUser() {
        List<User> users = TestUtils.getNewUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);
        user1.getFriendsList().add(user2);
        user1.getFriendsList().add(user3);
        user2.getFriendsList().add(user1);
        user3.getFriendsList().add(user1);

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            User friend = invocation.getArgument(1);
            user.getFriendsList().remove(friend);
            friend.getFriendsList().remove(user);
            return null;
        }).when(userFriendsRepository).deleteByUserAndFriend(user1, user2);

        userFriendsService.deleteFriends(user1, user2);

        assertFalse(user1.getFriendsList().contains(user2));
        assertFalse(user2.getFriendsList().contains(user1));
    }
}