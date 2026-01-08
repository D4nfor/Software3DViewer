package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.UIManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class ToolController {

    @FXML private StackPane contentPane;
    @FXML private Button transformButton;
    @FXML private Button deleteButton;
    @FXML private Button modelsButton;

    private TransformController transformController;
    private DeletionController deletionController;
    private ModelsController modelsController;
    private final SceneManager sceneManager;
    private final UIManager uiManager;

    public ToolController(SceneManager sceneManager, UIManager uiManager) {
        this.sceneManager = sceneManager;
        this.uiManager = uiManager;
    }

    @FXML
    private void initialize() {
        loadPanels();
        showModelsPanel();
    }


    private void loadPanels() {
        try {
            FXMLLoader transformLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/TransformPanel.fxml"));
            transformLoader.setControllerFactory(type -> {
                if (type == TransformController.class) {
                    return new TransformController(sceneManager, uiManager);
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create controller: " + type.getName(), e);
                }
            });
            Node transformNode = transformLoader.load();
            this.transformController = transformLoader.getController();
            transformNode.setVisible(false);
            contentPane.getChildren().add(transformNode);

            FXMLLoader deletionLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/DeletionPanel.fxml"));
            deletionLoader.setControllerFactory(type -> {
                if (type == DeletionController.class) {
                    return new DeletionController(sceneManager, uiManager);
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create controller: " + type.getName(), e);
                }
            });
            Node deletionNode = deletionLoader.load();
            this.deletionController = deletionLoader.getController();
            deletionNode.setVisible(false);
            contentPane.getChildren().add(deletionNode);

            FXMLLoader modelsLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/ModelsPanel.fxml"));
            modelsLoader.setControllerFactory(type -> {
                if (type == ModelsController.class) {
                    return new ModelsController(sceneManager, uiManager);
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Node modelsNode = modelsLoader.load();
            this.modelsController = modelsLoader.getController();
            modelsNode.setVisible(false);
            contentPane.getChildren().add(modelsNode);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showTransformPanel() {
        if (transformController != null) transformController.showPanel();
        if (deletionController != null) deletionController.hidePanel();
        if (modelsController != null) modelsController.hidePanel();
        updateMenuButtonStyles(true, false, false);
    }

    @FXML
    private void showDeletionPanel() {
        if (transformController != null) transformController.hidePanel();
        if (deletionController != null) deletionController.showPanel();
        if (modelsController != null) modelsController.hidePanel();
        updateMenuButtonStyles(false, true, false);
    }

    @FXML
    private void showModelsPanel() {
        if (transformController != null) transformController.hidePanel();
        if (deletionController != null) deletionController.hidePanel();
        if (modelsController != null) modelsController.showPanel();

        updateMenuButtonStyles(false, false, true);
    }


    private void updateMenuButtonStyles(boolean transformActive, boolean deleteActive, boolean modelsActive) {
        if (transformButton != null && deleteButton != null) {
            transformButton.getStyleClass().remove("button-primary");
            transformButton.getStyleClass().remove("button-secondary");
            deleteButton.getStyleClass().remove("button-primary");
            deleteButton.getStyleClass().remove("button-secondary");
            modelsButton.getStyleClass().remove("button-primary");
            modelsButton.getStyleClass().remove("button-secondary");

            if (transformActive) {
                transformButton.getStyleClass().add("button-primary");
                deleteButton.getStyleClass().add("button-secondary");
                modelsButton.getStyleClass().add("button-secondary");
            } else if (deleteActive) {
                transformButton.getStyleClass().add("button-secondary");
                deleteButton.getStyleClass().add("button-primary");
                modelsButton.getStyleClass().add("button-secondary");
            } else {
                transformButton.getStyleClass().add("button-secondary");
                deleteButton.getStyleClass().add("button-secondary");
                modelsButton.getStyleClass().add("button-primary");
            }
        }
    }
}