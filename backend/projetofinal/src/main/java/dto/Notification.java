package dto;

import java.util.Date;

public class Notification {


    private int id;
    private Date creationTime;
    private String message;
    private boolean seen;
    private boolean needsInput;
    private int relationId;


    public Notification(int id, Date creationTime, String message, boolean seen, boolean needsInput, int relationId) {
        this.id = id;
        this.creationTime = creationTime;
        this.message = message;
        this.seen = seen;
        this.needsInput = needsInput;
        this.relationId = relationId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isNeedsInput() {
        return needsInput;
    }

    public void setNeedsInput(boolean needsInput) {
        this.needsInput = needsInput;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }
}
