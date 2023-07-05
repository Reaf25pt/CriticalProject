package ENUM;

/**
 * Represents the possible values attribute contest Status can have
 */
public enum StatusContest {
    PLANNING("Planning"), //0
    OPEN("Open"), //1
    ONGOING("Ongoing"), //2
    CONCLUDED("Concluded"); //3

    private final String status;

    private StatusContest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
