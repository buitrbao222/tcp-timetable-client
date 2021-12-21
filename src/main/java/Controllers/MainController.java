package Controllers;

import DTO.Timetable;
import Socket.Connection;
import Utils.AlertUtils;
import Utils.JsonToTimetable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
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
            minDaysCheckBox,
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

        addTextField.setText("");
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

    public void onCreateClick() throws IOException {
        JSONObject options = new JSONObject();

        if (subjects.size() == 0) {
            AlertUtils.alert("Hãy thêm ít nhất 1 mã môn học");
            return;
        }

        String subjects = String.join(",", subjectsListView.getItems());
        options.put("subjects", subjects);

        if (subjectLimitCheckBox.isSelected()) {
            String subjectLimitString = subjectLimitTextField.getText();
            if (subjectLimitString.isEmpty()) {
                AlertUtils.alert("Hãy nhập giá trị cho 'Giới hạn số lượng môn học' hoặc bỏ check tùy chọn này.");
                return;
            }

            int subjectLimit = Integer.parseInt(subjectLimitString);
            int subjectLength = subjectsListView.getItems().size();

            if (subjectLimit < 1 || subjectLimit > subjectLength) {
                AlertUtils.alert("'Giới hạn số lượng môn học' phải có giá trị trong khoảng 1 đến " + subjectLength);
                return;
            }

            options.put("numSubject", subjectLimitString);
        }

        if (resultLimitCheckBox.isSelected()) {
            String resultLimitString = resultLimitTextField.getText();
            if (resultLimitString.isEmpty()) {
                AlertUtils.alert("Hãy nhập giá trị cho 'Giới hạn số lượng kết quả trả về' hoặc bỏ check tùy chọn này.");
                return;
            }

            if (Integer.parseInt(resultLimitString) == 0) {
                AlertUtils.alert("'Giới hạn số lượng kết quả trả về' phải có giá trị tối thiểu là 1.");
                return;
            }

            options.put("limit", resultLimitString);
        }

        if (minDaysCheckBox.isSelected()) {
            options.put("minNumDaysOn", true);
        }

        if (dayCountCheckBox.isSelected()) {
            String dayCountString = dayCountTextField.getText();

            if (dayCountString.isEmpty()) {
                AlertUtils.alert("Hãy nhập giá trị cho 'Bắt buộc số ngày học trong tuần' hoặc bỏ check tùy chọn này.");
                return;
            }

            int dayCount = Integer.parseInt(dayCountString);
            if (dayCount < 1 || dayCount > 6) {
                AlertUtils.alert("'Bắt buộc số ngày học trong tuần' phải có giá trị trong khoảng 1 đến 6.");
                return;
            }

            options.put("numDaysOn", dayCountString);
        }

        if (daySessionCheckBox.isSelected()) {
            RadioButton selectedDaySession = (RadioButton) daySessionToggleGroup.getSelectedToggle();

            if (selectedDaySession == null) {
                AlertUtils.alert("Hãy chọn giá trị cho 'Bắt buộc buổi học trong tuần' hoặc bỏ check tùy chọn này.");
                return;
            }

            switch (selectedDaySession.getText()) {
                case MORNING_ONLY -> options.put("morning", true);
                case AFTERNOON_ONLY -> options.put("afternoon", true);
                case MORNING_AND_AFTERNOON -> {
                    options.put("morning", true);
                    options.put("afternoon", true);
                }
            }
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

            if (studyDays.isEmpty()) {
                AlertUtils.alert("Hãy chọn giá trị cho 'Bắt buộc ngày học trong tuần' hoặc bỏ check tùy chọn này.");
                return;
            }

            String daysOn = String.join(",", studyDays);
            options.put("daysOn", daysOn);
        }

        String hostLocal = "localhost";
        String host = "103.6.169.208";
        Connection connection = new Connection(hostLocal, 6000);

        String response = connection.getTimetables(options);

        connection.close();

        // Unexpected error
        if (response.equals("error")) {
            AlertUtils.alert("Đã có lỗi bất ngờ xảy ra.");
            return;
        }

        JSONObject jsonData = new JSONObject(response);

        // Has error from server
        if (jsonData.keySet().contains("error")) {
            String error = jsonData.getString("error");

            String message = "";
            if (error.startsWith("Invalid subject")) {
                String subject = error.replaceAll("Invalid subject: ", "");
                message = "Mã môn học không tồn tại: " + subject;
            } else {
                message = error;
            }

            AlertUtils.alert(message);
            return;
        }

        ArrayList<Timetable> timetables = JsonToTimetable.convert(response);

        if (!timetables.isEmpty()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/timetable.fxml"));
            TimetableController timeTableController = new TimetableController();
            timeTableController.timetables = timetables;
            fxmlLoader.setController(timeTableController);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Thời khóa biểu SGU");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } else {
            AlertUtils.alert("Không có thời khóa biểu phù hợp theo yêu cầu");
        }
    }

    public void initAddTextField() {
        // Add on enter
        addTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                onAddClick();
            }
        });

        // Max length constraint
        addTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > SUBJECT_REQUIRED_LENGTH) {
                addTextField.setText(newValue.substring(0, SUBJECT_REQUIRED_LENGTH));
            }
        });
    }

    // Integer only text fields
    public void initIntegerTextFields() {
        subjectLimitTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                subjectLimitTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        resultLimitTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                resultLimitTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        dayCountTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                dayCountTextField.setText(newValue.replaceAll("[^\\d]", ""));
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
        initIntegerTextFields();
        initSubjectListView();
        initOptionalCheckBoxes();
    }
}