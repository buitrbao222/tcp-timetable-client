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
import java.util.HashMap;
import java.util.ResourceBundle;

public class TimetableController implements Initializable {
    public ArrayList<Timetable> timetables;
    public HashMap<String, Color> subjectColorMap;

    @FXML
    public TableView<ResultRow> resultsTable;

    @FXML
    public TableColumn<ResultRow, String> resultsColumn;

    @FXML
    public GridPane gridPane;

    @FXML
    public ComboBox<Integer> weekComboBox;

    ObservableList<Subject> subjects = FXCollections.observableArrayList();
    ObservableList<ResultRow> results = FXCollections.observableArrayList();
    ObservableList<Integer> weekNumber = FXCollections.observableArrayList();

    public void initGridPaneView(int numWeek) {
        gridPane.getChildren().removeIf(node -> node instanceof TextFlow);
        for (Subject subject : subjects) {
            Color color = subjectColorMap.get(subject.subjectId);
            String textColor = color.text;
            String backgroundColor = color.background;

            SubjectClass[] classes = subject.classes;

            for (SubjectClass clazz : classes) {
                ClassSession[] sessions = clazz.sessions;
                for (ClassSession session : sessions) {
                    if (session.weeks[numWeek]) {
                        int x = session.day - 1;
                        int y = session.start;
                        int length = session.length;

                        Text id = new Text(subject.subjectId + "\n");
                        Text name = new Text(subject.subjectName + "\n");
                        Text group = new Text("Nhóm " + clazz.group + "\n");
                        Text room = new Text("Phòng " + session.room + "\n");

                        id.setStyle("-fx-fill: " + textColor + ";");
                        name.setStyle("-fx-fill: " + textColor + ";");
                        group.setStyle("-fx-fill: " + textColor + ";");
                        room.setStyle("-fx-fill: " + textColor + ";");

                        TextFlow textFlow = new TextFlow(id, name, group, room);
                        textFlow.setStyle("-fx-background-color: " + backgroundColor + ";" +
                                                  "-fx-border-color: black;" +
                                                  "-fx-padding: 8 8 8 8;");
                        gridPane.add(textFlow, x, y, 1, length);
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
            int selectedIndex = weekComboBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                initGridPaneView(selectedIndex);
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
        // Results column render
        resultsColumn.setCellValueFactory(data -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue("Thời khóa biểu " + data.getValue().index);

            return property;
        });

        // Populate results table
        int size = timetables.size();
        for (int i = 1; i <= size; i++) {
            ResultRow resultRow = new ResultRow();
            resultRow.index = i;
            resultRow.timeTable = timetables.get(i - 1);
            results.add(resultRow);
        }

        resultsTable.setItems(results);

        resultsTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            subjects.clear();
            subjects.addAll(newValue.timeTable.subjects);
            weekComboBox.getItems().clear();
            initComboBox();
        });

        resultsTable.getSelectionModel().selectFirst();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
    }
}