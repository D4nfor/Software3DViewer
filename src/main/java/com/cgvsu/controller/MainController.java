package com.cgvsu.controller;

import com.cgvsu.manager.*;
import com.cgvsu.manager.implementations.DefaultInputManager;
import com.cgvsu.manager.implementations.ObjFileManager;
import com.cgvsu.manager.interfaces.InputManagerImpl;
import com.cgvsu.manager.interfaces.FileManagerImpl;
import com.cgvsu.render_engine.rendering.RendererImpl;
import com.cgvsu.render_engine.rendering.Renderer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML private BorderPane borderPane;

    private final SceneManager sceneManager;
    private final AnimationManager animationManager;
    private final UIManager uiManager;
    private final FileManagerImpl modelManager;
    private final InputManagerImpl inputManager;

    private ViewportController viewportController;
    private ToolController toolController;
    private MenuController menuController;

    private RendererImpl renderer;

    public MainController() {
        this.renderer = new Renderer();
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
        ControllerFactory factory = new ControllerFactory(
                sceneManager, animationManager, uiManager, modelManager, inputManager, this
        );

        // Viewport
        FXMLLoader viewportLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/ViewportPane.fxml"));
        viewportLoader.setControllerFactory(factory);
        Parent viewportNode = viewportLoader.load();
        borderPane.setCenter(viewportNode);
        this.viewportController = viewportLoader.getController();

        // Tool Panel
        FXMLLoader toolLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/ToolPanel.fxml"));
        toolLoader.setControllerFactory(factory);
        Parent toolNode = toolLoader.load();
        addStylesToNode(toolNode);
        borderPane.setRight(toolNode);
        this.toolController = toolLoader.getController();

        // Menu
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/com/cgvsu/fxml/MenuBar.fxml"));
        menuLoader.setControllerFactory(factory);
        Parent menuNode = menuLoader.load();
        addStylesToNode(menuNode);
        borderPane.setTop(menuNode);
        this.menuController = menuLoader.getController();
    }

    private void addStylesToNode(Parent node) {
        Scene scene = borderPane.getScene();
        if (scene != null) {
            for (String stylesheet : scene.getStylesheets()) {
                if (!node.getStylesheets().contains(stylesheet)) {
                    node.getStylesheets().add(stylesheet);
                }
            }
        }
    }

    public void requestRender() {
        renderFrame();
    }

    private void renderFrame() {
        if (viewportController != null) {
            viewportController.renderFrame();
        }
    }

    public void cleanup() {
        if (animationManager != null) {
            animationManager.stop();
        }
    }
}