package org.tucn.pt.model;

import java.io.Serializable;

public sealed abstract class Task implements Serializable permits SimpleTask,ComplexTask{

    protected int idTask;
    private ComplexTask partOf;
    protected String statusTask;

    public Task(int idTask, String statusTask) {
        this.idTask = idTask;
        this.statusTask = statusTask;
    }

    public ComplexTask getPartOf() {
        return partOf;
    }

    public void setPartOf(ComplexTask partOf) {
        this.partOf = partOf;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(String statusTask) {
        this.statusTask = statusTask;
    }

    public abstract int estimateDuration();
}
