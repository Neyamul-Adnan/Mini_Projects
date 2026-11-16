package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Student;
import java.io.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class MainController {

    @FXML private TextField nameField, idField, deptField, emailField, cgpaField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colName, colId, colDept, colEmail;
    @FXML private TableColumn<Student, Double> colCgpa;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final String filePath = "src/data/student.csv";

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCgpa.setCellValueFactory(new PropertyValueFactory<>("cgpa"));

        loadDataFromCSV();
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                idField.setText(newSelection.getId());
                deptField.setText(newSelection.getDepartment());
                emailField.setText(newSelection.getEmail());
                cgpaField.setText(String.valueOf(newSelection.getCgpa()));
            }
        });
    }

    @FXML
    private void handleShowAllStudents() {

        loadDataFromCSV();
    }

    private void loadDataFromCSV() {
        studentList.clear();
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                if (data.length >= 5) {
                    String name = data[0].trim();
                    String id = data[1].trim();
                    String dept = data[2].trim();
                    String email = data[3].trim();
                    try {
                        double cgpa = Double.parseDouble(data[4].trim());
                        studentList.add(new Student(name, id, dept, email, cgpa));
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid CGPA");
                    }
                }
            }
            studentTable.setItems(studentList);
        } catch (IOException e) {
            System.out.println("File not found");

        }
    }

    @FXML
    private void handleAddStudent() {
        String name = nameField.getText().trim();
        String id = idField.getText().trim();
        String dept = deptField.getText().trim();
        String email = emailField.getText().trim();
        String cgpaText = cgpaField.getText().trim();

        if (name.isEmpty() || id.isEmpty() || dept.isEmpty() || email.isEmpty() || cgpaText.isEmpty()) {
            showAlert("ERROR", "please fill all the fields");
            return;
        }

        for (Student s : studentList) {
            if (s.getId().equalsIgnoreCase(id)) {
                showAlert("Duplicate ID Error", "this id is already in use");
                return;
            }
            if (s.getEmail().equalsIgnoreCase(email)) {
                showAlert("Duplicate Email Error", "this email is already in use");
                return;
            }
        }

        try {
            double cgpa = Double.parseDouble(cgpaText);
            Student newStudent = new Student(name, id, dept, email, cgpa);
            studentList.add(newStudent);
            saveToCSV();
            showAlert("Successful", "Student added successfully");
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "please fill all the fields");
        } catch (Exception e) {
            showAlert("Error", "something went wrong" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setName(nameField.getText());
                selected.setId(idField.getText());
                selected.setDepartment(deptField.getText());
                selected.setEmail(emailField.getText());
                selected.setCgpa(Double.parseDouble(cgpaField.getText()));
                studentTable.refresh();
                saveToCSV();
                showAlert("Successful", "Information Updated successfully");
            } catch (NumberFormatException e) {
                showAlert("Error", "please fill all the fields");
            } catch (Exception e) {
                showAlert("Error", "something went wrong" +e);
            }
        } else {
            showAlert("Error", "Please select a student first");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            studentList.remove(selected);
            saveToCSV();
            showAlert("Success", "Successfully deleted");
            clearFields();
        } else {
            showAlert("Error", "Please select a student first");
        }
    }

    @FXML
    private void handleSearch() {
        String sName = nameField.getText().trim().toLowerCase();
        String sId = idField.getText().trim().toLowerCase();
        String sDept = deptField.getText().trim().toLowerCase();
        String sEmail = emailField.getText().trim().toLowerCase();
        String sCgpa = cgpaField.getText().trim().toLowerCase();

        if (sName.isEmpty() && sId.isEmpty() && sDept.isEmpty() && sEmail.isEmpty() && sCgpa.isEmpty()) {
            studentTable.setItems(studentList);
            showAlert("Error", "Please fill any field to search");
            return;
        }

        ObservableList<Student> filteredList = FXCollections.observableArrayList();
        for (Student s : studentList) {
            boolean matchesName = !sName.isEmpty() && s.getName().toLowerCase().contains(sName);
            boolean matchesId = !sId.isEmpty() && s.getId().toLowerCase().contains(sId);
            boolean matchesDept = !sDept.isEmpty() && s.getDepartment().toLowerCase().contains(sDept);
            boolean matchesEmail = !sEmail.isEmpty() && s.getEmail().toLowerCase().contains(sEmail);
            boolean matchesCgpa = !sCgpa.isEmpty() && String.valueOf(s.getCgpa()).contains(sCgpa);

            if (matchesName || matchesId || matchesDept || matchesEmail || matchesCgpa) {
                filteredList.add(s);
            }
        }
        showAlert("Error", "No Student Found");
        studentTable.setItems(studentList);
    }

    private void saveToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Student s : studentList) {
                bw.write(s.getName() + "," + s.getId() + "," + s.getDepartment() + "," + s.getEmail() + "," + s.getCgpa());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear(); idField.clear(); deptField.clear(); emailField.clear(); cgpaField.clear();
    }
}