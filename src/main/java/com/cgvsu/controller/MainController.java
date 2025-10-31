package com.cgvsu.controller;

import com.cgvsu.manager.*;
import com.cgvsu.manager.implementations.DefaultInputManager;
import com.cgvsu.manager.implementations.ObjFileManager;
import com.cgvsu.manager.interfaces.InputManagerImpl;
import com.cgvsu.manager.interfaces.FileManagerImpl;
import com.cgvsu.render_engine.rendering.RendererImpl;
import com.cgvsu.render_engine.rendering.WireframeRenderer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML private BorderPane borderPane;
    
    private final SceneManager sceneManager;
    private final AnimationManager animationManager;
    private final UIManager uiManager;
    private final FileManagerImpl modelManager;
    private final InputManagerImpl inputManager;
    
    // Дочерние контроллеры
    private ViewportController viewportController;
    private TransformController transformController;
    private MenuController menuController;

    public MainController() {
        RendererImpl renderer = new WireframeRenderer();
        this.sceneManager = new SceneManager(renderer);
        this.animationManager = new AnimationManager(this::renderFrame);
        this.uiManager = new UIManager();
        this.modelManager = new ObjFileManager();
        this.inputManager = new DefaultInputManager(sceneManager.getCamera());
    }

    @FXML
    private void initialize() {
        try {
            setupChildControllers();
            animationManager.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize main controller", e);
        }
    }

    private void setupChildControllers() throws IOException {
        // Фабрика для передачи зависимостей дочерним контроллерам
        ControllerFactory factory = new ControllerFactory(
            sceneManager, animationManager, uiManager, modelManager, inputManager, this
        );

        // Загружаем Viewport
        FXMLLoader viewportLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/ViewportPane.fxml"));
        viewportLoader.setControllerFactory(factory);
        borderPane.setCenter(viewportLoader.load());
        this.viewportController = viewportLoader.getController();

        // Загружаем Transform Panel
        FXMLLoader transformLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/TransformPanel.fxml"));
        transformLoader.setControllerFactory(factory);
        borderPane.setRight(transformLoader.load());
        this.transformController = transformLoader.getController();

        // Загружаем Menu Bar
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/MenuBar.fxml"));
        menuLoader.setControllerFactory(factory);
        borderPane.setTop(menuLoader.load());
        this.menuController = menuLoader.getController();
        this.menuController.setTransformController(transformController);
    }

    public void requestRender() {
        renderFrame();
    }

    private void renderFrame() {
        viewportController.renderFrame();
    }

    public void cleanup() {
        animationManager.stop();
    }
}