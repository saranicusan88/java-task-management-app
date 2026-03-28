package presentation;


import org.tucn.pt.business.TaskManagement;
import org.tucn.pt.business.Utility;
import org.tucn.pt.model.ComplexTask;
import org.tucn.pt.model.Employee;
import org.tucn.pt.model.SimpleTask;
import org.tucn.pt.model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tucn.pt.dataAccess.AppData;
import java.io.*;

public class MainGui extends JFrame {

    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton addEmployeeButton;
    private JButton addSimpleTaskButton;
    private JButton addComplexTaskButton;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JButton assignTaskButton1;
    private JTextField textField8;
    private JTextField textField9;
    private JButton modifyStatusButton1;
    private JTextField textField10;
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private JTextField textField11;
    private JTextField textField12;
    private JTextField textField13;
    private JButton deleteTaskButton;
    private JButton deleteEmployeeButton;

    private DefaultTableModel dataTableModel;
    private DefaultTableModel over40TableModel;
    private DefaultTableModel statsTableModel;

    private TaskManagement management;
    private List<Task> createdTasks;

    //pt salvat datele
    private static final String DATA_FILE = "task_management_data.ser";//fisierul unde se salveaza date

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(new AppData(management, createdTasks));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            AppData data = (AppData) in.readObject();
            management = data.getManagement();
            createdTasks = data.getCreatedTasks();
        } catch (IOException | ClassNotFoundException e) {
            management = new TaskManagement();
            createdTasks = new ArrayList<>();
        }
    }

    public MainGui() {

        loadData();

        setContentPane(panel1);
        UIManager.put("TabbedPane.selected", new Color(85, 60, 150)); // ca sa fie mov Sara tm
        UIManager.put("TabbedPane.background", Color.BLACK);
        UIManager.put("TabbedPane.foreground", Color.WHITE);

        SwingUtilities.updateComponentTreeUI(this);//refr ui
        setTitle("Task Management App");
        setIconImage(new ImageIcon("task.png").getImage());//iconita
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeTables();
        initializeActions();

        refreshDataTable(); //ia datele din .ser
        refreshStatisticsTables();
        pack();//sa se aseze frumos fara spatii
        setLocationRelativeTo(null);//centru
        setVisible(true);
    }

    private void initializeTables() {
        dataTableModel = new DefaultTableModel(
                new Object[]{"Task ID", "Assigned To", "Status", "Type", "Part Of", "Start Hour", "End Hour","Duration"}, 0
        );
        table1.setModel(dataTableModel); //ia tab din .form
        table1.setRowHeight(24);
        table1.getTableHeader().setReorderingAllowed(false); //impiedica utilizatoru sa mute randuri

        over40TableModel = new DefaultTableModel(
                new Object[]{"Name", "Work duration(h)"}, 0
        );
        table2.setModel(over40TableModel);
        table2.setRowHeight(24);
        table2.getTableHeader().setReorderingAllowed(false);

        statsTableModel = new DefaultTableModel(
                new Object[]{"ID","Name", "Completed", "Uncompleted"}, 0
        );
        table3.setModel(statsTableModel);
        table3.setRowHeight(24);
        table3.getTableHeader().setReorderingAllowed(false);

    }

    private void initializeActions() {
        addEmployeeButton.addActionListener(e -> addEmployeeAction());
        addSimpleTaskButton.addActionListener(e -> addSimpleTaskAction());
        addComplexTaskButton.addActionListener(e -> addComplexTaskAction());
        assignTaskButton1.addActionListener(e -> assignTaskAction());
        modifyStatusButton1.addActionListener(e -> modifyStatusAction());
        deleteTaskButton.addActionListener(e -> deleteTaskAction());
        deleteEmployeeButton.addActionListener(e -> deleteEmployeeAction());
    }

    private void addEmployeeAction() {
        try {
            int id = Integer.parseInt(textField1.getText().trim()); //citim din textfield
            String name = textField2.getText().trim();

            if (management.findEmployeeById(id) != null) {
                JOptionPane.showMessageDialog(this, "Employee with this ID already exists.");
                return;
            }

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Employee name cannot be empty.");
                return;
            }

            Employee employee = new Employee(id, name);
            management.addEmployee(employee);

            JOptionPane.showMessageDialog(this, "Employee added successfully.");

            textField1.setText(""); //golim textfield ul
            textField2.setText("");

            refreshStatisticsTables();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Employee ID must be a valid number.");
        }
        saveData();
    }

    private String normalizeStatus(String status) {
        if ("Completed".equalsIgnoreCase(status)) { //sa nu mai fie case sensitive
            return "Completed";
        }
        return "Uncompleted";
    }

    private void addSimpleTaskAction() {
        try {
            int id = Integer.parseInt(textField3.getText().trim());

            if (findCreatedTaskById(id) != null) {
                JOptionPane.showMessageDialog(this, "Task with this ID already exists.");
                return;
            }

            String status = textField4.getText().trim();
            String type = textField5.getText().trim();
            int startHour = Integer.parseInt(textField6.getText().trim());
            int endHour = Integer.parseInt(textField7.getText().trim());

            if (!"Simple".equalsIgnoreCase(type)) {
                JOptionPane.showMessageDialog(this, "For this button, Task Type should be Simple.");
                return;
            }

            if (!isValidStatus(status)) {
                JOptionPane.showMessageDialog(this, "Status must be Completed or Uncompleted.");
                return;
            }

            SimpleTask simpleTask = new SimpleTask(id, normalizeStatus(status), startHour, endHour);
            createdTasks.add(simpleTask);

            String partOfInput = textField11.getText().trim();
            if (!partOfInput.isEmpty()) {
                try {
                    int complexId = Integer.parseInt(partOfInput);
                    if (complexId == id) {
                        JOptionPane.showMessageDialog(this, "A task cannot be part of itself.");
                    } else {
                        for (Task t : createdTasks) {
                            if (t instanceof ComplexTask parentTask && t.getIdTask() == complexId) { //daca partofInputul e taskul t ii adaugam copilul in lista
                                parentTask.addTask(simpleTask); //recursiv in add din complex
                                break;
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Complex Task ID.");
                }
            }

            JOptionPane.showMessageDialog(this, "Simple task added successfully.");
            refreshDataTable();
            clearTaskFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Task ID, Start Hour and End Hour must be valid numbers.");
        }
        saveData();
    }

    private void addComplexTaskAction() {
        try {
            int id = Integer.parseInt(textField3.getText().trim());

            if (findCreatedTaskById(id) != null) {
                JOptionPane.showMessageDialog(this, "Task with this ID already exists.");
                return;
            }

            String status = textField4.getText().trim();
            String type = textField5.getText().trim();

            if (!"Complex".equalsIgnoreCase(type)) {
                JOptionPane.showMessageDialog(this, "For this button, Task Type should be Complex.");
                return;
            }

            if (!isValidStatus(status)) {
                JOptionPane.showMessageDialog(this, "Status must be Completed or Uncompleted.");
                return;
            }

            ComplexTask complexTask = new ComplexTask(id, normalizeStatus(status));
            createdTasks.add(complexTask);

            String partOfInput = textField11.getText().trim();
            if (!partOfInput.isEmpty()) {
                try {
                    int complexId = Integer.parseInt(partOfInput);
                    if (complexId == id) {
                        JOptionPane.showMessageDialog(this, "A task cannot be part of itself.");
                    } else {
                        for (Task t : createdTasks) {
                            if (t instanceof ComplexTask parentTask && t.getIdTask() == complexId) {
                                parentTask.addTask(complexTask);
                                break;
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Complex Task ID.");
                }
            }

            JOptionPane.showMessageDialog(this, "Complex task added successfully.");
            refreshDataTable();
            clearTaskFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Task ID must be a valid number.");
        }
        saveData();
    }

    private void deleteTaskAction() {
        try {
            int taskId = Integer.parseInt(textField12.getText().trim());

            // sterge din createdTasks
            Task toRemove = findCreatedTaskById(taskId);
            if (toRemove == null) {
                JOptionPane.showMessageDialog(this, "Task not found.");
                return;
            }

            createdTasks.remove(toRemove);
            management.removeTask(taskId);

            JOptionPane.showMessageDialog(this, "Task deleted successfully.");
            textField12.setText("");
            refreshDataTable();
            refreshStatisticsTables();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Task ID must be a valid number.");
        }
        saveData();
    }

    private void deleteEmployeeAction() {
        try {
            int employeeId = Integer.parseInt(textField13.getText().trim());

            if (management.findEmployeeById(employeeId) == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.");
                return;
            }

            management.removeEmployee(employeeId);

            JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
            textField13.setText("");
            refreshDataTable();
            refreshStatisticsTables(); //ca sa facem taskurile angajatului sters neasignate

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Employee ID must be a valid number.");
        }
        saveData();
    }

    private void assignTaskAction() {
        try {
            int employeeId = Integer.parseInt(textField8.getText().trim());
            int taskId = Integer.parseInt(textField9.getText().trim());

            Task taskToAssign = findCreatedTaskById(taskId);

            if (taskToAssign == null) {
                JOptionPane.showMessageDialog(this, "Task not found in created tasks.");
                return;
            }

            if (management.findEmployeeById(employeeId) == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.");
                return;
            }

            // verificare dacă taskul e deja asignat
            for (Employee employee : management.getMap().keySet()) {
                if (management.getMap().get(employee).contains(taskToAssign)) {
                    JOptionPane.showMessageDialog(this, "Task is already assigned to " + employee.getName());
                    return;
                }
            }

            management.assignTaskToEmployee(employeeId, taskToAssign);

            JOptionPane.showMessageDialog(this, "Task assigned successfully.");

            textField8.setText("");
            textField9.setText("");

            refreshDataTable();
            refreshStatisticsTables();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Employee ID and Task ID must be valid numbers.");
        }
        saveData();
    }

    private void modifyStatusAction() {
        try {
            int taskId = Integer.parseInt(textField10.getText().trim());

            boolean found = false;

            for (Employee employee : management.getMap().keySet()) {
                for (Task task : management.getMap().get(employee)) {
                    if (task.getIdTask() == taskId) {
                        management.modifyTaskStatus(employee.getIdEmployee(), taskId);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            if (!found) {
                for (Task task : createdTasks) {
                    if (task.getIdTask() == taskId) {
                        if ("Completed".equals(task.getStatusTask())) {
                            task.setStatusTask("Uncompleted");
                        } else {
                            task.setStatusTask("Completed");
                        }
                        if (task.getPartOf() != null) {
                            task.getPartOf().updateStatus();
                        }
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "Task not found.");
                return;
            }

            JOptionPane.showMessageDialog(this, "Task status modified successfully.");
            textField10.setText("");
            refreshDataTable();
            refreshStatisticsTables();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Task ID must be a valid number.");
        }
        saveData();
    }

    private Task findCreatedTaskById(int taskId) {
        for (Task task : createdTasks) {
            if (task.getIdTask() == taskId) {
                return task;
            }
        }
        return null;
    }

    private void addTaskRow(Task task, String employeeName) {
        String type;
        if (task instanceof SimpleTask)
            type="Simple";
        else type= "Complex";

        String partOf;
        if(task.getPartOf() == null)
            partOf="Itself";
        else partOf= String.valueOf(task.getPartOf().getIdTask());

        if (task instanceof SimpleTask simpleTask) {
            dataTableModel.addRow(new Object[]{
                    task.getIdTask(),
                    employeeName,
                    task.getStatusTask(),
                    type,
                    partOf,
                    simpleTask.getStartHour(),
                    simpleTask.getEndHour(),
                    simpleTask.estimateDuration()
            });
        } else {
            dataTableModel.addRow(new Object[]{
                    task.getIdTask(),
                    employeeName,
                    task.getStatusTask(),
                    type,
                    partOf,
                    "-", //daca e complex nu pune ora de inceput si ora de final
                    "-",
                    task.estimateDuration()
            });
        }
    }

    private void refreshDataTable() { //pune date in tab data
        dataTableModel.setRowCount(0);

        //pt task uri asignate in map
        for (Employee employee : management.getMap().keySet()) {
            for (Task task : management.getMap().get(employee)) {
                addTaskRow(task, employee.getName());
            }
        }

        //pt taskuri neasignate din createdTasks
        for (Task task : createdTasks) {
            boolean isAssigned = false;
            for (Employee employee : management.getMap().keySet()) {
                if (management.getMap().get(employee).contains(task)) {
                    isAssigned = true;
                    break;
                }
            }
            if (!isAssigned) {
                addTaskRow(task, "Unassigned");

            }
        }
    }

    private void refreshStatisticsTables() {
        over40TableModel.setRowCount(0); //golim datele ca sa nu le dublam cand adaugam
        statsTableModel.setRowCount(0);

        List<String> over40Employees = Utility.filterEmployeesWithMoreThan40Hours(management);

        for (String name : over40Employees) {
            for (Employee employee : management.getMap().keySet()) {
                if (employee.getName().equals(name)) { //daca emp curent e in lista
                    int hours = management.calculateEmployeeWorkDuration(employee.getIdEmployee());
                    over40TableModel.addRow(new Object[]{employee.getName(), hours});
                    break;
                }
            }
        }
        //pt tabelul cu completed, uncompleted
        Map<String, Map<String, Integer>> statistics = Utility.computeTaskStatistics(management);
        for (Employee employee : management.getMap().keySet()) {
            Map<String, Integer> employeeStats = statistics.get(employee.getName());
            if (employeeStats != null) {
                statsTableModel.addRow(new Object[]{ //le punem in tabelul de jos
                        employee.getIdEmployee(),
                        employee.getName(),
                        employeeStats.get("Completed"),
                        employeeStats.get("Uncompleted")
                });
            }
        }
    }

    private boolean isValidStatus(String status) {
        return "Completed".equalsIgnoreCase(status) || "Uncompleted".equalsIgnoreCase(status);
    }

    private void clearTaskFields() {
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
        textField6.setText("");
        textField7.setText("");
        textField11.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGui::new); //cream o instanta noua intr un singur thread

    }

    private void createUIComponents() {
        table1 = new JTable();
    }
}