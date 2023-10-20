package com.mehoil.relex.general.community.services;

import com.mehoil.relex.general.TestUtils;
import com.mehoil.relex.general.features.community.common.data.FriendRequest;
import com.mehoil.relex.general.features.community.common.services.FriendRequestsService;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import org.aspectj.bridge.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.mehoil.relex.general.features.community.common.exceptions.FriendRequestException;
import com.mehoil.relex.general.features.community.common.repositories.FriendRequestsRepository;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendRequestsServiceTest {

    @Mock
    private FriendRequestsRepository friendRequestsRepository;

    @Mock
    private UserService userService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private FriendRequestsService friendRequestsService;

    @Test
    void testGetAllFriendRequests() {
        List<FriendRequest> fakeRepository = TestUtils.getNewFriendRequests();

        when(friendRequestsRepository.findAll()).thenReturn(fakeRepository);
        Set<FriendRequest> result = friendRequestsService.getAllFriendRequests();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(fakeRepository));
    }

    @Test
    void testGetAllFriendRequestsForUser() {
        User user1 = TestUtils.getNewUsers().get(0);
        User user3 = TestUtils.getNewUsers().get(2);
        FriendRequest user3ToUser1Request = new FriendRequest(user3, user1);

        when(friendRequestsRepository.findByRecipient(user1))
                .thenReturn(List.of(user3ToUser1Request));
        List<FriendRequest> result = friendRequestsService.getAllFriendRequestsForUser(user1)
                .stream().toList();

        assertEquals(1, result.size());
        assertEquals(result, List.of(user3ToUser1Request));
    }

    @Test
    void testAddFriendIfUserDoesNotExist () throws UserNotFoundException {
        String fakeUsername = "fakename";
        User user3 = TestUtils.getNewUsers().get(2);
        when(userService.getUserByUsername(fakeUsername)).thenThrow(UserNotFoundException.class);

        assertThrowsExactly(UserNotFoundException.class,
                () -> friendRequestsService.sendFriendRequest(fakeUsername, user3));
    }

    @Test
    void testSendFriendRequestIfNotFriends () throws UserNotFoundException {
        User user1 = TestUtils.getNewUsers().get(0);
        User user3 = TestUtils.getNewUsers().get(2);
        when(userService.getUserByUsername("username")).thenReturn(user1);
        when(friendRequestsRepository.existsBySenderAndRecipient(user3, user1))
                .thenReturn(false);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");

        assertDoesNotThrow(() -> friendRequestsService.sendFriendRequest(user1.getUsername(), user3));
    }

    @Test
    void testSendFriendRequestIfAlreadySentRequest () throws UserNotFoundException {
        User user1 = TestUtils.getNewUsers().get(0);
        User user3 = TestUtils.getNewUsers().get(2);
        when(userService.getUserByUsername("username")).thenReturn(user1);
        when(friendRequestsRepository.existsBySenderAndRecipient(user3, user1))
                .thenReturn(true);

        assertThrowsExactly(FriendRequestException.class,
                () -> friendRequestsService.sendFriendRequest(user1.getUsername(), user3));
    }

    @Test
    void testSendFriendRequestIfAlreadyFriends () throws UserNotFoundException {
        User user1 = TestUtils.getNewUsers().get(0);
        User user3 = TestUtils.getNewUsers().get(2);
        user1.getFriendsList().add(user3);
        user3.getFriendsList().add(user1);
        when(userService.getUserByUsername("username")).thenReturn(user1);

        assertThrowsExactly(FriendRequestException.class,
                () -> friendRequestsService.sendFriendRequest(user1.getUsername(), user3));
    }

    @Test
    void testAcceptFriendRequestIfNotSent () throws UserNotFoundException {
        User user2 = TestUtils.getNewUsers().get(1);
        User user1Acceptor = TestUtils.getNewUsers().get(0);

        when(userService.getUserByUsername("nickname")).thenReturn(user2);
        when(friendRequestsRepository.findBySenderAndRecipient(user2, user1Acceptor))
                .thenReturn(Optional.empty());

        assertThrowsExactly(FriendRequestException.class,
                () -> friendRequestsService.acceptFriendRequest(user2.getUsername(), user1Acceptor));
    }

    @Test
    void testAcceptFriendRequest() throws UserNotFoundException {
        User user3Sender = TestUtils.getNewUsers().get(2);
        User user1Acceptor = TestUtils.getNewUsers().get(0);
        FriendRequest user3ToUser1Request = new FriendRequest(user3Sender, user1Acceptor);

        when(userService.getUserByUsername("cooluniquename")).thenReturn(user3Sender);
        when(friendRequestsRepository.findBySenderAndRecipient(user3Sender, user1Acceptor))
                .thenReturn(Optional.of(user3ToUser1Request));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");
        doAnswer(invocation -> {
            User acceptorUser = invocation.getArgument(0);
            User senderUser = invocation.getArgument(1);
            acceptorUser.getFriendsList().add(senderUser);
            senderUser.getFriendsList().add(acceptorUser);
            return null;
        }).when(userService).addFriend(user1Acceptor, user3Sender);

        assertDoesNotThrow(() -> friendRequestsService.acceptFriendRequest(user3Sender.getUsername(), user1Acceptor));

        assertTrue(user1Acceptor.getFriendsList().contains(user3Sender));
        assertTrue(user3Sender.getFriendsList().contains(user1Acceptor));
    }

    @Test
    void testAcceptFriendRequestByNonExistingUsername() throws UserNotFoundException {
        String senderFakeUsername = "fakeusername";
        User user1Acceptor = TestUtils.getNewUsers().get(0);
        when(userService.getUserByUsername(senderFakeUsername)).thenThrow(UserNotFoundException.class);

        assertThrowsExactly(UserNotFoundException.class,
                () -> friendRequestsService.acceptFriendRequest(senderFakeUsername, user1Acceptor));
    }

    @Test
    void testAcceptAllFriendRequests() {
        User user3Sender = TestUtils.getNewUsers().get(2);
        User user1Acceptor = TestUtils.getNewUsers().get(0);
        FriendRequest user3ToUser1Request = new FriendRequest(user3Sender, user1Acceptor);

        when(friendRequestsRepository.findByRecipient(user1Acceptor))
                .thenReturn(List.of(user3ToUser1Request));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");
        doAnswer(invocation -> {
            User acceptorUser = invocation.getArgument(0);
            User senderUser = invocation.getArgument(1);
            acceptorUser.getFriendsList().add(senderUser);
            senderUser.getFriendsList().add(acceptorUser);
            return null;
        }).when(userService).addFriend(user1Acceptor, user3Sender);

        friendRequestsService.acceptAllFriendRequests(user1Acceptor);

        verify(userService, times(1)).addFriend(any(), any());
        assertTrue(user1Acceptor.getFriendsList().contains(user3Sender));
        assertTrue(user3Sender.getFriendsList().contains(user1Acceptor));
    }

    @Test
    void testDenyFriendRequest() throws UserNotFoundException {
        User user3Sender = TestUtils.getNewUsers().get(2);
        User user1Acceptor = TestUtils.getNewUsers().get(0);
        FriendRequest user3ToUser1Request = new FriendRequest(user3Sender, user1Acceptor);

        when(userService.getUserByUsername("cooluniquename")).thenReturn(user3Sender);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("text");
        when(friendRequestsRepository.findBySenderAndRecipient(user3Sender, user1Acceptor))
                .thenReturn(Optional.of(user3ToUser1Request));

        assertDoesNotThrow(() -> friendRequestsService
                .denyFriendRequest(user3Sender.getUsername(), user1Acceptor));
    }


}