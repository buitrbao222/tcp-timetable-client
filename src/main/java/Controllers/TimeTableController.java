package Controllers;

import DTO.*;
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
    public TableView<TimeTableColumn> table;
    @FXML
    public TableColumn<TimeTableColumn, String> timeTableColumn;
    @FXML
    public GridPane gridPane;

    ObservableList<Subject> subjects = FXCollections.observableArrayList();
    ObservableList<TimeTableColumn> timeTables = FXCollections.observableArrayList();

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
            TableRow<TimeTableColumn> row = new TableRow<>();

            timeTableColumn.setCellValueFactory(data -> {
                SimpleStringProperty property = new SimpleStringProperty();
                property.setValue("Thời khóa biểu " + data.getValue().index);
                return property;
            });

            return row;
        });

        int size = timeTableList.size();
        for (int i = 1; i <= size; i++) {
            TimeTableColumn timeTableColumn = new TimeTableColumn();
            timeTableColumn.index = i;
            timeTableColumn.timeTable = timeTableList.get(i - 1);
            timeTables.add(timeTableColumn);
        }

        table.setItems(timeTables);

        table.getSelectionModel().selectedItemProperty().addListener((observableValue, timeTable, t1) -> {
            subjects.clear();
            subjects.addAll(timeTables.get(table.getSelectionModel().getSelectedIndex()).timeTable.subjects);
            initGridPaneView();
        });

        table.getSelectionModel().selectFirst();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
    }
}