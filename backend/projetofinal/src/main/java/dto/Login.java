package dto;

import ENUM.Office;

public class Login {
    private int userId;
// identifies the session
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private Office office;
    private String nickname;
    private String photo;
    private String bio;
    private boolean contestManager;
    private boolean fillInfo;
    private boolean openProfile=false;

    public Login(){

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setPhoto(String photo) {
        this.photo = photo;
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
}
