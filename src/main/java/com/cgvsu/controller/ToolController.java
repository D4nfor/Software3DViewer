package com.cgvsu.controller;

import com.cgvsu.manager.*;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.rendering.RenderSettings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
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

        RenderSettings settings = sceneManager.getRenderSettings();

        // Галочка "Проводная модель"
        wireframeCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            settings.setWireframe(newV);
            mainController.requestRender();
        });

        // Галочка "Использовать текстуру"
        textureCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            Model activeModel = sceneManager.getActiveModel();

            if (newV) { // включаем текстуру
                if (activeModel == null) {
                    textureCheckBox.setSelected(false);
                    mainController.showAlert("Нет модели", "Сначала откройте модель.");
                    return;
                }

                if (activeModel.getTexture() == null) {
                    textureCheckBox.setSelected(false);
                    mainController.showAlert("Нет текстуры", "Сначала загрузите текстуру для модели.");
                    return;
                }

                // Всё ок — включаем отображение текстуры
                settings.setUseTexture(true);
            } else {
                // Выключаем текстуру
                settings.setUseTexture(false);
            }

            mainController.requestRender();
        });

        // Галочка "Освещение"
        lightingCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            settings.setUseLighting(newV); // только флаг освещения
            mainController.requestRender();
        });

        // Выбор базового цвета
        baseColorPicker.setValue(Color.GRAY);
        baseColorPicker.valueProperty().addListener((obs, oldV, newV) -> {
            settings.setBaseColor(newV);
            mainController.requestRender();
        });
    }

    /** Загрузка текстуры через диалог выбора файла */
    @FXML
    private void loadTexture() {
        Model activeModel = sceneManager.getActiveModel();
        if (activeModel == null) {
            mainController.showAlert("Нет модели", "Сначала откройте модель.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(baseColorPicker.getScene().getWindow());
        if (file != null) {
            try {
                Image img = new Image(file.toURI().toString());
                activeModel.setTexture(img);
                mainController.showAlert("Текстура загружена",
                        "Текстура успешно загружена. Включите галочку 'Использовать текстуру', чтобы отобразить её.");
            } catch (Exception e) {
                mainController.showAlert("Ошибка загрузки", "Не удалось загрузить текстуру: " + e.getMessage());
            }
        }
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
