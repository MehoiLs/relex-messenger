package com.mehoil.relex.general.features.community.common.services;

import com.mehoil.relex.general.features.community.common.repositories.UserFriendsRepository;
import com.mehoil.relex.general.user.data.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserFriendsService {

    private final UserFriendsRepository userFriendsRepository;

    public UserFriendsService(UserFriendsRepository userFriendsRepository) {
        this.userFriendsRepository = userFriendsRepository;
    }

    @Transactional
    public void deleteFriends(User user1, User user2) {
        userFriendsRepository.deleteByUserAndFriend(user1, user2);
        userFriendsRepository.deleteByUserAndFriend(user2, user1);
    }

    @Transactional
    public void deleteAllFriendsOfUser(User user) {
        user.getFriendsList().forEach(friend -> {
            deleteFriends(user, friend);
        });
    }

}
