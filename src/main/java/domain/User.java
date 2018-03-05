package domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.enterprise.inject.Model;
import javax.json.bind.annotation.JsonbTransient;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Model
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
    ,
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email LIKE :email")
    ,
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username LIKE :username")
})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true)
    private String username;
    private String picture;
    private String website;
    private String firstName;
    private String lastName;
    private String bio;
    private String location;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private UserRole userRole = UserRole.USER;
    @OneToMany(mappedBy = "tweetedBy", cascade = ALL)
    private final Set<Tweet> tweets = new HashSet<>();
    @ManyToMany
    private final Set<User> following = new HashSet<>();
    @ManyToMany(mappedBy = "following")
    private final Set<User> followers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getPicture() {
        return picture;
    }

    public String getWebsite() {
        return website;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    @JsonbTransient
    public Set<Tweet> getTweets() {
        return Collections.unmodifiableSet(tweets);
    }

    @JsonbTransient
    public Set<User> getFollowing() {
        return Collections.unmodifiableSet(following);
    }

    @JsonbTransient
    public Set<User> getFollowers() {
        return Collections.unmodifiableSet(followers);
    }

    public String getUsername() {
        return this.username;
    }

    public User() {

    }

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public User(String email, String username, String password, String picture, String website, String firstName, String lastName, String bio, String location) {
        this(email, username, password);
        this.picture = picture;
        this.website = website;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.location = location;

    }

    public Tweet tweet(String message) {
        Tweet tweet = new Tweet(message, this);
        this.tweets.add(tweet);
        return tweet;
    }

    public boolean follow(User user) {
        if (user == null || user == this) {
            return false;
        }

        user.addFollower(this);

        return following.add(user);
    }

    public boolean unfollow(User user) {
        user.removeFollower(this);
        return following.remove(user);
    }

    private void addFollower(User user) {
        followers.add(user);
    }

    private boolean removeFollower(User user) {
        return followers.remove(user);
    }

    public UserRole promote() {
        switch (userRole) {
            case USER:
                userRole = UserRole.MODERATOR;
                break;
            case MODERATOR:
                userRole = UserRole.ADMIN;
                break;
            case ADMIN:
                break;
            default:
                break;
        }

        return userRole;
    }

    public UserRole demote() {
        switch (userRole) {
            case MODERATOR:
                userRole = UserRole.USER;
                break;
            case ADMIN:
                userRole = UserRole.MODERATOR;
                break;
            case USER:
                break;
            default:
                break;
        }

        return userRole;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.picture);
        hash = 59 * hash + Objects.hashCode(this.website);
        hash = 59 * hash + Objects.hashCode(this.firstName);
        hash = 59 * hash + Objects.hashCode(this.lastName);
        hash = 59 * hash + Objects.hashCode(this.bio);
        hash = 59 * hash + Objects.hashCode(this.location);
        hash = 59 * hash + Objects.hashCode(this.email);
        hash = 59 * hash + Objects.hashCode(this.password);
        hash = 59 * hash + Objects.hashCode(this.userRole);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return Objects.equals(this.email, other.email);
    }

}
