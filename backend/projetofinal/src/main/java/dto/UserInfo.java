package dto;

public class UserInfo {

    private int id;
    private String firstName;
    private String lastName;
    private String nickname;
    private String photo;

private boolean openProfile;

    public UserInfo(int id, String firstName, String lastName, String nickname, String photo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.photo = photo;
    }

    public UserInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isOpenProfile() {
        return openProfile;
    }

    public void setOpenProfile(boolean openProfile) {
        this.openProfile = openProfile;
    }
}
