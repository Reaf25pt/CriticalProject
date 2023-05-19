package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="User")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="userId", nullable = false, unique = true, updatable = false )
    private int userId;

    //email pode ser updated para estar preparado para um admin do sistema o fazer
    @Column(name="email", nullable = false, unique = true, updatable = true)
    private String email;

    @Column(name="password", nullable = false, unique = false, updatable = true)
    private String password;

    @Column(name="firstName", nullable = false, unique = false, updatable = true)
    private String firstName;

    @Column(name="lastName", nullable = false, unique = false, updatable = true)
    private String lastName;

    @Column(name="office", nullable = false, unique = false, updatable = true)
    private Office office;

    @Column(name="nickname", nullable = true, unique = false, updatable = true)
    private String nickname;

    @Column(name="photo", nullable = true, unique = false, updatable = true)
    private String photoUrl;

    @Column(name="bio", nullable = true, unique = false, updatable = true)
    private String bio;

    @Column(name="contestManager", nullable = false, unique = false, updatable = true)
    private boolean contestManager;

    @Column(name="openProfile", nullable = false, unique = false, updatable = true)
    private boolean openProfile=false;

    @Column(name="validated", nullable = false, unique = false, updatable = true)
    private boolean validated = false;

    @Column(name="token", nullable=true, unique = false, updatable = true)
    private String token;

    @Column (name="timestampForToken", nullable=true, unique = false, updatable = true )
    private long timestampForToken;

    //list of all sessions
    @OneToMany(mappedBy = "tokenOwner")
    private List<Token> userSessions = new ArrayList<>();

    //list of all notifications
    @OneToMany(mappedBy = "notificationOwner")
    private List<Notification> userNotificationList = new ArrayList<>();

    //list of all received messages
    @OneToMany(mappedBy="messageReceiver")
    private List<PersonalMessage> receivedMsgList = new ArrayList<>();

    @ManyToMany(mappedBy = "listUsers_Hobbies")
    private List<Hobby> listHobbies = new ArrayList<>();

    @ManyToMany(mappedBy = "listUsers_Skills")
    private List<Skill> listSkills = new ArrayList<>();



    public User(){

}

}
