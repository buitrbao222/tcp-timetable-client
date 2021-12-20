package Controllers;

import DTO.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TimetableController implements Initializable {
    public ArrayList<Timetable> timetables;

    @FXML
    public TableView<TimetableColumn> resultsTable;

    @FXML
    public TableColumn<TimetableColumn, String> resultsColumn;

    @FXML
    public GridPane gridPane;

    @FXML
    public ComboBox<Integer> weekComboBox;

    ObservableList<Subject> subjects = FXCollections.observableArrayList();
    ObservableList<TimetableColumn> results = FXCollections.observableArrayList();
    ObservableList<Integer> weekNumber = FXCollections.observableArrayList();

    public void initGridPaneView(int numWeek) {
        gridPane.getChildren().removeIf(node -> node instanceof TextFlow);
        for (Subject subject : subjects) {
            SubjectClass[] subjectClasses = subject.classes;
            for (SubjectClass subjectClass : subjectClasses) {
                ClassSession[] classSessions = subjectClass.sessions;
                for (ClassSession classSession : classSessions) {
                    if (classSession.weeks[numWeek]) {
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
    }

    public void initComboBox() {
        Callback<ListView<Integer>, ListCell<Integer>> factory = (lv) ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : "Tuần " + (getIndex() + 1));
                    }
                };

        weekComboBox.setCellFactory(factory);

        weekComboBox.setButtonCell(factory.call(null));

        weekComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (weekComboBox.getSelectionModel().getSelectedIndex() != -1) {
                int numWeek = weekComboBox.getSelectionModel().getSelectedIndex();
                initGridPaneView(numWeek);
            }
        });

        // '15' is num week
        Integer[] num = new Integer[15];
        for (int i = 0; i < 15; i++) {
            num[i] = i + 1;
        }

        weekNumber.addAll(num);

        weekComboBox.setItems(weekNumber);

        weekComboBox.getSelectionModel().selectFirst();
    }

    public void initTableView() {
        resultsColumn.setCellValueFactory(data -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue("Thời khóa biểu " + data.getValue().index);

            return property;
        });

        int size = timetables.size();
        for (int i = 1; i <= size; i++) {
            TimetableColumn timetablesColumn = new TimetableColumn();
            timetablesColumn.index = i;
            timetablesColumn.timeTable = timetables.get(i - 1);
            results.add(timetablesColumn);
        }

        resultsTable.setItems(results);

        resultsTable.getSelectionModel().selectedItemProperty().addListener((observableValue, timeTable, t1) -> {
            subjects.clear();
            weekComboBox.getItems().clear();
            subjects.addAll(results.get(resultsTable.getSelectionModel().getSelectedIndex()).timeTable.subjects);
            initComboBox();
        });
        resultsTable.getSelectionModel().selectFirst();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
    }
}