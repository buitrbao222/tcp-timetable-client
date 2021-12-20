module com.tcp.tcptimetableclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires lombok;

    exports Controllers;
    opens Controllers to javafx.fxml;
    exports Main;
    opens Main to javafx.fxml;
    exports DTO;
    opens DTO to javafx.fxml;
}