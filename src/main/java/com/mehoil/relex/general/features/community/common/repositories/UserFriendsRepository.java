package com.mehoil.relex.general.features.community.common.repositories;

import com.mehoil.relex.general.features.community.common.data.UserFriends;
import com.mehoil.relex.general.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFriendsRepository extends JpaRepository<UserFriends, Long> {
    void deleteByUserAndFriend(User user, User friend);
}
