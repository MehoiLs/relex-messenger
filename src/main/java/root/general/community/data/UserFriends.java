package root.general.community.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Value;
import root.general.main.data.User;

@Value
@Entity
@AllArgsConstructor
@Table(name = "user_friends")
public class UserFriends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    User friend;

    public UserFriends() {
        this.id = null;
        this.user = null;
        this.friend = null;
    }
}
