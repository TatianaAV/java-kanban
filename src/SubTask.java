public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, String status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }




    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
