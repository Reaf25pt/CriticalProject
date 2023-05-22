package dto;

import java.util.Date;

public class TaskGantt {

    private int id;
    private String title;
    private Date startDate;
    private Date finishDate;

    public TaskGantt(int id, String title, Date startDate, Date finishDate) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}
