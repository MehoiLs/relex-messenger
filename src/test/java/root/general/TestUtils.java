package root.general;

import root.general.community.data.FriendRequest;
import root.general.main.data.User;

import java.util.List;

public final class TestUtils {

    public static User getNewDefaultUser() {
        return new User(
            "email@website.com",
            "login",
            "password",
            "username",
            "Firstname",
            "Lastname"
        );
    }
    public static List<User> getNewUsers() {
        return List.of(
                getNewDefaultUser(),
                new User(
                        "soap@mail.org",
                        "logger",
                        "passworder",
                        "nickname",
                        "Name",
                        "Surname"
                ),
                new User(
                        "shampoo@rail.ru",
                        "cawabanga",
                        "becareful",
                        "cooluniquename",
                        "John",
                        "Smith"
                )
        );
    }

    /** Will return the following relations:
     * User 1 sent request to User 2;
     * User 3 sent request to User 1;
     * none of the mentioned have any friends.
     * Remember, you must be careful while comparing users and requests returned from this method,
     * since it creates new User and FriendRequest objects when called, so the behaviour
     * is unpredictable.
     **/
    public static List<FriendRequest> getNewFriendRequests() {
        List<User> users = getNewUsers();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);
        return List.of(
                new FriendRequest(1L, user1, user2),
                new FriendRequest(2L, user3, user1)
        );
    }
}
