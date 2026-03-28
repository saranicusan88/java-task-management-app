package org.tucn.pt.dataAccess;

import org.tucn.pt.business.TaskManagement;
import org.tucn.pt.model.Task;

import java.io.Serializable;
import java.util.List;

public class AppData implements Serializable {
    private static final long serialVersionUID = 1L;//ca sa nu crape daca modificam ceva

    private TaskManagement management;
    private List<Task> createdTasks;

    public AppData(TaskManagement management, List<Task> createdTasks) {
        this.management = management;
        this.createdTasks = createdTasks;
    }

    public TaskManagement getManagement() { return management; }
    public void setManagement(TaskManagement management) { this.management = management; }
    public List<Task> getCreatedTasks() { return createdTasks; }
    public void setCreatedTasks(List<Task> createdTasks) { this.createdTasks = createdTasks; }
}