module valle.grande.edu.pe.rpsgames {
    requires javafx.controls;
    requires javafx.fxml;

    opens valle.grande.edu.pe.rpsgames to javafx.fxml;
    exports valle.grande.edu.pe.rpsgames;
}