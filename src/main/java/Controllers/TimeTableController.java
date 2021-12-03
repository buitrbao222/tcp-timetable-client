package Controllers;

import DTO.ClassSession;
import DTO.Subject;
import DTO.SubjectClass;
import DTO.TimeTable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TimeTableController implements Initializable {
    public ArrayList<TimeTable> timeTableList;
    @FXML
    public TableView<TimeTable> table;
    @FXML
    public TableColumn<TimeTable, String> timeTableColumn;
    @FXML
    public GridPane gridPane;
    ObservableList<Subject> subjects = FXCollections.observableArrayList();
    ObservableList<TimeTable> timeTables = FXCollections.observableArrayList();

    public void initGridPaneView() {
        gridPane.getChildren().removeIf(node -> node instanceof TextFlow);
        for (Subject subject : subjects) {
            SubjectClass[] subjectClasses = subject.classes;
            for (SubjectClass subjectClass : subjectClasses) {
                ClassSession[] classSessions = subjectClass.sessions;
                for (ClassSession classSession : classSessions) {
                    int x = classSession.day - 1;
                    int y = classSession.start;
                    int length = classSession.length;
                    Text idSubject = new Text("Mã: " + subject.subjectId + "\n");
                    Text roomSubject = new Text("Phòng: " + classSession.room + "\n");
                    Text groupSubject = new Text("Nhóm: " + subjectClass.group + "\n");
                    Text nameSubject = new Text("Tên: " + subject.subjectName);
                    TextFlow timeTableTextFlow = new TextFlow(idSubject, roomSubject, groupSubject, nameSubject);
                    timeTableTextFlow.setStyle("-fx-background-color: lightblue;-fx-border-color: black;");
                    gridPane.add(timeTableTextFlow, x, y, 1, length);
                }
            }
        }
    }

    public void initTableView() {
        table.setRowFactory(tv -> {
            TableRow<TimeTable> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    subjects.clear();
                    subjects.addAll(timeTables.get(row.getIndex()).subjects);
                    initGridPaneView();
                }
            });

            timeTableColumn.setCellValueFactory(data -> {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue("Thời khóa biểu " + (row.getIndex() + 1));
                return property;
            });

            return row;
        });

        timeTables.addAll(timeTableList);
        table.setItems(timeTables);

        // Set timetable default
        if (!timeTableList.isEmpty()) {
            table.getSelectionModel().selectFirst();
            subjects.clear();
            subjects.addAll(timeTables.get(table.getSelectionModel().getSelectedIndex()).subjects);
            initGridPaneView();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
    }
}