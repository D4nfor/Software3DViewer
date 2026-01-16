package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.render_engine.Camera;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class CameraController {

    @FXML private ComboBox<Camera> cameraComboBox;
    @FXML private Button addCameraButton;
    @FXML private Button removeCameraButton;

    private final SceneManager sceneManager;

    public CameraController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        // Привязываем список камер к ComboBox
        ObservableList<Camera> cameras = sceneManager.getCameras();
        cameraComboBox.setItems(cameras);

        // Отображаем активную камеру в ComboBox
        sceneManager.activeCameraProperty().addListener((obs, oldCam, newCam) -> {
            cameraComboBox.getSelectionModel().select(newCam);
        });

        cameraComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldCam, newCam) -> {
            if (newCam != null) {
                sceneManager.setActiveCamera(newCam);
            }
        });

        // Добавление новой камеры
        addCameraButton.setOnAction(e -> {
            String name = "Camera " + (cameras.size() + 1);
            Camera newCam = new Camera(
                    name,
                    sceneManager.getActiveCamera().getPosition(),
                    sceneManager.getActiveCamera().getTarget(),
                    1.0f,
                    1f,
                    0.01f,
                    100f
            );
            sceneManager.addCamera(newCam);
            sceneManager.setActiveCamera(newCam);
        });

        // Удаление выбранной камеры
        removeCameraButton.setOnAction(e -> {
            Camera selected = cameraComboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (cameras.size() == 1) {
                    showAlert("Невозможно удалить", "Должна быть хотя бы одна камера.");
                    return;
                }
                sceneManager.removeCamera(selected);
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
