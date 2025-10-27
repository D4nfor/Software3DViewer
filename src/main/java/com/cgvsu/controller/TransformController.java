package com.cgvsu.controller;

import com.cgvsu.render_engine.Transform;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class TransformController {
    private Transform transform = new Transform(); // объект для преобразований


    @FXML private HBox transformPanel;
    @FXML private Slider translateXSlider, translateYSlider, translateZSlider;
    @FXML
    private Slider rotateXSlider, rotateYSlider, rotateZSlider;
    @FXML private Slider scaleSlider;

    private Runnable onTransformChange;

    @FXML
    private void initialize() {
        setupSliderListeners();
    }

    private void setupSliderListeners() {
        // Для трансляции
        addSliderListener(translateXSlider, this::handleTranslateX);
        addSliderListener(translateYSlider, this::handleTranslateY);
        addSliderListener(translateZSlider, this::handleTranslateZ);

        // Для вращения
        addSliderListener(rotateXSlider, this::handleRotateX);
        addSliderListener(rotateYSlider, this::handleRotateY);
        addSliderListener(rotateZSlider, this::handleRotateZ);

        // Для масштаба
        addSliderListener(scaleSlider, this::handleScale);
    }

    private void addSliderListener(Slider slider, Runnable handler) {
        if (slider != null) {
            slider.valueProperty().addListener((obs, oldVal, newVal) -> handler.run());
        }
    }

    // Изменяем сеттер чтобы обновлять слайдеры при смене transform
    public void setTransform(Transform transform) {
        this.transform = transform;
        updateSlidersFromTransform();
    }

    public Transform getTransform() {
        return transform;
    }
    
    public void setOnTransformChange(Runnable callback) {
        this.onTransformChange = callback;
    }
    
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
    
    // ОБНОВЛЯЕМ СЛАЙДЕРЫ ПРИ ИЗМЕНЕНИИ TRANSFORM
    private void updateSlidersFromTransform() {
        if (transform == null) return;
        
        if (translateXSlider != null) translateXSlider.setValue(transform.translateX);
        if (translateYSlider != null) translateYSlider.setValue(transform.translateY);
        if (translateZSlider != null) translateZSlider.setValue(transform.translateZ);
        
        if (rotateXSlider != null) rotateXSlider.setValue(Math.toDegrees(transform.rotateX));
        if (rotateYSlider != null) rotateYSlider.setValue(Math.toDegrees(transform.rotateY));
        if (rotateZSlider != null) rotateZSlider.setValue(Math.toDegrees(transform.rotateZ));
        
        if (scaleSlider != null) scaleSlider.setValue(transform.scaleX);
    }
    
    // === ОБРАБОТЧИКИ СЛАЙДЕРОВ ===
    @FXML
    private void handleTranslateX() {
        if (transform != null) {
            transform.translateX = getSliderValue(translateXSlider);
            notifyChange();
        }
    }
    
    @FXML
    private void handleTranslateY() {
        if (transform != null) {
            transform.translateY = getSliderValue(translateYSlider);
            notifyChange();
        }
    }
    
    @FXML
    private void handleTranslateZ() {
        if (transform != null) {
            transform.translateZ = getSliderValue(translateZSlider);
            notifyChange();
        }
    }
    
    @FXML
    private void handleRotateX() {
        if (transform != null) {
            transform.rotateX = (float) Math.toRadians(getSliderValue(rotateXSlider));
            notifyChange();
        }
    }
    
    @FXML
    private void handleRotateY() {
        if (transform != null) {
            transform.rotateY = (float) Math.toRadians(getSliderValue(rotateYSlider));
            notifyChange();
        }
    }
    
    @FXML
    private void handleRotateZ() {
        if (transform != null) {
            transform.rotateZ = (float) Math.toRadians(getSliderValue(rotateZSlider));
            notifyChange();
        }
    }
    
    @FXML
    private void handleScale() {
        if (transform != null) {
            float scale = getSliderValue(scaleSlider);
            transform.scaleX = scale;
            transform.scaleY = scale;
            transform.scaleZ = scale;
            notifyChange();
        }
    }
    
    @FXML
    private void handleResetTransform() {
        if (transform != null) {
            // НЕ создаем новый Transform, а обнуляем существующий!
            transform.translateX = 0;
            transform.translateY = 0;
            transform.translateZ = 0;
            transform.rotateX = 0;
            transform.rotateY = 0;
            transform.rotateZ = 0;
            transform.scaleX = 1;
            transform.scaleY = 1;
            transform.scaleZ = 1;
            
            updateSlidersFromTransform(); // Обновляем слайдеры
            notifyChange();
        }
    }
    
    @FXML
    private void hideTransformPanel() {
        hidePanel();
    }
    
    private float getSliderValue(Slider slider) {
        return slider != null ? (float) slider.getValue() : 0;
    }
    
    private void notifyChange() {
        if (onTransformChange != null) {
            onTransformChange.run();
        }
    }
}