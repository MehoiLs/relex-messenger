package root.general.community.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import root.general.TestUtils;
import root.general.community.data.FriendRequest;
import root.general.community.exception.FriendRequestException;
import root.general.community.repositories.FriendRequestsRepository;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;

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
        FriendRequest user3ToUser1Request = new FriendRequest(2L, user3, user1);

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
        FriendRequest user3ToUser1Request = new FriendRequest(2L, user3Sender, user1Acceptor);

        when(userService.getUserByUsername("cooluniquename")).thenReturn(user3Sender);
        when(friendRequestsRepository.findBySenderAndRecipient(user3Sender, user1Acceptor))
                .thenReturn(Optional.of(user3ToUser1Request));
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
        FriendRequest user3ToUser1Request = new FriendRequest(2L, user3Sender, user1Acceptor);

        when(friendRequestsRepository.findByRecipient(user1Acceptor))
                .thenReturn(List.of(user3ToUser1Request));
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
        FriendRequest user3ToUser1Request = new FriendRequest(2L, user3Sender, user1Acceptor);

        when(userService.getUserByUsername("cooluniquename")).thenReturn(user3Sender);
        when(friendRequestsRepository.findBySenderAndRecipient(user3Sender, user1Acceptor))
                .thenReturn(Optional.of(user3ToUser1Request));

        assertDoesNotThrow(() -> friendRequestsService
                .denyFriendRequest(user3Sender.getUsername(), user1Acceptor));
    }


}