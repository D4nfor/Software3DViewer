package com.cgvsu.manager;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.Transform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;

//вся логика рендеринга, камеры и трансформаций
public class RenderManager {
    private final Camera camera;
    private final ObjectProperty<Model> model = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> transform = new SimpleObjectProperty<>();

    public RenderManager() {
        this.camera = new Camera(
                new Vector3f(0, 0, 50),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100
        );
        this.transform.set(new Transform());
    }

    public void render(GraphicsContext gc, double width, double height) {
        camera.setAspectRatio((float)(width / height));
        Model currentModel = model.get();
        Transform currentTransform = transform.get();

        if (currentModel != null) {
            RenderEngine.render(gc, camera, currentModel, (int)width, (int)height, currentTransform);
        }
    }

    public Camera getCamera() { return camera; }

    public Model getModel() { return model.get(); }
    public void setModel(Model model) { this.model.set(model); }
    public ObjectProperty<Model> modelProperty() { return model; }

    public Transform getTransform() { return transform.get(); }
    public void setTransform(Transform transform) { this.transform.set(transform); }
    public ObjectProperty<Transform> transformProperty() { return transform; }

    public void resetTransform() {
        transform.set(new Transform());
    }
}