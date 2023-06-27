package dto;

public class PotentialProjMember {
    private int id;
    private int projectId;

    private int userInvitedId;
    private String userInvitedFirstName;
    private String userInvitedLastName;
    private String userInvitedPhoto;
    private boolean selfInvitation;

    private boolean answered;



    public PotentialProjMember() {
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

    public boolean isSelfInvitation() {
        return selfInvitation;
    }

    public void setSelfInvitation(boolean selfInvitation) {
        this.selfInvitation = selfInvitation;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
