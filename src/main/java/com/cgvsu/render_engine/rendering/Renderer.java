package com.cgvsu.render_engine.rendering;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.Rasterizer;
import com.cgvsu.render_engine.ZBuffer;
import com.cgvsu.render_engine.transform.Transform;
import com.cgvsu.utils.math.Matrix4f;
import com.cgvsu.utils.math.Vector3f;
import com.cgvsu.utils.math.Vertex;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static com.cgvsu.render_engine.VertexProjector.projectPolygon;

public class Renderer implements RendererImpl {

    @Override
    public void render(
            GraphicsContext gc,
            Camera camera,
            Model model,
            int width,
            int height,
            Transform transform,
            RenderSettings settings
    ) {
        if (model == null) {
            return;
        }

        // ===== Model-View-Projection =====
        var modelMatrix =
                com.cgvsu.render_engine.GraphicConveyor.createModelMatrix(
                        transform.scaleX, transform.scaleY, transform.scaleZ,
                        transform.rotateX, transform.rotateY, transform.rotateZ,
                        transform.translateX, transform.translateY, transform.translateZ
                );

        var mvp = camera.getProjectionMatrix()
                .multiply(camera.getViewMatrix())
                .multiply(modelMatrix);

        Vector3f lightPos = camera.getPosition();

        boolean fastMode = !settings.isUseTexture() && !settings.isUseLighting();

        if (fastMode) {
            drawFast(gc, model, mvp, width, height, settings.getBaseColor());
        } else {
            drawWithRasterizer(gc, model, mvp, width, height, lightPos);
        }

        if (settings.isWireframe()) {
            drawWireframe(gc, model, mvp, width, height);
        }

    }

    private void drawFast(GraphicsContext gc,
                          Model model,
                          Matrix4f mvp,
                          int width,
                          int height,
                          Color baseColor)
    {

        gc.clearRect(0, 0, width, height);
        gc.setFill(baseColor);

        for (var polygon : model.getPolygons()) {
            ArrayList<Vertex> verts =
                    projectPolygon(model, polygon, mvp, width, height);

            if (verts.size() < 3) continue;

            double[] x = new double[verts.size()];
            double[] y = new double[verts.size()];

            for (int i = 0; i < verts.size(); i++) {
                x[i] = verts.get(i).x;
                y[i] = verts.get(i).y;
            }

            gc.fillPolygon(x, y, verts.size());
        }
    }

    private void drawWithRasterizer(GraphicsContext gc,
                                    Model model,
                                    Matrix4f mvp,
                                    int width,
                                    int height,
                                    Vector3f lightPos)
    {

        WritableImage frame = new WritableImage(width, height);
        PixelWriter pw = frame.getPixelWriter();
        ZBuffer zBuffer = new ZBuffer(width, height);

        for (var polygon : model.getPolygons()) {

            ArrayList<Vertex> verts =
                    projectPolygon(model, polygon, mvp, width, height);

            if (verts.size() < 3) continue;

            Rasterizer.triangulateAndRasterize(
                    verts,
                    zBuffer,
                    pw,
                    model.getTexture(),
                    lightPos
            );
        }

        gc.drawImage(frame, 0, 0);
    }

    private void drawWireframe(GraphicsContext gc,
                               Model model,
                               Matrix4f mvp,
                               int width,
                               int height)
    {

        gc.setStroke(Color.BLACK);

        for (var polygon : model.getPolygons()) {
            ArrayList<Vertex> verts =
                    projectPolygon(model, polygon, mvp, width, height);

            if (verts.size() < 3) continue;

            for (int i = 0; i < verts.size(); i++) {
                Vertex v1 = verts.get(i);
                Vertex v2 = verts.get((i + 1) % verts.size());

                gc.strokeLine(v1.x, v1.y, v2.x, v2.y);
            }
        }
    }

    @Override
    public Model applyTransform(Model model, Transform transform) {
        if (model == null) {
            return null;
        }

        Model transformedModel = new Model(model);

        for (int i = 0; i < transformedModel.getVertices().size(); i++) {
            Vector3f v = transformedModel.getVertices().get(i);

            Vector3f scaled = new Vector3f(
                    v.getX() * transform.scaleX,
                    v.getY() * transform.scaleY,
                    v.getZ() * transform.scaleZ
            );

            Vector3f rotated = transform.rotate(scaled);

            Vector3f translated = new Vector3f(
                    rotated.getX() + transform.translateX,
                    rotated.getY() + transform.translateY,
                    rotated.getZ() + transform.translateZ
            );

            transformedModel.getVertices().set(i, translated);
        }

        return transformedModel;
    }
}
