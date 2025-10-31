package com.cgvsu.manager.interfaces;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.Transform;
import javafx.scene.canvas.GraphicsContext;

public interface RendererImpl {
    void render(GraphicsContext gc, Camera camera, Model model, int width, int height, Transform transform);
    Model applyTransform(Model model, Transform transform);
}