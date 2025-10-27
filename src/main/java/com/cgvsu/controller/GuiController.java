package com.cgvsu.controller;

import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.Transform;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;


import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 0.5F;
    final private float ROTATION_STEP = 0.1F; // шаг вращения в радианах
    final private float SCALE_STEP = 0.1F;    // шаг масштабирования

    @FXML private Slider translateXSlider;
    @FXML private Slider translateYSlider;
    @FXML private Slider translateZSlider;
    @FXML private Slider rotateXSlider;
    @FXML private Slider rotateYSlider;
    @FXML private Slider rotateZSlider;
    @FXML private Slider scaleSlider;
    @FXML private HBox transformPanel;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;
    private Transform transform = new Transform(); // объект для преобразований

    private Camera camera = new Camera(
            new Vector3f(0, 20, 50),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                // Используем новый метод render с преобразованиями
                RenderEngine.render(
                        canvas.getGraphicsContext2D(),
                        camera,
                        mesh,
                        (int) width,
                        (int) height,
                        transform
                );
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // Сбрасываем преобразования при загрузке новой модели
            transform = new Transform();
            // todo: обработка ошибок
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // === КАМЕРА ===
    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    @FXML
    public void handleTranslateX() {
        if (translateXSlider != null) {
            transform.translateX = (float) translateXSlider.getValue();
        }
    }

    @FXML
    public void handleTranslateY() {
        if (translateYSlider != null) {
            transform.translateY = (float) translateYSlider.getValue();
        }
    }

    @FXML
    public void handleTranslateZ() {
        if (translateZSlider != null) {
            transform.translateZ = (float) translateZSlider.getValue();
        }
    }

    @FXML
    public void handleRotateX() {
        if (rotateXSlider != null) {
            transform.rotateX = (float) Math.toRadians(rotateXSlider.getValue());
        }
    }

    @FXML
    public void handleRotateY() {
        if (rotateYSlider != null) {
            transform.rotateY = (float) Math.toRadians(rotateYSlider.getValue());
        }
    }

    @FXML
    public void handleRotateZ() {
        if (rotateZSlider != null) {
            transform.rotateZ = (float) Math.toRadians(rotateZSlider.getValue());
        }
    }

    @FXML
    public void handleScale() {
        if (scaleSlider != null) {
            float scale = (float) scaleSlider.getValue();
            transform.scaleX = scale;
            transform.scaleY = scale;
            transform.scaleZ = scale;
        }
    }

    @FXML
    public void handleResetTransform(ActionEvent actionEvent) {
        transform = new Transform();

        // Сбросить слайдеры
        if (translateXSlider != null) translateXSlider.setValue(0);
        if (translateYSlider != null) translateYSlider.setValue(0);
        if (translateZSlider != null) translateZSlider.setValue(0);
        if (rotateXSlider != null) rotateXSlider.setValue(0);
        if (rotateYSlider != null) rotateYSlider.setValue(0);
        if (rotateZSlider != null) rotateZSlider.setValue(0);
        if (scaleSlider != null) scaleSlider.setValue(1);
    }

    @FXML
    public void showTransformPanel() {
        if (transformPanel != null) {
            transformPanel.setVisible(true);
            transformPanel.setManaged(true);
        }
    }

    @FXML
    public void hideTransformPanel() {
        if (transformPanel != null) {
            transformPanel.setVisible(false);
            transformPanel.setManaged(false);
        }
    }

    @FXML
    public void toggleTransformPanel() {
        if (transformPanel != null) {
            boolean visible = transformPanel.isVisible();
            transformPanel.setVisible(!visible);
            transformPanel.setManaged(!visible);
        }
    }
}