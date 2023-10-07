package root.main.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import root.main.data.enums.UserRoles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
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

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.DATE)
    private final LocalDate createdAt = LocalDate.now();
    @Column(name = "role", nullable = false)
    private UserRoles role;

    // Security
    @Column(name = "has_active_session")
    private boolean hasActiveSession = false;
    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = false;
    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friendsList = new ArrayList<>();

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
