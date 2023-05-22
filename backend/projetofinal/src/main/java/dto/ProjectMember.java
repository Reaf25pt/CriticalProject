package dto;

import entity.StatusProject;

public class ProjectMember {
    private int id;
    private int projectId;

    private int userInvitedId;
    private String userInvitedFirstName;
    private String userInvitedLastName;
    private String userInvitedPhoto;

    private boolean manager;

    public ProjectMember(int id, int projectId, int userInvitedId, String userInvitedFirstName, String userInvitedLastName, String userInvitedPhoto, boolean manager) {
        this.id = id;
        this.projectId = projectId;
        this.userInvitedId = userInvitedId;
        this.userInvitedFirstName = userInvitedFirstName;
        this.userInvitedLastName = userInvitedLastName;
        this.userInvitedPhoto = userInvitedPhoto;
        this.manager = manager;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getUserInvitedId() {
        return userInvitedId;
    }

    public void setUserInvitedId(int userInvitedId) {
        this.userInvitedId = userInvitedId;
    }

    public String getUserInvitedFirstName() {
        return userInvitedFirstName;
    }

    public void setUserInvitedFirstName(String userInvitedFirstName) {
        this.userInvitedFirstName = userInvitedFirstName;
    }

    public String getUserInvitedLastName() {
        return userInvitedLastName;
    }

    public void setUserInvitedLastName(String userInvitedLastName) {
        this.userInvitedLastName = userInvitedLastName;
    }

    public String getUserInvitedPhoto() {
        return userInvitedPhoto;
    }

    public void setUserInvitedPhoto(String userInvitedPhoto) {
        this.userInvitedPhoto = userInvitedPhoto;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }
}
