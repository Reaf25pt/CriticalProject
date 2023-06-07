package dto;

public class Hobby {

    private int id;
    private String title;

    public Hobby(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Hobby() {
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
}
