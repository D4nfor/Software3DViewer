package com.cgvsu.manager.implementations;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.manager.interfaces.RendererImpl;
import com.cgvsu.render_engine.Transform;
import javafx.scene.canvas.GraphicsContext;

public class DefaultRenderer implements RendererImpl {
    @Override
    public void render(GraphicsContext gc, Camera camera, Model model, int width, int height, Transform transform) {
        camera.setAspectRatio((float) width / height);
        if (model != null) {
            RenderEngine.render(gc, camera, model, width, height, transform);
        }
    }
    
    @Override
    public Model applyTransform(Model model, Transform transform) {
        return RenderEngine.applyTransform(model, transform);
    }
}