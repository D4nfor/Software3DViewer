package com.cgvsu.manager;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.rendering.RendererImpl;
import com.cgvsu.utils.math.Vector3f;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;

public class SceneManager {

    private final Camera camera;
    private final RendererImpl renderer;

    private final ObservableList<Model> models = FXCollections.observableArrayList();
    private final ObjectProperty<Model> activeModel = new SimpleObjectProperty<>();

    public SceneManager(RendererImpl renderer, Camera camera) {
        this.renderer = renderer;
        this.camera = camera;
    }

    public SceneManager(RendererImpl renderer) {
        this(renderer, new Camera(
                new Vector3f(0, 0, 50),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100
        ));
    }

    public void render(GraphicsContext gc, double width, double height) {
        Model model = activeModel.get();
        if (model != null) {
            renderer.render(gc, camera, model, (int) width, (int) height, model.getTransform());
        }
    }


    public Camera getCamera() {
        return camera;
    }

    public RendererImpl getRenderer() {
        return renderer;
    }

    public ObservableList<Model> getModels() {
        return models;
    }

    public void addModel(Model model) {
        models.add(model);
        if (activeModel.get() == null) {
            setActiveModel(model);
        }
    }

    public void removeModel(Model model) {
        models.remove(model);
        if (model == activeModel.get()) {
            activeModel.set(models.isEmpty() ? null : models.get(0));
        }
    }

    public Model getActiveModel() {
        return activeModel.get();
    }

    public void setActiveModel(Model model) {
        activeModel.set(model);
    }

    public ObjectProperty<Model> activeModelProperty() {
        return activeModel;
    }

    public Model getTransformedModel() {
        Model model = getActiveModel();
        return model != null
                ? renderer.applyTransform(model, model.getTransform())
                : null;
    }
}
