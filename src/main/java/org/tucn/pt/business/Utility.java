package org.tucn.pt.business;

import org.tucn.pt.model.Employee;
import org.tucn.pt.model.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {

    public static List<String> filterEmployeesWithMoreThan40Hours(TaskManagement management) { //nu instantez,apelez direct
        List<Employee> filteredEmployees = new ArrayList<>();
        for (Employee employee : management.getMap().keySet()) {
            int duration = management.calculateEmployeeWorkDuration(employee.getIdEmployee());
            if (duration > 40) {
                filteredEmployees.add(employee);
            }
        }
        filteredEmployees.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {

                int d1 = management.calculateEmployeeWorkDuration(e1.getIdEmployee());
                int d2 = management.calculateEmployeeWorkDuration(e2.getIdEmployee());

                if (d1 < d2) {
                    return -1;
                } else if (d1 > d2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        List<String> result = new ArrayList<>();

        for (Employee employee : filteredEmployees) {
            result.add(employee.getName());
        }
       return result;
    }

    public static Map<String, Map<String, Integer>> computeTaskStatistics(TaskManagement management) {

        Map<String, Map<String, Integer>> statistics = new HashMap<>();

        for (Employee employee : management.getMap().keySet()) {

            List<Task> tasks = management.getMap().get(employee);

            int completed = 0;
            int uncompleted = 0;

            for (Task task : tasks) {
                if ("Completed".equals(task.getStatusTask())) {
                    completed++;
                } else if ("Uncompleted".equals(task.getStatusTask())) {
                    uncompleted++;
                }
            }

            Map<String, Integer> employeeStats = new HashMap<>(); //lista completed-nr,uncompleted-nr2
            employeeStats.put("Completed", completed);
            employeeStats.put("Uncompleted", uncompleted);

            statistics.put(employee.getName(), employeeStats);//nume-completed-nr,uncompleted-nr
        }

        return statistics;
    }
}