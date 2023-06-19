package ENUM;

public enum StatusProject {
    PLANNING("Planning"),
    READY("Ready"),
    PROPOSED("Proposed to Contest"),
    APPROVED("Approved to Contest"),
    PROGRESS("In Progress"),
    CANCELLED("Cancelled"),
    FINISHED("Finished");


    private final String status;

    private StatusProject(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
