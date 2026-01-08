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

    private TransformController transformController;
    private DeletionController deletionController;
    private final SceneManager sceneManager;
    private final UIManager uiManager;

    public ToolController(SceneManager sceneManager, UIManager uiManager) {
        this.sceneManager = sceneManager;
        this.uiManager = uiManager;
    }

    @FXML
    private void initialize() {
        loadPanels();
        showTransformPanel();
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showTransformPanel() {
        if (transformController != null) {
            transformController.showPanel();
        }
        if (deletionController != null) {
            deletionController.hidePanel();
        }
        updateMenuButtonStyles(true, false);
    }

    @FXML
    private void showDeletionPanel() {
        if (transformController != null) {
            transformController.hidePanel();
        }
        if (deletionController != null) {
            deletionController.showPanel();
        }
        updateMenuButtonStyles(false, true);
    }

    private void updateMenuButtonStyles(boolean transformActive, boolean deleteActive) {
        if (transformButton != null && deleteButton != null) {
            // Используем CSS классы вместо inline стилей
            transformButton.getStyleClass().remove("button-primary");
            transformButton.getStyleClass().remove("button-secondary");
            deleteButton.getStyleClass().remove("button-primary");
            deleteButton.getStyleClass().remove("button-secondary");

            if (transformActive) {
                transformButton.getStyleClass().add("button-primary");
                deleteButton.getStyleClass().add("button-secondary");
            } else {
                transformButton.getStyleClass().add("button-secondary");
                deleteButton.getStyleClass().add("button-primary");
            }
        }
    }
}