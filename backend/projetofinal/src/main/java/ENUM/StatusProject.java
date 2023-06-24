package ENUM;

public enum StatusProject {
    PLANNING("Planning"), //0
    READY("Ready"), //1
    PROPOSED("Proposed to Contest"), //2
    APPROVED("Approved to Contest"), //3
    PROGRESS("In Progress"),  //4
    CANCELLED("Cancelled"), //5
    FINISHED("Finished"); //6 


    private final String status;

    private StatusProject(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
