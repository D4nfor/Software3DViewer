package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.UIManager;
import com.cgvsu.render_engine.Transform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
        setupSpinnerListener(translateXField);
        setupSpinnerListener(translateYField);
        setupSpinnerListener(translateZField);
        setupSpinnerListener(rotateXField);
        setupSpinnerListener(rotateYField);
        setupSpinnerListener(rotateZField);
        setupSpinnerListener(scaleXField);
        setupSpinnerListener(scaleYField);
        setupSpinnerListener(scaleZField);

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

    // ОСТОРОЖНО НИЖЕ КОСТЫЛИ
    public void setScaleX(float value) {
        if (scaleXField != null) {
            SpinnerValueFactory<Double> factory = scaleXField.getValueFactory();
            if (factory != null) {
                factory.setValue((double) value);
            } else {
                scaleXField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void setScaleY(float value) {
        if (scaleYField != null) {
            SpinnerValueFactory<Double> factory = scaleYField.getValueFactory();
            if (factory != null) {
                factory.setValue((double) value);
            } else {
                scaleYField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void setScaleZ(float value) {
        if (scaleZField != null) {
            SpinnerValueFactory<Double> factory = scaleZField.getValueFactory();
            if (factory != null) {
                factory.setValue((double) value);
            } else {
                scaleZField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void setRotateX(float value) {
        if (rotateXField != null) {
            SpinnerValueFactory<Double> factory = rotateXField.getValueFactory();
            if (factory != null) {
                double normalized = value % 360;
                if (normalized < 0) normalized += 360;
                factory.setValue(normalized);
            } else {
                rotateXField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void setRotateY(float value) {
        if (rotateYField != null) {
            SpinnerValueFactory<Double> factory = rotateYField.getValueFactory();
            if (factory != null) {
                double normalized = value % 360;
                if (normalized < 0) normalized += 360;
                factory.setValue(normalized);
            } else {
                rotateYField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void setRotateZ(float value) {
        if (rotateZField != null) {
            SpinnerValueFactory<Double> factory = rotateZField.getValueFactory();
            if (factory != null) {
                double normalized = value % 360;
                if (normalized < 0) normalized += 360;
                factory.setValue(normalized);
            } else {
                rotateZField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void setTranslateX(float value) {
        if (translateXField != null) {
            SpinnerValueFactory<Double> factory = translateXField.getValueFactory();
            if (factory != null) {
                factory.setValue((double) value);
            } else {
                translateXField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void setTranslateY(float value) {
        if (translateYField != null) {
            SpinnerValueFactory<Double> factory = translateYField.getValueFactory();
            if (factory != null) {
                factory.setValue((double) value);
            } else {
                translateYField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void setTranslateZ(float value) {
        if (translateZField != null) {
            SpinnerValueFactory<Double> factory = translateZField.getValueFactory();
            if (factory != null) {
                factory.setValue((double) value);
            } else {
                translateZField.getEditor().setText(String.valueOf(value));
            }
        }
    }

    public void applyTransform() {
        handleTransformChange();
    }
}