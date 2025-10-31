package com.cgvsu.manager.implementations;

import com.cgvsu.manager.interfaces.InputSystemImpl;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.Camera;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class CameraInputSystem implements InputSystemImpl {
    private final Camera camera;
    private boolean middleMousePressed = false;
    private double lastMouseX, lastMouseY;
    private float mouseSensitivity = 0.2f;
    private float moveSpeed = 0.5f;
    
    private Runnable onOpenModel;
    private Runnable onSaveModel;
    private Runnable onShowTransformPanel;
    private Runnable onHideTransformPanel;
    private Runnable onRenderRequest;

    public CameraInputSystem(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void setupKeyboardHandlers(Node targetNode, Runnable onRenderRequest) {
        this.onRenderRequest = onRenderRequest;
        targetNode.setFocusTraversable(true);
        
        targetNode.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            handleKeyPressed(event);
        });
    }

    @Override
    public void setupMouseHandlers(Node targetNode) {
        targetNode.setOnMousePressed(this::handleMousePressed);
        targetNode.setOnMouseReleased(this::handleMouseReleased);
        targetNode.setOnMouseDragged(this::handleMouseDragged);
        targetNode.setOnScroll(this::handleMouseScroll);
    }

    @Override
    public void setHotkeyHandlers(Runnable onOpenModel, Runnable onSaveModel, 
                                 Runnable onShowTransformPanel, Runnable onHideTransformPanel) {
        this.onOpenModel = onOpenModel;
        this.onSaveModel = onSaveModel;
        this.onShowTransformPanel = onShowTransformPanel;
        this.onHideTransformPanel = onHideTransformPanel;
    }

    private void handleKeyPressed(KeyEvent event) {
        boolean cameraMoved = false;
        
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case O -> { if (onOpenModel != null) { onOpenModel.run(); event.consume(); return; } }
                case S -> { if (onSaveModel != null) { onSaveModel.run(); event.consume(); return; } }
                case T -> { if (onShowTransformPanel != null) { onShowTransformPanel.run(); event.consume(); return; } }
                case G -> { if (onHideTransformPanel != null) { onHideTransformPanel.run(); event.consume(); return; } }
            }
        }

        switch (event.getCode()) {
            case W -> { camera.moveForward(moveSpeed); cameraMoved = true; }
            case S -> { camera.moveBackward(moveSpeed); cameraMoved = true; }
            case A -> { camera.moveLeft(moveSpeed); cameraMoved = true; }
            case D -> { camera.moveRight(moveSpeed); cameraMoved = true; }
            case SPACE -> { camera.moveUp(moveSpeed); cameraMoved = true; }
            case SHIFT -> { camera.moveDown(moveSpeed); cameraMoved = true; }
            case R -> { resetCamera(); cameraMoved = true; }
        }

        if (cameraMoved && onRenderRequest != null) {
            onRenderRequest.run();
        }
    }

    private void resetCamera() {
        camera.reset(new Vector3f(0, 0, 50), new Vector3f(0, 0, 0));
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            middleMousePressed = true;
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
            event.consume();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            middleMousePressed = false;
            event.consume();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (!middleMousePressed) return;

        double deltaX = event.getSceneX() - lastMouseX;
        double deltaY = event.getSceneY() - lastMouseY;

        if (event.isShiftDown()) {
            camera.moveRight((float) (-deltaX * mouseSensitivity * 0.05f));
            camera.moveUp((float) (deltaY * mouseSensitivity * 0.05f));
        } else if (event.isControlDown()) {
            camera.rotateHorizontal((float) (-deltaX * mouseSensitivity));
            camera.rotateVertical((float) (-deltaY * mouseSensitivity));
        } else {
            camera.orbitHorizontal((float) (-deltaX * mouseSensitivity));
            camera.orbitVertical((float) (-deltaY * mouseSensitivity));
        }

        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();

        if (onRenderRequest != null) onRenderRequest.run();
        event.consume();
    }

    private void handleMouseScroll(javafx.scene.input.ScrollEvent event) {
        float zoomAmount = (float) (event.getDeltaY() * 0.05f);
        camera.zoom(zoomAmount);
        if (onRenderRequest != null) onRenderRequest.run();
        event.consume();
    }

    @Override
    public void setMouseSensitivity(float sensitivity) { 
        this.mouseSensitivity = sensitivity; 
    }
    
    @Override
    public void setMoveSpeed(float speed) { 
        this.moveSpeed = speed; 
    }
}