package com.cgvsu.manager;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.Transform;
import com.cgvsu.render_engine.GraphicConveyor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

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
    public Model getTransformedModel() {
        Model original = getModel();
        if (original == null) return null;

        Transform t = getTransform();
        Matrix4f modelMatrix = GraphicConveyor.createModelMatrix(
                t.scaleX, t.scaleY, t.scaleZ,
                t.rotateX, t.rotateY, t.rotateZ,
                t.translateX, t.translateY, t.translateZ
        );

        Model transformed = new Model();

        ArrayList<Vector3f> newVertices = new ArrayList<>();
        for (Vector3f v : original.getVertices()) {
            newVertices.add(GraphicConveyor.multiplyMatrix4ByVector3(modelMatrix, v));
        }
        transformed.setVertices(newVertices);

        transformed.setTextureVertices(new ArrayList<>(original.getTextureVertices()));
        transformed.setNormals(new ArrayList<>(original.getNormals()));
        transformed.setPolygons(new ArrayList<>(original.getPolygons()));

        return transformed;
    }
}