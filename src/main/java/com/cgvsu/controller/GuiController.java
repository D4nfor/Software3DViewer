package com.cgvsu.controller;

import com.cgvsu.manager.*;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Transform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

import java.util.Optional;

public class GuiController {
    @FXML private AnchorPane canvasContainer;
    @FXML private BorderPane borderPane;
    @FXML private Canvas canvas;
    @FXML private VBox transformPanel;

    @FXML private Spinner<Double> translateXField, translateYField, translateZField;
    @FXML private Spinner<Double> rotateXField, rotateYField, rotateZField;
    @FXML private Spinner<Double> scaleXField, scaleYField, scaleZField;

    private RenderManager renderManager;
    private AnimationManager animationManager;
    private UIManager uiManager;
    private FileManager fileManager;
    private InputManager inputManager;

    @FXML
    private void initialize() {
        initializeManagers();
        setupUI();
//        setupCanvasBinding();  надо фиксить поскольку canvas закрывает panel
        setupInputHandlers();
        setupTransformListeners();

        if (animationManager != null) {
            animationManager.start();
        }
        hideTransformPanel();
    }

    private void initializeManagers() {
        this.renderManager = new RenderManager();
        this.animationManager = new AnimationManager(this::renderFrame);
        this.uiManager = new UIManager();
        this.fileManager = new FileManager();
        this.inputManager = new InputManager(renderManager.getCamera());
    }

    private void setupInputHandlers() {
        if (inputManager != null && canvas != null) {
            inputManager.setupMouseHandlers(canvas);
            inputManager.setupKeyboardHandlers(canvas, this::requestRender);
            inputManager.setHotkeyHandlers(
                    this::onOpenModelMenuItemClick,
                    this::onSaveModelMenuItemClick,
                    this::showTransformPanel,
                    this::hideTransformPanel
            );
        }
    }

    private void setupUI() {
        if (uiManager != null) {
            uiManager.setupTransformSpinners(
                    translateXField, translateYField, translateZField,
                    rotateXField, rotateYField, rotateZField,
                    scaleXField, scaleYField, scaleZField
            );
            setupSpinnerListeners();
        }
    }

    private void setupSpinnerListeners() {
        // Слушатели для позиции
        if (translateXField != null) {
            translateXField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }
        if (translateYField != null) {
            translateYField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }
        if (translateZField != null) {
            translateZField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }

        // Слушатели для вращения
        if (rotateXField != null) {
            rotateXField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }
        if (rotateYField != null) {
            rotateYField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }
        if (rotateZField != null) {
            rotateZField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }

        // Слушатели для масштаба
        if (scaleXField != null) {
            scaleXField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }
        if (scaleYField != null) {
            scaleYField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }
        if (scaleZField != null) {
            scaleZField.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
        }
    }

    private void setupTransformListeners() {
        if (renderManager != null && uiManager != null) {
            renderManager.transformProperty().addListener((obs, oldTransform, newTransform) -> {
                if (!uiManager.isUpdatingFromModel()) {
                    uiManager.updateSpinnersFromTransform(
                            newTransform,
                            translateXField, translateYField, translateZField,
                            rotateXField, rotateYField, rotateZField,
                            scaleXField, scaleYField, scaleZField
                    );
                }
            });
        }
    }

    private void setupCanvasBinding() {
        if (borderPane != null && canvas != null) {
            canvas.widthProperty().bind(borderPane.widthProperty());
            canvas.heightProperty().bind(borderPane.heightProperty());
        }
    }
    @FXML
    private void onOpenModelMenuItemClick() {
        if (fileManager != null && canvas != null && canvas.getScene() != null) {
            fileManager.openModelFile(
                    canvas.getScene().getWindow(),
                    this::onModelLoaded,
                    this::onModelLoadError
            );
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        if (fileManager != null && renderManager != null) {
            Model currentModel = renderManager.getModel();
            if (currentModel == null) {
                showAlert("No model loaded", "Please open a model first.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Model");
            alert.setHeaderText("Choose which version to save");
            alert.setContentText("Do you want to save the original model or the transformed one?");

            ButtonType btnOriginal = new ButtonType("Original");
            ButtonType btnTransformed = new ButtonType("Transformed");
            ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(btnOriginal, btnTransformed, btnCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() == btnCancel) {
                return;
            }

            Model toSave = (result.get() == btnTransformed)
                    ? renderManager.getTransformedModel()
                    : renderManager.getModel();

            fileManager.saveModelFile(
                    canvas.getScene().getWindow(),
                    toSave,
                    msg -> showAlert("Success", msg),
                    err -> showAlert("Error", err)
            );
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void onModelLoaded(Model model) {
        if (renderManager != null) {
            renderManager.setModel(model);
            renderManager.resetTransform();
            requestRender();
        }
    }

    private void onModelLoadError(String errorMessage) {
        showErrorDialog(errorMessage);
    }

    @FXML
    private void showTransformPanel() {
        if (transformPanel != null && uiManager != null && renderManager != null) {
            transformPanel.setVisible(true);
            transformPanel.setManaged(true);
            uiManager.updateSpinnersFromTransform(
                    renderManager.getTransform(),
                    translateXField, translateYField, translateZField,
                    rotateXField, rotateYField, rotateZField,
                    scaleXField, scaleYField, scaleZField
            );
        }
    }
    @FXML
    private void hideTransformPanel() {
        if (transformPanel != null) {
            transformPanel.setVisible(false);
            transformPanel.setManaged(false);
        }
    }

    @FXML
    private void handleResetTransform() {
        if (renderManager != null && uiManager != null) {
            renderManager.resetTransform();
            uiManager.updateSpinnersFromTransform(
                    renderManager.getTransform(),
                    translateXField, translateYField, translateZField,
                    rotateXField, rotateYField, rotateZField,
                    scaleXField, scaleYField, scaleZField
            );
        }
    }

    private void handleTransformChange() {
        if (uiManager == null || uiManager.isUpdatingFromModel()) return;

        Transform transform = uiManager.createTransformFromSpinners(
                translateXField, translateYField, translateZField,
                rotateXField, rotateYField, rotateZField,
                scaleXField, scaleYField, scaleZField
        );

        if (renderManager != null) {
            renderManager.setTransform(transform);
        }
    }

    public void requestRender() {
        renderFrame();
    }

    private void renderFrame() {
        if (canvas != null && renderManager != null) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            renderManager.render(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight());
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void cleanup() {
        if (animationManager != null) {
            animationManager.stop();
        }
    }
}