package org.tucn.pt.model;
import java.util.ArrayList;
import java.util.List;

public final class ComplexTask extends Task {

    private List<Task> tasks;

    public ComplexTask(int idTask, String statusTask) {
        super(idTask, statusTask);
        tasks = new ArrayList<Task>();
    }

    public int estimateDuration() {
        int sum = 0;
        for (Task t : tasks) {
            sum += t.estimateDuration();
        }
        return sum;
    }

    public void updateStatus() {
        for (Task task : tasks) {
            if (!"Completed".equals(task.getStatusTask())) { //daca 1 subtask incompl atunci task incomplet
                this.setStatusTask("Uncompleted");
                return;
            }
        }
        this.setStatusTask("Completed");
    }

    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
            task.setPartOf(this);
        }
        updateStatus();
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
        task.setPartOf(null);
        updateStatus();
    }

    @Override
    public String toString() {
        return "ComplexTask{idTask=" + idTask +
                ", statusTask='" + statusTask + '\'' +
                ", duration=" + estimateDuration() + '}';
    }
}

