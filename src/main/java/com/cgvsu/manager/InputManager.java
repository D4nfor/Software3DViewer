package com.cgvsu.manager;

import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.Camera;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

// все бинды
public class InputManager {
    private final Camera camera;

    // Состояние мыши
    private boolean middleMousePressed = false;
    private double lastMouseX, lastMouseY;

    // Настройки чувствительности
    private float mouseSensitivity = 0.3f;
    private float moveSpeed = 0.5f;

    // Делегаты горячих клавиш
    private Runnable onOpenModel;
    private Runnable onSaveModel;
    private Runnable onShowTransformPanel;
    private Runnable onHideTransformPanel;
    private Runnable onRenderRequest;

    public InputManager(Camera camera) {
        this.camera = camera;
    }

    public void setHotkeyHandlers(Runnable onOpenModel,
                                  Runnable onSaveModel,
                                  Runnable onShowTransformPanel,
                                  Runnable onHideTransformPanel) {
        this.onOpenModel = onOpenModel;
        this.onSaveModel = onSaveModel;
        this.onShowTransformPanel = onShowTransformPanel;
        this.onHideTransformPanel = onHideTransformPanel;
    }

    public void setupKeyboardHandlers(Node targetNode, Runnable onRenderRequest) {
        this.onRenderRequest = onRenderRequest;

        targetNode.setFocusTraversable(true);
        targetNode.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case O -> { if (onOpenModel != null) onOpenModel.run(); event.consume(); return; }
                    case S -> { if (onSaveModel != null) onSaveModel.run(); event.consume(); return; }
                    case T -> { if (onShowTransformPanel != null) onShowTransformPanel.run(); event.consume(); return; }
                    case G -> { if (onHideTransformPanel != null) onHideTransformPanel.run(); event.consume(); return; }
                }
            }

            switch (event.getCode()) {
                case W -> camera.moveForward(moveSpeed);
                case S -> camera.moveBackward(moveSpeed);
                case A -> camera.moveLeft(moveSpeed);
                case D -> camera.moveRight(moveSpeed);
                case SPACE -> camera.moveUp(moveSpeed);
                case SHIFT -> camera.moveDown(moveSpeed);
                case R -> resetCamera();
            }

            if (onRenderRequest != null) onRenderRequest.run();
        });
    }

    private void resetCamera() {
        camera.reset(new Vector3f(0, 0, 50), new Vector3f(0, 0, 0));
    }

    public void setupMouseHandlers(Node targetNode) {
        targetNode.setOnMousePressed(this::handleMousePressed);
        targetNode.setOnMouseReleased(this::handleMouseReleased);
        targetNode.setOnMouseDragged(this::handleMouseDragged);
        targetNode.setOnScroll(this::handleMouseScroll);
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

    public void setMouseSensitivity(float sensitivity) { this.mouseSensitivity = sensitivity; }
    public void setMoveSpeed(float speed) { this.moveSpeed = speed; }
    public float getMouseSensitivity() { return mouseSensitivity; }
    public float getMoveSpeed() { return moveSpeed; }
}
