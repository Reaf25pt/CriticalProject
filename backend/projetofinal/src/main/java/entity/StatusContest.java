package entity;

public enum StatusContest {
    PLANNING("Planning"),
    OPEN("Open"),
    ONGOING("Ongoing"),
    CONCLUDED("Concluded");

    private final String status;

    private StatusContest(String status) {
        this.status = status;
    }
}
