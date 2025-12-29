package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.interfaces.FileManagerImpl;
import com.cgvsu.model.Model;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.Random;

public class MenuController {
    @FXML private MenuBar menuBar;
    @FXML private MenuItem animationMenuItem;

    private final FileManagerImpl modelManager;
    private final SceneManager sceneManager;
    private final MainController mainController;
    private TransformController transformController;

    public MenuController(FileManagerImpl modelManager, SceneManager sceneManager, MainController mainController) {
        this.modelManager = modelManager;
        this.sceneManager = sceneManager;
        this.mainController = mainController;
    }

    public void setTransformController(TransformController transformController) {
        this.transformController = transformController;
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        modelManager.openModelFile(
            menuBar.getScene().getWindow(),
            this::onModelLoaded,
            this::onModelLoadError
        );
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        Model currentModel = sceneManager.getModel();
        if (currentModel == null) {
            showAlert("No model loaded", "Please open a model first.");
            return;
        }

        Alert alert = createSaveDialog();
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty() || result.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
            return;
        }

        Model toSave = (result.get().getText().equals("Transformed"))
                ? sceneManager.getTransformedModel()
                : sceneManager.getModel();

        modelManager.saveModelFile(
            menuBar.getScene().getWindow(),
            toSave,
            msg -> showAlert("Success", msg),
            err -> showAlert("Error", err)
        );
    }

    @FXML
    private void showTransformPanel() {
        if (transformController != null) {
            transformController.showPanel();
        }
    }

    @FXML
    private void hideTransformPanel() {
        if (transformController != null) {
            transformController.hidePanel();
        }
    }
    // ЭТО УДАЛИТЬ
    private boolean isAnimating = false;
    private AnimationTimer simpleAnimationTimer;
    private float animationTime = 0;
    private Random random = new Random();
    @FXML
    private void onAnimationMenuItemClick() {
        if (transformController == null || sceneManager.getModel() == null) {
            showAlert("Error", "Please load a model first.");
            return;
        }

        if (isAnimating) {
            stopSimpleAnimation();
            animationMenuItem.setText("Start Random Animation");
        } else {
            startRandomAnimation();
            animationMenuItem.setText("Stop Animation");
        }

        isAnimating = !isAnimating;
    }

    private void startRandomAnimation() {
        stopSimpleAnimation();

        simpleAnimationTimer = new AnimationTimer() {
            private long lastTime = 0;
            private float timeSinceLastChange = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                float deltaTime = (now - lastTime) / 1_000_000_000.0f;
                lastTime = now;
                animationTime += deltaTime;
                timeSinceLastChange += deltaTime;

                float changeInterval = 0.3f;
                if (timeSinceLastChange >= changeInterval) {
                    timeSinceLastChange = 0;

                    transformController.setScaleX(random.nextFloat() * 1.5f + 0.5f);     // 0.5 - 2.0
                    transformController.setScaleY(random.nextFloat() * 1.5f + 0.5f);
                    transformController.setScaleZ(random.nextFloat() * 1.5f + 0.5f);
                    transformController.setRotateX(random.nextFloat() * 15f);
                    transformController.setRotateY(random.nextFloat() * 15f);
                    transformController.setRotateZ(random.nextFloat() * 15f);
                    transformController.setTranslateX(random.nextFloat() * 4f - 2f);     // -1.0 - +1.0
                    transformController.setTranslateY(random.nextFloat() * 2f - 1f);
                    transformController.setTranslateZ(random.nextFloat() * 4f - 2f);

                    transformController.applyTransform();
                    mainController.requestRender();
                }
            }
        };

        simpleAnimationTimer.start();
    }

    private void stopSimpleAnimation() {
        if (simpleAnimationTimer != null) {
            simpleAnimationTimer.stop();
            simpleAnimationTimer = null;
        }

        animationTime = 0;
        if (transformController != null) {
            transformController.setScaleX(1.0f);
            transformController.setScaleY(1.0f);
            transformController.setScaleZ(1.0f);
            transformController.setRotateX(0.0f);
            transformController.setRotateY(0.0f);
            transformController.setRotateZ(0.0f);
            transformController.setTranslateX(0.0f);
            transformController.setTranslateY(0.0f);
            transformController.setTranslateZ(0.0f);
            transformController.applyTransform();
            mainController.requestRender();
        }
        isAnimating = false;
    }
    // конец
    private void onModelLoaded(Model model) {
        sceneManager.setModel(model);
        sceneManager.resetTransform();
        mainController.requestRender();
    }

    private void onModelLoadError(String errorMessage) {
        showErrorDialog(errorMessage);
    }

    private Alert createSaveDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Model");
        alert.setHeaderText("Choose which version to save");
        alert.setContentText("Do you want to save the original model or the transformed one?");

        ButtonType btnOriginal = new ButtonType("Original");
        ButtonType btnTransformed = new ButtonType("Transformed");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnOriginal, btnTransformed, btnCancel);
        return alert;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}