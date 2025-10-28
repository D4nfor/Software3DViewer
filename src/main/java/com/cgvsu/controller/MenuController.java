package com.cgvsu.controller;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.Transform;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MenuController {
    final private float TRANSLATION = 0.5F;

    private GuiController mainController;
    private Model mesh = null;
    private Camera camera = new Camera(
            new Vector3f(0, 0, 50),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100
    );

    public void setMainController(GuiController mainController) {
        this.mainController = mainController;
    }

    public Camera getCamera() {
        return camera;
    }

    public Model getMesh() {
        return mesh;
    }

    @FXML
    private void onOpenModelMenuItemClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) mainController.getCanvas().getScene().getWindow());
        if (file == null) return;

        loadModel(file);
    }

    private void loadModel(File file) {
        Path fileName = Path.of(file.getAbsolutePath());
        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            mainController.setTransform(new Transform());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // === КАМЕРА ===
    @FXML
    private void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
        notifyRender();
    }

    @FXML
    private void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
        notifyRender();
    }

    @FXML
    private void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
        notifyRender();
    }

    @FXML
    private void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
        notifyRender();
    }

    @FXML
    private void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
        notifyRender();
    }

    @FXML
    private void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
        notifyRender();
    }

    @FXML
    private void showTransformPanel(ActionEvent event) {
        if (mainController != null) {
            mainController.showTransformPanel();
        }
    }

    @FXML
    private void hideTransformPanel(ActionEvent event) {
        if (mainController != null) {
            mainController.hideTransformPanel();
        }
    }

    private void notifyRender() {
        if (mainController != null) {
            mainController.requestRender();
        }
    }
}