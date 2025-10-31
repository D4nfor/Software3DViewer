package com.cgvsu.manager.interfaces;

import javafx.scene.Node;

public interface InputSystemImpl {
    void setupKeyboardHandlers(Node targetNode, Runnable onRenderRequest);
    void setupMouseHandlers(Node targetNode);
    void setHotkeyHandlers(Runnable onOpenModel, Runnable onSaveModel, 
                          Runnable onShowTransformPanel, Runnable onHideTransformPanel);
    void setMouseSensitivity(float sensitivity);
    void setMoveSpeed(float speed);
}