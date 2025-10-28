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

    private void setupSpinners() {
        // POSITION
        initSpinner(translateXField, -10.0, 10.0, 0.0, 0.1, this::handleTranslate);
        initSpinner(translateYField, -10.0, 10.0, 0.0, 0.1, this::handleTranslate);
        initSpinner(translateZField, -10.0, 10.0, 0.0, 0.1, this::handleTranslate);

        // ROTATION (degrees)
        initSpinner(rotateXField, -180.0, 180.0, 0.0, 1.0, this::handleRotate);
        initSpinner(rotateYField, -180.0, 180.0, 0.0, 1.0, this::handleRotate);
        initSpinner(rotateZField, -180.0, 180.0, 0.0, 1.0, this::handleRotate);

        // SCALE
        initSpinner(scaleXField, 0.1, 5.0, 1.0, 0.1, this::handleScale);
        initSpinner(scaleYField, 0.1, 5.0, 1.0, 0.1, this::handleScale);
        initSpinner(scaleZField, 0.1, 5.0, 1.0, 0.1, this::handleScale);
    }

    private void initSpinner(Spinner<Double> spinner, double min, double max, double init, double step, Runnable handler) {
        if (spinner != null) {
            spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, init, step));
            spinner.valueProperty().addListener((obs, oldVal, newVal) -> handler.run());
        }
    }

    // === Handlers ===
    private void handleTranslate() {
        if (transform == null) return;
        transform.translateX = parseFloat(translateXField.getEditor());
        transform.translateY = parseFloat(translateYField.getEditor());
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

    private void handleRotate() {
        if (transform != null) {
            transform.rotateX = (float) Math.toRadians(getValue(rotateXField));
            transform.rotateY = (float) Math.toRadians(getValue(rotateYField));
            transform.rotateZ = (float) Math.toRadians(getValue(rotateZField));
            notifyChange();
        }
    }

    private void handleScale() {
        if (transform != null) {
            transform.scaleX = scaleXField.getValue().floatValue();
            transform.scaleY = scaleYField.getValue().floatValue();
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

    private void    notifyChange() {
        if (onTransformChange != null) {
            onTransformChange.run();
        }
    }
}
