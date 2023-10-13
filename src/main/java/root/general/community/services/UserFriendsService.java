package root.general.community.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.community.repositories.UserFriendsRepository;
import root.general.main.data.User;
import root.general.main.services.user.UserService;

@Service
public class UserFriendsService {

    private final UserFriendsRepository userFriendsRepository;

    @Autowired
    public UserFriendsService(UserFriendsRepository userFriendsRepository) {
        this.userFriendsRepository = userFriendsRepository;
    }

    public void deleteFriends(User user1, User user2) {
        userFriendsRepository.deleteByUserAndFriend(user1, user2);
        userFriendsRepository.deleteByUserAndFriend(user2, user1);
    }

}
