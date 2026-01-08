package com.cgvsu.controller;

import com.cgvsu.manager.AnimationManager;
import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.interfaces.InputManagerImpl;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;

public class ViewportController {
    @FXML private AnchorPane canvasContainer;
    @FXML private Canvas canvas;

    private final SceneManager sceneManager;
    private final AnimationManager animationManager;
    private final InputManagerImpl inputManager;
    private final MainController mainController;

    public ViewportController(SceneManager sceneManager, AnimationManager animationManager,
                              InputManagerImpl inputSystem, MainController mainController) {
        this.sceneManager = sceneManager;
        this.animationManager = animationManager;
        this.inputManager = inputSystem;
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        setupCanvasBinding();
        setupInputHandlers();
    }

    private void setupCanvasBinding() {
        canvas.widthProperty().bind(canvasContainer.widthProperty());
        canvas.heightProperty().bind(canvasContainer.heightProperty());

        canvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                mainController.requestRender();
            }
        });

        canvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                mainController.requestRender();
            }
        });
    }

    private void setupInputHandlers() {
        inputManager.setupMouseHandlers(canvas);
        inputManager.setupKeyboardHandlers(canvas, mainController::requestRender);
    }

    public void renderFrame() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (width > 0 && height > 0) {
            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            sceneManager.render(canvas.getGraphicsContext2D(), width, height);
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
}