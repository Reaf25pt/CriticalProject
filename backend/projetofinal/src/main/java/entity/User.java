package entity;

import ENUM.Office;
import jakarta.persistence.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "User")
@NamedQuery(name = "User.findUserByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
@NamedQuery(name = "User.findUserByTokenForActivationOrRecoverPass", query = "SELECT u FROM User u WHERE u.token = :token")
@NamedQuery(name = "User.findUserById", query = "SELECT u FROM User u WHERE u.userId = :userId")


public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false, unique = true, updatable = false)
    private int userId;

    //email pode ser updated para estar preparado para um admin do sistema o fazer
    @Column(name = "email", nullable = false, unique = true, updatable = true)
    private String email;

    @Column(name = "password", nullable = false, unique = false, updatable = true)
    private String password;

    @Column(name = "firstName", nullable = true, unique = false, updatable = true)
    private String firstName;

    @Column(name = "lastName", nullable = true, unique = false, updatable = true)
    private String lastName;

    @Column(name = "office", nullable = true, unique = false, updatable = true)
    private Office office;

    @Column(name = "nickname", nullable = true, unique = false, updatable = true)
    private String nickname;

    @Column(name = "photo", nullable = true, unique = false, updatable = true)
    private String photo;

    @Column(name = "bio", nullable = true, unique = false, updatable = true)
    private String bio;

    @Column(name = "contestManager", nullable = false, unique = false, updatable = true)
    private boolean contestManager;

    @Column(name = "openProfile", nullable = false, unique = false, updatable = true)
    private boolean openProfile = false;

    // coluna que permite redireccionar no frontend no 1º login para 1 página específica para preencher os restantes dados pessoais
    @Column(name = "fillInfo", nullable = false, unique = false, updatable = true)
    private boolean fillInfo = false;  // TODO não esquecer

    @Column(name = "validated", nullable = false, unique = false, updatable = true)
    private boolean validated = false;

    @Column(name = "token", nullable = true, unique = false, updatable = true)
    private String token;

    @Column(name = "timestampForToken", nullable = true, unique = false, updatable = true)
    private long timestampForToken;

    //list of all sessions
    @OneToMany(mappedBy = "tokenOwner")
    private List<Token> userSessions = new ArrayList<>();

    //list of all notifications
    @OneToMany(mappedBy = "notificationOwner")
    private List<Notification> userNotificationList = new ArrayList<>();

    //list of all received messages
    @OneToMany(mappedBy = "messageReceiver")
    private List<PersonalMessage> receivedMsgList = new ArrayList<>();

    @ManyToMany(mappedBy = "listUsers_Hobbies")
    private List<Hobby> listHobbies = new ArrayList<>();

    @ManyToMany(mappedBy = "listUsers_Skills")
    private List<Skill> listSkills = new ArrayList<>();

    @OneToMany(mappedBy = "userInvited")
    private List<ProjectMember> listProjects = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<ProjectHistory> listRecords = new ArrayList<>();

    public User() {

    }

    public User(int userId, String email, String password, String firstName, String lastName, Office office, String nickname, String photo, String bio, boolean contestManager, boolean openProfile, boolean validated, String token, long timestampForToken, List<Token> userSessions, List<Notification> userNotificationList, List<PersonalMessage> receivedMsgList, List<Hobby> listHobbies, List<Skill> listSkills, List<ProjectMember> listProjects, List<ProjectHistory> listRecords) {
        this.userId = userId;
        this.email = email;
        this.password = this.passMask(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.office = office;
        this.nickname = nickname;
        this.photo = photo;
        this.bio = bio;
        this.contestManager = contestManager;
        this.openProfile = openProfile;
        this.validated = validated;
        this.token = token;
        this.timestampForToken = timestampForToken;
        this.userSessions = userSessions;
        this.userNotificationList = userNotificationList;
        this.receivedMsgList = receivedMsgList;
        this.listHobbies = listHobbies;
        this.listSkills = listSkills;
        this.listProjects = listProjects;
        this.listRecords = listRecords;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photoUrl) {
        this.photo = photoUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isContestManager() {
        return contestManager;
    }

    public void setContestManager(boolean contestManager) {
        this.contestManager = contestManager;
    }

    public boolean isOpenProfile() {
        return openProfile;
    }

    public void setOpenProfile(boolean openProfile) {
        this.openProfile = openProfile;
    }

    public boolean isFillInfo() {
        return fillInfo;
    }

    public void setFillInfo(boolean fillInfo) {
        this.fillInfo = fillInfo;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTimestampForToken() {
        return timestampForToken;
    }

    public void setTimestampForToken(long timestampForToken) {
        this.timestampForToken = timestampForToken;
    }

    public List<Token> getUserSessions() {
        return userSessions;
    }

    public void setUserSessions(List<Token> userSessions) {
        this.userSessions = userSessions;
    }

    public List<Notification> getUserNotificationList() {
        return userNotificationList;
    }

    public void setUserNotificationList(List<Notification> userNotificationList) {
        this.userNotificationList = userNotificationList;
    }

    public List<PersonalMessage> getReceivedMsgList() {
        return receivedMsgList;
    }

    public void setReceivedMsgList(List<PersonalMessage> receivedMsgList) {
        this.receivedMsgList = receivedMsgList;
    }

    public List<Hobby> getListHobbies() {
        return listHobbies;
    }

    public void setListHobbies(List<Hobby> listHobbies) {
        this.listHobbies = listHobbies;
    }

    public List<Skill> getListSkills() {
        return listSkills;
    }

    public void setListSkills(List<Skill> listSkills) {
        this.listSkills = listSkills;
    }

    public List<ProjectMember> getListProjects() {
        return listProjects;
    }

    public void setListProjects(List<ProjectMember> listProjects) {
        this.listProjects = listProjects;
    }

    public List<ProjectHistory> getListRecords() {
        return listRecords;
    }

    public void setListRecords(List<ProjectHistory> listRecords) {
        this.listRecords = listRecords;
    }

    // mascara a password introduzida
    public String passMask(String password) {

        return DigestUtils.md5Hex(password).toUpperCase();
    }


    public String createTokenForActivation() {

        long newtoken = System.currentTimeMillis();
        String tokenString = this.firstName + newtoken;
        this.token = tokenString;
        return this.token;
    }

    public String createTokenToRecoverPassword() {

        long newtoken = System.currentTimeMillis();
        String tokenString = this.email + newtoken;
        this.token = tokenString;
        return this.token;
    }


    public long createTimeoutTimeStamp() {

        //final int timeOutForToken = 300000; // 5min
        //long timestamp = Calendar.getInstance().getTimeInMillis() + timeOutSession;
        long timestamp = Calendar.getInstance().getTimeInMillis();

        return timestamp;
    }

}
