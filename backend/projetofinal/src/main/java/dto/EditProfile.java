package dto;

import ENUM.Office;

public class EditProfile {
    private int id;

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String office;
    //TODO office provavelmente tem de ser definido como int
    private int officeInfo;
    private String nickname;
    private String photo;
    private String bio;
    private boolean openProfile;

    private boolean fillInfo;

    public EditProfile(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
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

    public boolean isOpenProfile() {
        return openProfile;
    }

    public void setOpenProfile(boolean openProfile) {
        this.openProfile = openProfile;
    }


    public int getOfficeInfo() {
        return officeInfo;
    }

    public void setOfficeInfo(int officeInfo) {
        this.officeInfo = officeInfo;
    }


    public boolean isFillInfo() {
        return fillInfo;
    }

    public void setFillInfo(boolean fillInfo) {
        this.fillInfo = fillInfo;
    }

}
