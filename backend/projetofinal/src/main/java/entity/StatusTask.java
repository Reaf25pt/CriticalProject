package entity;

public enum StatusTask {

    PLANNED("Planned"),
    PROGRESS("In Progress"),
    FINISHED("Finished");

    private final String status;

    private StatusTask(String status) {
        this.status = status;
    }
}
