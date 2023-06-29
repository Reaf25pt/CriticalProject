package dto;

import java.io.Serializable;
import java.util.List;

public class AnotherProfile {

    private int id;

    private String email;
    private String firstName;
    private String lastName;
    private String office;
    private int officeInfo;
    private String nickname;
    private String photo;
    private String bio;
    private boolean openProfile;


    private List<Hobby> hobbies;

    private List<Skill> skills;

    private List<ProjectMinimal> projects;


    public AnotherProfile() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public int getOfficeInfo() {
        return officeInfo;
    }

    public void setOfficeInfo(int officeInfo) {
        this.officeInfo = officeInfo;
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


    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<ProjectMinimal> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectMinimal> projects) {
        this.projects = projects;
    }
}
