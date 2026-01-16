package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.UIManager;
import com.cgvsu.render_engine.rendering.RenderSettings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ToolController {

    @FXML private StackPane contentPane;
    @FXML private Button transformButton;
    @FXML private Button deleteButton;
    @FXML private Button modelsButton;
    @FXML private CheckBox wireframeCheckBox;
    @FXML private CheckBox textureCheckBox;
    @FXML private CheckBox lightingCheckBox;
    @FXML private ColorPicker baseColorPicker;

    private TransformController transformController;
    private DeletionController deletionController;
    private ModelsController modelsController;

    private final SceneManager sceneManager;
    private final UIManager uiManager;
    private final MainController mainController;

    public ToolController(SceneManager sceneManager, UIManager uiManager, MainController mainController) {
        this.sceneManager = sceneManager;
        this.uiManager = uiManager;
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        loadPanels();
        showModelsPanel();

        // Привязываем галочки и ColorPicker к RenderSettings
        RenderSettings settings = sceneManager.getRenderSettings();

        wireframeCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            settings.setWireframe(newV);
            mainController.requestRender();
        });

        textureCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            settings.setUseTexture(newV);
            mainController.requestRender();
        });

        lightingCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            settings.setUseLighting(newV);
            mainController.requestRender();
        });

        baseColorPicker.setValue(Color.GRAY);
        baseColorPicker.valueProperty().addListener((obs, oldV, newV) -> {
            settings.setBaseColor(newV);
            mainController.requestRender();
        });
    }

    private void loadPanels() {
        try {
            // Transform Panel
            FXMLLoader transformLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/TransformPanel.fxml"));
            transformLoader.setControllerFactory(type -> {
                if (type == TransformController.class) return new TransformController(sceneManager, uiManager);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            Node transformNode = transformLoader.load();
            transformController = transformLoader.getController();
            transformNode.setVisible(false);
            contentPane.getChildren().add(transformNode);

            // Deletion Panel
            FXMLLoader deletionLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/DeletionPanel.fxml"));
            deletionLoader.setControllerFactory(type -> {
                if (type == DeletionController.class) return new DeletionController(sceneManager, uiManager);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            Node deletionNode = deletionLoader.load();
            deletionController = deletionLoader.getController();
            deletionNode.setVisible(false);
            contentPane.getChildren().add(deletionNode);

            // Models Panel
            FXMLLoader modelsLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/ModelsPanel.fxml"));
            modelsLoader.setControllerFactory(type -> {
                if (type == ModelsController.class) return new ModelsController(sceneManager, uiManager);
                try { return type.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            Node modelsNode = modelsLoader.load();
            modelsController = modelsLoader.getController();
            modelsNode.setVisible(false);
            contentPane.getChildren().add(modelsNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showTransformPanel() {
        transformController.showPanel();
        deletionController.hidePanel();
        modelsController.hidePanel();
        updateMenuButtonStyles(true, false, false);
    }

    @FXML
    private void showDeletionPanel() {
        transformController.hidePanel();
        deletionController.showPanel();
        modelsController.hidePanel();
        updateMenuButtonStyles(false, true, false);
    }

    @FXML
    private void showModelsPanel() {
        transformController.hidePanel();
        deletionController.hidePanel();
        modelsController.showPanel();
        updateMenuButtonStyles(false, false, true);
    }

    private void updateMenuButtonStyles(boolean transformActive, boolean deleteActive, boolean modelsActive) {
        transformButton.getStyleClass().removeAll("button-primary", "button-secondary");
        deleteButton.getStyleClass().removeAll("button-primary", "button-secondary");
        modelsButton.getStyleClass().removeAll("button-primary", "button-secondary");

        transformButton.getStyleClass().add(transformActive ? "button-primary" : "button-secondary");
        deleteButton.getStyleClass().add(deleteActive ? "button-primary" : "button-secondary");
        modelsButton.getStyleClass().add(modelsActive ? "button-primary" : "button-secondary");
    }
}
