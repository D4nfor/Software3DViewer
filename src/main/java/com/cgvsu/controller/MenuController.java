package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.interfaces.FileManagerImpl;
import com.cgvsu.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;

import java.util.Optional;

public class MenuController {
    @FXML private MenuBar menuBar;

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