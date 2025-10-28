package com.cgvsu.controller;

import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.Transform;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class GuiController {

    @FXML
    private BorderPane borderPane; // главный BorderPane

    @FXML
    private Canvas canvas;

    @FXML
    private MenuController menuIncludeController;

    @FXML
    private TransformController transformPanelIncludeController;

    private Timeline timeline;

    @FXML
    private void initialize() {
        // Настраиваем контроллер меню
        if (menuIncludeController != null) {
            menuIncludeController.setMainController(this);
        }

        // Настраиваем контроллер трансформаций
        if (transformPanelIncludeController != null) {
            transformPanelIncludeController.setOnTransformChange(this::renderFrame);
            transformPanelIncludeController.hidePanel();
        }

        // setupCanvasResizeListener();
        setupRenderLoop();
    }

    private void setupCanvasResizeListener() {
        canvas.widthProperty().bind(borderPane.widthProperty());
        canvas.heightProperty().bind(borderPane.heightProperty());
    }

    private void setupRenderLoop() {
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            renderFrame();
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    private void renderFrame() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

        if (menuIncludeController != null) {
            menuIncludeController.getCamera().setAspectRatio((float) (width / height));

            if (menuIncludeController.getMesh() != null) {
                RenderEngine.render(
                        canvas.getGraphicsContext2D(),
                        menuIncludeController.getCamera(),
                        menuIncludeController.getMesh(),
                        (int) width,
                        (int) height,
                        transformPanelIncludeController.getTransform()
                );
            }
        }
    }

    // Публичные методы для MenuController
    public void requestRender() {
        renderFrame();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    // Методы для управления панелью трансформаций
    public void showTransformPanel() {
        if (transformPanelIncludeController != null) {
            transformPanelIncludeController.showPanel();
        }
    }

    public void hideTransformPanel() {
        if (transformPanelIncludeController != null) {
            transformPanelIncludeController.hidePanel();
        }
    }

    public void setTransform(Transform transform) {
        transformPanelIncludeController.setTransform(transform);
    }
}