public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected String status;

    public Task( String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task( String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }
}
