package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.UIManager;
import com.cgvsu.render_engine.Transform;
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
    private final UIManager uiManager;

    public TransformController(SceneManager sceneManager, UIManager uiManager) {
        this.sceneManager = sceneManager;
        this.uiManager = uiManager;
    }

    @FXML
    private void initialize() {
        setupUI();
        setupListeners();
        hidePanel(); // По умолчанию скрыта
    }

    private void setupUI() {
        uiManager.setupTransformSpinners(
            translateXField, translateYField, translateZField,
            rotateXField, rotateYField, rotateZField,
            scaleXField, scaleYField, scaleZField
        );
    }

    private void setupListeners() {
        setupSpinnerListener(translateXField);
        setupSpinnerListener(translateYField);
        setupSpinnerListener(translateZField);
        setupSpinnerListener(rotateXField);
        setupSpinnerListener(rotateYField);
        setupSpinnerListener(rotateZField);
        setupSpinnerListener(scaleXField);
        setupSpinnerListener(scaleYField);
        setupSpinnerListener(scaleZField);

        // Слушатель изменений трансформации из SceneManager
        sceneManager.transformProperty().addListener((obs, oldTransform, newTransform) -> {
            if (!uiManager.isUpdatingFromModel()) {
                updateSpinnersFromTransform(newTransform);
            }
        });
    }

    private void setupSpinnerListener(Spinner<Double> spinner) {
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> handleTransformChange());
    }

    @FXML
    private void handleResetTransform() {
        sceneManager.resetTransform();
        updateSpinnersFromTransform(sceneManager.getTransform());
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

    private void updateSpinnersFromTransform(Transform transform) {
        uiManager.updateSpinnersFromTransform(
            transform,
            translateXField, translateYField, translateZField,
            rotateXField, rotateYField, rotateZField,
            scaleXField, scaleYField, scaleZField
        );
    }

    public void showPanel() {
        transformPanel.setVisible(true);
        transformPanel.setManaged(true);
        updateSpinnersFromTransform(sceneManager.getTransform());
    }

    public void hidePanel() {
        transformPanel.setVisible(false);
        transformPanel.setManaged(false);
    }
}