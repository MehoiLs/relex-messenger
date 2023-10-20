package com.mehoil.relex.general.user.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import com.mehoil.relex.general.user.data.enums.UserRoles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @NotEmpty(message = "E-Mail cannot be empty.")
    @Email(message = "Invalid E-Mail format")
    private String email;
    @Column(name = "login", unique = true, nullable = false)
    @NotEmpty(message = "Login cannot be empty.")
    private final String login;
    @Column(name = "password", nullable = false)
    @NotEmpty(message = "Password cannot be empty.")
    private String password;

    @Column(name = "username", unique = true, nullable = false)
    @NotEmpty(message = "Username cannot be empty.")
    private String username;
    @Column(name = "first_name", nullable = false)
    @JsonProperty("first_name")
    @NotEmpty(message = "First name cannot be empty.")
    private String firstName;
    @Column(name = "last_name", nullable = false)
    @JsonProperty("last_name")
    @NotEmpty(message = "Last name cannot be empty.")
    private String lastName;

    @Column(name = "personal_status")
    private String personalStatus;
    @Column(name = "description")
    private String description;
    @Column(name = "pfp_bytes")
    private byte[] profilePictureBytes;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.DATE)
    private final LocalDate createdAt = LocalDate.now();
    @EqualsAndHashCode.Exclude
    @Column(name = "last_online", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastOnline = LocalDateTime.now();
    @Column(name = "role", nullable = false)
    private UserRoles role;

    // Security
    @Column(name = "has_active_session")
    private boolean hasActiveSession = false;
    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = false;
    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friendsList = new HashSet<>();
    @Column(name = "friends_list_is_hidden", nullable = false)
    private boolean friendsListHidden = false;
    @Column(name = "accessibility_is_friends_only", nullable = false)
    private boolean accessibilityFriendsOnly = false;

    public User(String email, String login, String password, String username, String firstName, String lastName) {
        this.email = email;
        this.login = login;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = UserRoles.USER;
    }

    public User() {
        this.email = null;
        this.login = null;
        this.password = null;
        this.username = null;
        this.firstName = null;
        this.lastName = null;
        this.role = null;
    }
}
