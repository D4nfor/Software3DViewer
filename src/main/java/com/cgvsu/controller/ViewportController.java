package com.cgvsu.controller;

import com.cgvsu.manager.AnimationManager;
import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.interfaces.InputSystemImpl;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

public class ViewportController {
    @FXML private StackPane canvasContainer;
    @FXML private Canvas canvas;

    private final SceneManager sceneManager;
    private final AnimationManager animationManager;
    private final InputSystemImpl inputSystem;
    private final MainController mainController;

    public ViewportController(SceneManager sceneManager, AnimationManager animationManager,
                            InputSystemImpl inputSystem, MainController mainController) {
        this.sceneManager = sceneManager;
        this.animationManager = animationManager;
        this.inputSystem = inputSystem;
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        // setupCanvasBinding(); без ограничений расширяется
        setupInputHandlers();
    }

    private void setupCanvasBinding() {
        canvas.widthProperty().bind(canvasContainer.widthProperty());
        canvas.heightProperty().bind(canvasContainer.heightProperty());
    }

    private void setupInputHandlers() {
        inputSystem.setupMouseHandlers(canvas);
        inputSystem.setupKeyboardHandlers(canvas, mainController::requestRender);
    }

    public void renderFrame() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        sceneManager.render(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight());
    }

    public Canvas getCanvas() {
        return canvas;
    }
}