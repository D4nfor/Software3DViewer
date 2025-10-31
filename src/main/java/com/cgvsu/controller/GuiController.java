package com.cgvsu.controller;

import com.cgvsu.manager.*;
import com.cgvsu.manager.implementations.CameraInputSystem;
import com.cgvsu.manager.implementations.ObjModelManager;
import com.cgvsu.manager.implementations.DefaultRenderer;
import com.cgvsu.manager.interfaces.InputSystemImpl;
import com.cgvsu.manager.interfaces.ModelManagerImpl;
import com.cgvsu.manager.interfaces.RendererImpl;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Transform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

import java.util.Optional;

public class GuiController {
    @FXML private StackPane canvasContainer;
    @FXML private BorderPane borderPane;
    @FXML private Canvas canvas;
    @FXML private VBox transformPanel;
    @FXML private Spinner<Double> translateXField, translateYField, translateZField;
    @FXML private Spinner<Double> rotateXField, rotateYField, rotateZField;
    @FXML private Spinner<Double> scaleXField, scaleYField, scaleZField;

    private final SceneManager sceneManager;
    private final AnimationManager animationManager;
    private final UIManager uiManager;
    private final ModelManagerImpl modelManager;
    private final InputSystemImpl inputSystem;

    public GuiController(SceneManager sceneManager,
                         AnimationManager animationManager,
                         UIManager uiManager,
                         ModelManagerImpl modelManager,
                         InputSystemImpl inputSystem) {
        this.sceneManager = sceneManager;
        this.animationManager = animationManager;
        this.uiManager = uiManager;
        this.modelManager = modelManager;
        this.inputSystem = inputSystem;
    }

    public GuiController() {
        RendererImpl renderer = new DefaultRenderer();
        this.sceneManager = new SceneManager(renderer);
        this.animationManager = new AnimationManager(this::renderFrame);
        this.uiManager = new UIManager();
        this.modelManager = new ObjModelManager();
        this.inputSystem = new CameraInputSystem(sceneManager.getCamera());
    }

    @FXML
    private void initialize() {
        setupUI();
        setupInputHandlers();
        setupTransformListeners();
        // setupCanvasBinding(); без контрольно расширяется

        animationManager.start();
        hideTransformPanel();
    }

    private void setupInputHandlers() {
        inputSystem.setupMouseHandlers(canvas);
        inputSystem.setupKeyboardHandlers(canvas, this::requestRender);
        inputSystem.setHotkeyHandlers(
                this::onOpenModelMenuItemClick,
                this::onSaveModelMenuItemClick,
                this::showTransformPanel,
                this::hideTransformPanel
        );
    }

    private void setupUI() {
        uiManager.setupTransformSpinners(
                translateXField, translateYField, translateZField,
                rotateXField, rotateYField, rotateZField,
                scaleXField, scaleYField, scaleZField
        );
        setupSpinnerListeners();
    }

    private void setupSpinnerListeners() {
        setupSpinnerListener(translateXField);
        setupSpinnerListener(translateYField);
        setupSpinnerListener(translateZField);
        setupSpinnerListener(rotateXField);
        setupSpinnerListener(rotateYField);
        setupSpinnerListener(rotateZField);
        setupSpinnerListener(scaleXField);
        setupSpinnerListener(scaleYField);
        setupSpinnerListener(scaleZField);
    }

    private void setupSpinnerListener(Spinner<Double> spinner) {
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
    }

    private void setupTransformListeners() {
        sceneManager.transformProperty().addListener((obs, oldTransform, newTransform) -> {
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

    private void setupCanvasBinding() {
//        canvasContainer.maxWidthProperty().bind(borderPane.widthProperty().subtract(250));
//        canvasContainer.maxHeightProperty().bind(borderPane.heightProperty());
        canvas.widthProperty().bind(canvasContainer.widthProperty());
        canvas.heightProperty().bind(canvasContainer.heightProperty());
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        modelManager.openModelFile(
                canvas.getScene().getWindow(),
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
                canvas.getScene().getWindow(),
                toSave,
                msg -> showAlert("Success", msg),
                err -> showAlert("Error", err)
        );
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

    private void onModelLoaded(Model model) {
        sceneManager.setModel(model);
        sceneManager.resetTransform();
        requestRender();
    }

    private void onModelLoadError(String errorMessage) {
        showErrorDialog(errorMessage);
    }

    @FXML
    private void showTransformPanel() {
        transformPanel.setVisible(true);
        transformPanel.setManaged(true);
        uiManager.updateSpinnersFromTransform(
                sceneManager.getTransform(),
                translateXField, translateYField, translateZField,
                rotateXField, rotateYField, rotateZField,
                scaleXField, scaleYField, scaleZField
        );
    }

    @FXML
    private void hideTransformPanel() {
        transformPanel.setVisible(false);
        transformPanel.setManaged(false);
    }

    @FXML
    private void handleResetTransform() {
        sceneManager.resetTransform();
        uiManager.updateSpinnersFromTransform(
                sceneManager.getTransform(),
                translateXField, translateYField, translateZField,
                rotateXField, rotateYField, rotateZField,
                scaleXField, scaleYField, scaleZField
        );
    }

    private void handleTransformChange() {
        if (uiManager.isUpdatingFromModel()) return;

        Transform transform = uiManager.createTransformFromSpinners(
                translateXField, translateYField, translateZField,
                rotateXField, rotateYField, rotateZField,
                scaleXField, scaleYField, scaleZField
        );

        sceneManager.setTransform(transform);
    }

    public void requestRender() {
        renderFrame();
    }

    private void renderFrame() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        sceneManager.render(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight());
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void cleanup() {
        animationManager.stop();
    }
}