package ENUM;

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
