package com.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;

public class ExmController {
    @FXML private ToggleGroup genderGroup;



    @FXML private TextField textField;
    @FXML private TextArea textArea;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox checkBox;
    @FXML private ComboBox<String> comboBox;
    @FXML private ListView<String> listView;
    @FXML private TableView<?> tableView;
    @FXML private DatePicker datePicker;
    @FXML private Slider slider;
    @FXML private WebView webView;

    @FXML
    private void handleButton() {
        String name = textField.getText();
        String message = "Привет, " + name + "!";
        System.out.println(message);

        // Пример использования других компонентов
        System.out.println("Согласен: " + checkBox.isSelected());
        System.out.println("Выбранное значение из ComboBox: " + comboBox.getValue());
        System.out.println("Слайдер на: " + slider.getValue());

        // Установка текста в TextArea
        textArea.setText(message + "\n(Это вы ввели)");

        // Пример работы с WebView
        webView.getEngine().load("https://example.com");
    }
}
