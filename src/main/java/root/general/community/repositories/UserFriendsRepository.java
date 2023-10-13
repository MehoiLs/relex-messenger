package root.general.community.repositories;

import org.springframework.data.repository.CrudRepository;
import root.general.community.data.UserFriends;
import root.general.main.data.User;

public interface UserFriendsRepository extends CrudRepository<UserFriends, Long> {
    void deleteByUserAndFriend(User user, User friend);
}
