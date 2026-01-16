package com.cgvsu.controller;

import com.cgvsu.manager.*;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.rendering.RenderSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ToolController {

    @FXML private StackPane contentPane;
    @FXML private Button transformButton;
    @FXML private Button deleteButton;
    @FXML private Button modelsButton;
    @FXML private CheckBox wireframeCheckBox;
    @FXML private CheckBox textureCheckBox;
    @FXML private CheckBox lightingCheckBox;
    @FXML private ColorPicker baseColorPicker;

    @FXML private ComboBox<Camera> cameraComboBox;
    @FXML private Button addCameraButton;
    @FXML private Button removeCameraButton;

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

        setupCameraControls();
        setupRenderSettings();
    }

    /** Настройка галочек и ColorPicker */
    private void setupRenderSettings() {
        RenderSettings settings = sceneManager.getRenderSettings();

        wireframeCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            settings.setWireframe(newV);
            mainController.requestRender();
        });

        textureCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            Model activeModel = sceneManager.getActiveModel();
            if (newV) {
                if (activeModel == null || activeModel.getTexture() == null) {
                    textureCheckBox.setSelected(false);
                    mainController.showAlert(
                            activeModel == null ? "Нет модели" : "Нет текстуры",
                            activeModel == null ? "Сначала откройте модель." :
                                    "Сначала загрузите текстуру для модели."
                    );
                    return;
                }
                settings.setUseTexture(true);
            } else {
                settings.setUseTexture(false);
            }
            settings.setBaseColor(baseColorPicker.getValue());
            mainController.requestRender();
        });

        lightingCheckBox.selectedProperty().addListener((obs, oldV, newV) -> {
            settings.setUseLighting(newV);
            settings.setBaseColor(baseColorPicker.getValue());
            mainController.requestRender();
        });

        baseColorPicker.setValue(Color.GRAY);
        baseColorPicker.valueProperty().addListener((obs, oldV, newV) -> {
            settings.setBaseColor(newV);
            mainController.requestRender();
        });
    }

    /** Настройка панели камер */
    private void setupCameraControls() {
        // Сначала загружаем существующие камеры
        ObservableList<Camera> cameras = sceneManager.getCameras();
        cameraComboBox.setItems(cameras);

        // Выбираем активную камеру
        cameraComboBox.setValue(sceneManager.getActiveCamera());

        // При смене выбора
        cameraComboBox.valueProperty().addListener((obs, oldCam, newCam) -> {
            if (newCam != null) {
                sceneManager.setActiveCamera(newCam);
                mainController.requestRender();
            }
        });

        // Кнопка добавления камеры
        addCameraButton.setOnAction(e -> {
            Camera newCam = new Camera(
                    "Cam-" + UUID.randomUUID().toString().substring(0, 4),
                    new com.cgvsu.utils.math.Vector3f(0, 0, 100),
                    new com.cgvsu.utils.math.Vector3f(0, 0, 0),
                    1.0f, 1f, 0.01f, 100f
            );
            sceneManager.addCamera(newCam);
            cameraComboBox.setValue(newCam); // автоматически выбрать новую камеру
            mainController.requestRender();
        });

        // Кнопка удаления камеры
        removeCameraButton.setOnAction(e -> {
            Camera cam = cameraComboBox.getValue();
            if (cam != null) {
                sceneManager.removeCamera(cam);
                Camera firstCam = sceneManager.getCameras().isEmpty() ? null : sceneManager.getCameras().get(0);
                cameraComboBox.setValue(firstCam);
                mainController.requestRender();
            }
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
            } catch (Exception ex) {
                mainController.showAlert("Ошибка загрузки", "Не удалось загрузить текстуру: " + ex.getMessage());
            }
        }
    }

    /** Загрузка сменяемых панелей (Transform, Deletion, Models) */
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

    @FXML private void showTransformPanel() {
        transformController.showPanel();
        deletionController.hidePanel();
        modelsController.hidePanel();
        updateMenuButtonStyles(true, false, false);
    }

    @FXML private void showDeletionPanel() {
        transformController.hidePanel();
        deletionController.showPanel();
        modelsController.hidePanel();
        updateMenuButtonStyles(false, true, false);
    }

    @FXML private void showModelsPanel() {
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
