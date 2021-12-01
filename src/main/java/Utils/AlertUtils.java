package Utils;

import javafx.scene.control.Alert;

public class AlertUtils {
    public static void alert(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
