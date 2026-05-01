package com.vis.util;

import com.vis.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class SceneManager {

    public static void switchTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            SceneManager.class.getResource("/com/vis/fxml/" + fxmlFile)
        );
        Scene currentScene = MainApp.primaryStage.getScene();
        if (currentScene == null) {
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(SceneManager.class.getResource("/com/vis/css/style.css").toExternalForm());
            MainApp.primaryStage.setScene(scene);
        } else {
            currentScene.setRoot(loader.load());
            String cssUrl = SceneManager.class.getResource("/com/vis/css/style.css").toExternalForm();
            if (!currentScene.getStylesheets().contains(cssUrl)) {
                currentScene.getStylesheets().add(cssUrl);
            }
        }
        MainApp.primaryStage.setMaximized(true);
    }

    public static <T> T switchToWithController(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            SceneManager.class.getResource("/com/vis/fxml/" + fxmlFile)
        );
        Scene currentScene = MainApp.primaryStage.getScene();
        if (currentScene == null) {
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(SceneManager.class.getResource("/com/vis/css/style.css").toExternalForm());
            MainApp.primaryStage.setScene(scene);
        } else {
            currentScene.setRoot(loader.load());
            String cssUrl = SceneManager.class.getResource("/com/vis/css/style.css").toExternalForm();
            if (!currentScene.getStylesheets().contains(cssUrl)) {
                currentScene.getStylesheets().add(cssUrl);
            }
        }
        MainApp.primaryStage.setMaximized(true);
        return loader.getController();
    }
}
