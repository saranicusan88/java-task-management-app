package org.tucn.pt.business;

import org.tucn.pt.model.Employee;
import org.tucn.pt.model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagement implements Serializable {
    private Map<Employee, List<Task>> map; //emp1->t1,t2
                                           //emp2->t3,t4
    public TaskManagement() {
        this.map = new HashMap<>();
    }

    public Map<Employee, List<Task>> getMap() {
        return map;
    }

    public void setMap(Map<Employee, List<Task>> map) {
        this.map = map;
    }

    public void addEmployee(Employee employee) {
        if (employee != null && !map.containsKey(employee)) { //daca exista si nu e in map
            map.put(employee, new ArrayList<>());
        }
    }

    public void removeEmployee(int idEmployee) {
        Employee employee = findEmployeeById(idEmployee);
        if (employee == null) return;
        //taskurile lui devin neasig in refreshdatata
        map.remove(employee);
    }

    public boolean removeTask(int idTask) {
        for (Employee employee : map.keySet()) {
            for (Task task : map.get(employee)) {
                if (task.getIdTask() == idTask) {
                    map.get(employee).remove(task);
                    return true;
                }
            }
        }
        return false;
    }

    public Employee findEmployeeById(int idEmployee) {
        for (Employee e : map.keySet()) {
            if (e.getIdEmployee() == idEmployee) {
                return e;
            }
        }
        return null;
    }

    public void assignTaskToEmployee(int idEmployee, Task task) {
        Employee employee = findEmployeeById(idEmployee);
        if (employee != null && task != null) {
            map.get(employee).add(task);//luam lista de taskuri si adaugam la ea task
        }
    }

    public int calculateEmployeeWorkDuration(int idEmployee) {
        Employee employee = findEmployeeById(idEmployee);
        if (employee == null) {
            return 0;
        }

        int total = 0;
        for (Task task : map.get(employee)) {
            if ("Completed".equals(task.getStatusTask())) {
                total += task.estimateDuration();
            }
        }
        return total;
    }

    public void modifyTaskStatus(int idEmployee, int idTask) {
        Employee employee = findEmployeeById(idEmployee);
        if (employee == null) {
            return;
        }

        for (Task task : map.get(employee)) {
            if (task.getIdTask() == idTask) {
                if ("Completed".equals(task.getStatusTask())) {
                    task.setStatusTask("Uncompleted");
                } else {
                    task.setStatusTask("Completed");
                }

                if (task.getPartOf() != null) {
                    task.getPartOf().updateStatus();
                }
                return;
            }
        }
    }

    public List<Task> getEmployeeTasks(int idEmployee) {
        Employee employee = findEmployeeById(idEmployee);
        if (employee != null) {
            return map.get(employee);
        }
        return new ArrayList<>();//nu are taskuri
    }
}