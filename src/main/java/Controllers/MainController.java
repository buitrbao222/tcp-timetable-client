package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import Utils.AlertUtils;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    private final static String MORNING_ONLY = "Chỉ buổi sáng";
    private final static String AFTERNOON_ONLY = "Chỉ buổi chiều";
    private final static String MORNING_AND_AFTERNOON = "Phải có sáng lẫn chiều";
    private final static int SUBJECT_REQUIRED_LENGTH = 6;

    public ObservableList<String> subjects = FXCollections.observableArrayList();

    @FXML
    public TextField addTextField, subjectLimitTextField, resultLimitTextField, dayCountTextField;

    @FXML
    public ListView<String> subjectsListView;

    @FXML
    public ToggleGroup daySessionToggleGroup;

    @FXML
    public CheckBox subjectLimitCheckBox,
            resultLimitCheckBox,
            dayCountCheckBox,
            daySessionCheckBox,
            weekDaysCheckBox, weekDay2, weekDay3, weekDay4, weekDay5, weekDay6, weekDay7;

    @FXML
    public RadioButton morningOnlyRadioButton, afternoonOnlyRadioButton, morningAndAfternoonRadioButton;

    public List<CheckBox> weekDayCheckBoxes;

    @FXML
    public void onAddClick() {
        String subject = addTextField.getText();

        if (subject.length() < SUBJECT_REQUIRED_LENGTH) {
            AlertUtils.alert("Mã môn học phải có 6 kí tự");
            return;
        }

        if (!subject.matches("[a-zA-Z0-9]*")) {
            AlertUtils.alert("Mã môn học không được có kí tự đặc biệt");
            return;
        }

        if (subjects.contains(subject)) {
            AlertUtils.alert("Mã môn học này đã có trong danh sách");
            return;
        }

        subjects.add(subject);
    }

    @FXML
    public void onUpClick() {
        int index = subjectsListView.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            AlertUtils.alert("Hãy chọn mã môn học để thay đổi thứ tự");
            return;
        }

        if (index == 0) {
            return;
        }

        Collections.swap(subjects, index, index - 1);
        subjectsListView.getSelectionModel().select(index - 1);
    }

    @FXML
    public void onDownClick() {
        int index = subjectsListView.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            AlertUtils.alert("Hãy chọn mã môn học để thay đổi thứ tự");
            return;
        }

        if (index == subjects.size() - 1) {
            return;
        }

        Collections.swap(subjects, index, index + 1);
        subjectsListView.getSelectionModel().select(index + 1);
    }

    @FXML
    public void onDeleteClick() {
        int index = subjectsListView.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            AlertUtils.alert("Hãy chọn mã môn học để xóa");
            return;
        }

        subjects.remove(index);
    }

    public void onCreateClick() {
        if (subjects.size() == 0) {
            AlertUtils.alert("Hãy thêm ít nhất 1 mã môn học");
            return;
        }

        String params = "";

        String subjects = String.join(",", subjectsListView.getItems());
        params += "?subjects=" + subjects;

        if (subjectLimitCheckBox.isSelected()) {
            String subjectLimit = subjectLimitTextField.getText();
            params += "&numSubjects=" + subjectLimit;
        }

        if (resultLimitCheckBox.isSelected()) {
            String resultLimit = resultLimitTextField.getText();
            params += "&limit=" + resultLimit;
        }

        if (dayCountCheckBox.isSelected()) {
            String dayCount = dayCountTextField.getText();
            params += "&numDaysOn=" + dayCount;
        }

        if (daySessionCheckBox.isSelected()) {
            RadioButton selectedDaySession = (RadioButton) daySessionToggleGroup.getSelectedToggle();
            String value = "";

            switch (selectedDaySession.getText()) {
                case MORNING_ONLY:
                    value = "&morning=true";
                    break;
                case AFTERNOON_ONLY:
                    value = "&afternoon=true";
                    break;
                case MORNING_AND_AFTERNOON:
                    value = "&morning=true&afternoon=true";
                    break;
            }

            params += value;
        }

        if (weekDaysCheckBox.isSelected()) {
            List<String> studyDays = new ArrayList<>();
            for (var checkBox : weekDayCheckBoxes) {
                if (!checkBox.isSelected()) {
                    continue;
                }

                String day = checkBox.getText().replace("Thứ ", "");
                studyDays.add(day);
            }
            params += "&daysOn=" + String.join(",", studyDays);
        }

        System.out.println("--- Parameters:");
        System.out.println(params);
        System.out.println();
    }

    public void initAddTextField() {
        // Add on enter
        addTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                onAddClick();
            }
        });

        // Max 6 characters
        addTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > SUBJECT_REQUIRED_LENGTH) {
                addTextField.setText(newValue.substring(0, SUBJECT_REQUIRED_LENGTH));
            }
        });
    }

    public void initSubjectListView() {
        // Bind subjects list
        subjectsListView.setItems(subjects);

        // Delete selected row on DELETE key press
        subjectsListView.setOnKeyPressed(keyEvent -> {
            int index = subjectsListView.getSelectionModel().getSelectedIndex();

            if (index == -1) {
                return;
            }

            if (!keyEvent.getCode().equals(KeyCode.DELETE)) {
                return;
            }

            onDeleteClick();
        });
    }

    public void initOptionalCheckBoxes() {
        subjectLimitCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            subjectLimitTextField.setDisable(oldValue);
        });

        resultLimitCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            resultLimitTextField.setDisable(oldValue);
        });

        dayCountCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            dayCountTextField.setDisable(oldValue);
        });


        // Set toggle labels
        morningOnlyRadioButton.setText(MORNING_ONLY);
        afternoonOnlyRadioButton.setText(AFTERNOON_ONLY);
        morningAndAfternoonRadioButton.setText(MORNING_AND_AFTERNOON);

        // Disable all session day radio buttons on checkbox toggle
        daySessionCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            daySessionToggleGroup.getToggles().forEach(toggle -> {
                Node node = (Node) toggle;
                node.setDisable(oldValue);
            });
        });

        weekDayCheckBoxes = Arrays.asList(weekDay2, weekDay3, weekDay4, weekDay5, weekDay6, weekDay7);
        weekDaysCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            weekDayCheckBoxes.forEach(checkBox -> {
                checkBox.setDisable(oldValue);
            });
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAddTextField();
        initSubjectListView();
        initOptionalCheckBoxes();
    }
}