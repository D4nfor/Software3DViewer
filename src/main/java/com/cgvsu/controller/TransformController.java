package com.cgvsu.controller;

import com.cgvsu.render_engine.Transform;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TransformController {
    private Transform transform = new Transform();

    @FXML private VBox transformPanel;

    // === Numeric fields ===
    @FXML private Spinner<Double> translateXField, translateYField, translateZField;
    @FXML private Spinner<Double> rotateXField, rotateYField, rotateZField;
    @FXML private Spinner<Double> scaleXField, scaleYField, scaleZField;
    private Runnable onTransformChange;

    @FXML
    private void initialize() {
        setupSpinners();
    }

    @FXML
    private void handleResetTransform() {
        if (transform != null) {
            transform.translateX = 0;
            transform.translateY = 0;
            transform.translateZ = 0;

            transform.rotateX = 0;
            transform.rotateY = 0;
            transform.rotateZ = 0;

            transform.scaleX = 1;
            transform.scaleY = 1;
            transform.scaleZ = 1;

            // Обновление спиннеров
            updateSpinnersFromTransform();
            notifyChange();
        }
    }


    private void setupSpinners() {
        // POSITION
        initSpinner(translateXField, -10.0, 10.0, 0.0, 0.1, this::handleXTranslate);
        initSpinner(translateYField, -10.0, 10.0, 0.0, 0.1, this::handleYTranslate);
        initSpinner(translateZField, -10.0, 10.0, 0.0, 0.1, this::handleZTranslate);

        // ROTATION (degrees)
        initSpinner(rotateXField, -180.0, 180.0, 0.0, 1.0, this::handleXRotate);
        initSpinner(rotateYField, -180.0, 180.0, 0.0, 1.0, this::handleYRotate);
        initSpinner(rotateZField, -180.0, 180.0, 0.0, 1.0, this::handleZRotate);

        // SCALE
        initSpinner(scaleXField, 0.1, 5.0, 1.0, 0.1, this::handleXScale);
        initSpinner(scaleYField, 0.1, 5.0, 1.0, 0.1, this::handleYScale);
        initSpinner(scaleZField, 0.1, 5.0, 1.0, 0.1, this::handleZScale);
    }

    private void initSpinner(Spinner<Double> spinner, double min, double max, double init, double step, Runnable handler) {
        if (spinner != null) {
            spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, init, step));
            spinner.valueProperty().addListener((obs, oldVal, newVal) -> handler.run());
        }
    }

    // === Handlers ===
    private void handleXTranslate() {
        if (transform == null) return;
        transform.translateX = parseFloat(translateXField.getEditor());
        notifyChange();
    }
    private void handleYTranslate() {
        if (transform == null) return;
        transform.translateY = parseFloat(translateYField.getEditor());
        notifyChange();
    }
    private void handleZTranslate() {
        if (transform == null) return;
        transform.translateZ = parseFloat(translateZField.getEditor());
        notifyChange();
    }

    private float parseFloat(TextField field) {
        try {
            return Float.parseFloat(field.getText().replace(",", "."));
        } catch (Exception e) {
            return 0f;
        }
    }

    private void handleXRotate() {
        if (transform != null) {
            transform.rotateX = (float) Math.toRadians(getValue(rotateXField));
            notifyChange();
        }
    }
    private void handleYRotate() {
        if (transform != null) {
            transform.rotateY = (float) Math.toRadians(getValue(rotateYField));
            notifyChange();
        }
    }
    private void handleZRotate() {
        if (transform != null) {
            transform.rotateZ = (float) Math.toRadians(getValue(rotateZField));
            notifyChange();
        }
    }

    private void handleXScale() {
        if (transform != null) {
            transform.scaleX = scaleXField.getValue().floatValue();
            notifyChange();
        }
    }
    private void handleYScale() {
        if (transform != null) {
            transform.scaleY = scaleYField.getValue().floatValue();
            notifyChange();
        }
    }
    private void handleZScale() {
        if (transform != null) {
            transform.scaleZ = scaleZField.getValue().floatValue();
            notifyChange();
        }
    }

    private double getValue(Spinner<Double> spinner) {
        return spinner != null ? spinner.getValue() : 0.0;
    }

    private void updateSpinnersFromTransform() {
        if (transform == null) return;

        setSpinnerValue(translateXField, transform.translateX);
        setSpinnerValue(translateYField, transform.translateY);
        setSpinnerValue(translateZField, transform.translateZ);

        setSpinnerValue(rotateXField, Math.toDegrees(transform.rotateX));
        setSpinnerValue(rotateYField, Math.toDegrees(transform.rotateY));
        setSpinnerValue(rotateZField, Math.toDegrees(transform.rotateZ));

        setSpinnerValue(scaleXField, transform.scaleX);
        setSpinnerValue(scaleYField, transform.scaleY);
        setSpinnerValue(scaleZField, transform.scaleZ);
    }

    private void setSpinnerValue(Spinner<Double> spinner, double value) {
        if (spinner != null && spinner.getValueFactory() != null) {
            spinner.getValueFactory().setValue(value);
        }
    }

    // === Visibility ===
    public void showPanel() {
        if (transformPanel != null) {
            transformPanel.setVisible(true);
            transformPanel.setManaged(true);
        }
    }

    public void hidePanel() {
        if (transformPanel != null) {
            transformPanel.setVisible(false);
            transformPanel.setManaged(false);
        }
    }

    // === Data access ===
    public void setTransform(Transform transform) {
        this.transform = transform;
        updateSpinnersFromTransform();
    }

    public Transform getTransform() {
        return transform;
    }

    public void setOnTransformChange(Runnable callback) {
        this.onTransformChange = callback;
    }

    private void notifyChange() {
        if (onTransformChange != null) {
            onTransformChange.run();
        }
    }
}
