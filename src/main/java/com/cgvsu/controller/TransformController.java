package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.UIManager;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.transform.Transform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;

public class TransformController {
    @FXML private VBox transformPanel;
    @FXML private Spinner<Double> translateXField, translateYField, translateZField;
    @FXML private Spinner<Double> rotateXField, rotateYField, rotateZField;
    @FXML private Spinner<Double> scaleXField, scaleYField, scaleZField;
    @FXML private Button resetButton;

    private final SceneManager sceneManager;
    private UIManager uiManager;

    public TransformController(SceneManager sceneManager, UIManager uiManager) {
        this.sceneManager = sceneManager;
        this.uiManager = uiManager;
    }

    @FXML
    private void initialize() {
        setupUI();
        setupListeners();
        hidePanel();
    }

    private void setupUI() {
        uiManager.setupTransformSpinners(
                translateXField, translateYField, translateZField,
                rotateXField, rotateYField, rotateZField,
                scaleXField, scaleYField, scaleZField
        );
    }

    private void setupListeners() {
        // слушатели спиннеров
        setupSpinnerListener(translateXField);
        setupSpinnerListener(translateYField);
        setupSpinnerListener(translateZField);
        setupSpinnerListener(rotateXField);
        setupSpinnerListener(rotateYField);
        setupSpinnerListener(rotateZField);
        setupSpinnerListener(scaleXField);
        setupSpinnerListener(scaleYField);
        setupSpinnerListener(scaleZField);

        // слушаем смену активной модели
        sceneManager.activeModelProperty().addListener((obs, oldModel, newModel) -> {
            if (oldModel != null) {
                oldModel.transformProperty().removeListener(transformListener);
            }
            if (newModel != null) {
                newModel.transformProperty().addListener(transformListener);
                updateSpinnersFromTransform(newModel.getTransform());
                setSpinnersEditable(true);
            } else {
                setSpinnersEditable(false); // если модели нет — спиннеры заблокированы
            }
        });

        Model activeModel = sceneManager.getActiveModel();
        if (activeModel != null) {
            activeModel.transformProperty().addListener(transformListener);
            updateSpinnersFromTransform(activeModel.getTransform());
            setSpinnersEditable(true);
        } else {
            setSpinnersEditable(false);
        }
    }

    private final ChangeListener<Transform> transformListener = (obs, oldTransform, newTransform) -> {
        if (!uiManager.isUpdatingFromModel()) {
            updateSpinnersFromTransform(newTransform);
        }
    };

    private void setupSpinnerListener(Spinner<Double> spinner) {
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
    }

    @FXML
    private void handleResetTransform() {
        Model activeModel = sceneManager.getActiveModel();
        if (activeModel == null) return;

        activeModel.setTransform(new Transform());
        updateSpinnersFromTransform(activeModel.getTransform());
    }

    private void handleTransformChange() {
        if (uiManager.isUpdatingFromModel()) return;

        Model activeModel = sceneManager.getActiveModel();
        if (activeModel == null) return; // если модели нет — ничего не делаем

        Transform transform = uiManager.createTransformFromSpinners(
                translateXField, translateYField, translateZField,
                rotateXField, rotateYField, rotateZField,
                scaleXField, scaleYField, scaleZField
        );

        activeModel.setTransform(transform);
    }

    private void updateSpinnersFromTransform(Transform transform) {
        uiManager.updateSpinnersFromTransform(
                transform,
                translateXField, translateYField, translateZField,
                rotateXField, rotateYField, rotateZField,
                scaleXField, scaleYField, scaleZField
        );
    }

    // метод блокировки/разблокировки спиннеров
    private void setSpinnersEditable(boolean editable) {
        translateXField.setDisable(!editable);
        translateYField.setDisable(!editable);
        translateZField.setDisable(!editable);
        rotateXField.setDisable(!editable);
        rotateYField.setDisable(!editable);
        rotateZField.setDisable(!editable);
        scaleXField.setDisable(!editable);
        scaleYField.setDisable(!editable);
        scaleZField.setDisable(!editable);
        resetButton.setDisable(!editable);
    }

    public void showPanel() {
        transformPanel.setVisible(true);
        transformPanel.setManaged(true);
        // спиннеры будут заблокированы, если модели нет
    }

    public void hidePanel() {
        transformPanel.setVisible(false);
        transformPanel.setManaged(false);
    }

    public VBox getPanel() {
        return transformPanel;
    }
}
