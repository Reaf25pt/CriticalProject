package ENUM;
/**
 * Represents the possible values attribute task Status can have
 */
public enum StatusTask {

    PLANNED("Planned"), //0
    PROGRESS("In Progress"), //1
    FINISHED("Finished"); //2

    private final String status;

    private StatusTask(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
