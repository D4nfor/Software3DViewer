package com.cgvsu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Software3DViewer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/Main.fxml"));
        BorderPane viewport = loader.load();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(viewport);

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        viewport.prefWidthProperty().bind(scene.widthProperty());
        viewport.prefHeightProperty().bind(scene.heightProperty());

        stage.setTitle("Software3DViewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
