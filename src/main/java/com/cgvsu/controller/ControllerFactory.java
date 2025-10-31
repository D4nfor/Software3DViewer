package com.cgvsu.controller;

import com.cgvsu.manager.*;
import com.cgvsu.manager.interfaces.InputManagerImpl;
import com.cgvsu.manager.interfaces.FileManagerImpl;

public class ControllerFactory implements javafx.util.Callback<Class<?>, Object> {
    private final SceneManager sceneManager;
    private final AnimationManager animationManager;
    private final UIManager uiManager;
    private final FileManagerImpl modelManager;
    private final InputManagerImpl inputManager;
    private final MainController mainController;

    public ControllerFactory(SceneManager sceneManager, AnimationManager animationManager,
                             UIManager uiManager, FileManagerImpl modelManager,
                             InputManagerImpl inputSystem, MainController mainController) {
        this.sceneManager = sceneManager;
        this.animationManager = animationManager;
        this.uiManager = uiManager;
        this.modelManager = modelManager;
        this.inputManager = inputSystem;
        this.mainController = mainController;
    }

    @Override
    public Object call(Class<?> type) {
        if (type == ViewportController.class) {
            return new ViewportController(sceneManager, animationManager, inputManager, mainController);
        } else if (type == TransformController.class) {
            return new TransformController(sceneManager, uiManager);
        } else if (type == MenuController.class) {
            return new MenuController(modelManager, sceneManager, mainController);
        }
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create controller: " + type.getName(), e);
        }
    }
}