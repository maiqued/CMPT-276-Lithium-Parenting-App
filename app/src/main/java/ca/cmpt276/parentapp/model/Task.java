package ca.cmpt276.parentapp.model;

public class Task {
    private String taskName;
    private String childName;
    private int currentChildID;
    private int childImgID;

    public Task(String taskName, String childName, int currentChildID, int childImgID) {
        this.taskName = taskName;
        this.childName = childName;
        this.currentChildID = currentChildID;
        this.childImgID = childImgID;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getChildName() {
        return childName;
    }

    public int getCurrentChildID() {
        return currentChildID;
    }

    public void setCurrentChildID(int currentChildID) {
        this.currentChildID = currentChildID;
    }

    public void setChildName(String currentChildName) {
        this.childName = currentChildName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getChildImgID() {
        return childImgID;
    }
}
