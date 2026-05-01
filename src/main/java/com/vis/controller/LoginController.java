package com.vis.controller;

import com.vis.util.SceneManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;
    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#1a73e8"));
        shadow.setRadius(15);
        loginButton.setEffect(shadow);

        FadeTransition fade = new FadeTransition(Duration.millis(1000), loginButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.5);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        loadingIndicator.setVisible(false);
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        errorLabel.setVisible(false);

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            errorLabel.setVisible(true);
            return;
        }

        loadingIndicator.setVisible(true);

        try {
            if (username.equals("admin") && password.equals("admin")) {
                SceneManager.switchTo("Dashboard.fxml");
            } else {
                loadingIndicator.setVisible(false);
                errorLabel.setText("Invalid credentials. Try admin / admin.");
                errorLabel.setVisible(true);
            }
        } catch (Exception e) {
            loadingIndicator.setVisible(false);
            errorLabel.setText("Error: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
}
