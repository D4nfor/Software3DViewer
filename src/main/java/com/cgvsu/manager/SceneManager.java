package com.cgvsu.manager;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.manager.interfaces.RendererImpl;
import com.cgvsu.render_engine.Transform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;

public class SceneManager {
    private final Camera camera;
    private final RendererImpl renderer;
    private final ObjectProperty<Model> model = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> transform = new SimpleObjectProperty<>();

    public SceneManager(RendererImpl renderer, Camera camera) {
        this.renderer = renderer;
        this.camera = camera;
        this.transform.set(new Transform());
    }

    public SceneManager(RendererImpl renderer) {
        this(renderer, new Camera(
            new Vector3f(0, 0, 50),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100
        ));
    }

    public void render(GraphicsContext gc, double width, double height) {
        renderer.render(gc, camera, model.get(), (int)width, (int)height, transform.get());
    }

    public Camera getCamera() { return camera; }
    public RendererImpl getRenderer() { return renderer; }

    public Model getModel() { return model.get(); }
    public void setModel(Model model) { this.model.set(model); }
    public ObjectProperty<Model> modelProperty() { return model; }

    public Transform getTransform() { return transform.get(); }
    public void setTransform(Transform transform) { this.transform.set(transform); }
    public ObjectProperty<Transform> transformProperty() { return transform; }

    public void resetTransform() {
        transform.set(new Transform());
    }

    public Model getTransformedModel() {
        Model original = getModel();
        return original != null ? renderer.applyTransform(original, getTransform()) : null;
    }
}