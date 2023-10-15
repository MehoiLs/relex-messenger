package root.general.community.services;

import org.springframework.stereotype.Service;
import root.general.community.repositories.UserFriendsRepository;
import root.general.main.data.User;

@Service
public class UserFriendsService {

    private final UserFriendsRepository userFriendsRepository;

    public UserFriendsService(UserFriendsRepository userFriendsRepository) {
        this.userFriendsRepository = userFriendsRepository;
    }

    public void deleteFriends(User user1, User user2) {
        userFriendsRepository.deleteByUserAndFriend(user1, user2);
        userFriendsRepository.deleteByUserAndFriend(user2, user1);
    }

    public void deleteAllFriendsOfUser(User user) {
        user.getFriendsList().forEach(friend -> {
            deleteFriends(user, friend);
        });
    }

}
