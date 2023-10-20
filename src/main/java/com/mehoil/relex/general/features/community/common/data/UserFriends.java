package com.mehoil.relex.general.features.community.common.data;

import com.mehoil.relex.general.user.data.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "user_friends")
public class UserFriends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private final User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private final User friend;

    public UserFriends() {
        this.id = null;
        this.user = null;
        this.friend = null;
    }
}
