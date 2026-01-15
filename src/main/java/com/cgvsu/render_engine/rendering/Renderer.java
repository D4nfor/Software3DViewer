package com.cgvsu.render_engine.rendering;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.Rasterizer;
import com.cgvsu.render_engine.ZBuffer;
import com.cgvsu.render_engine.transform.Transform;
import com.cgvsu.utils.math.Vector3f;
import com.cgvsu.utils.math.Vertex;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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
            Transform transform
    ) {
        if (model == null) {
            return;
        }

        WritableImage frame = new WritableImage(width, height);
        PixelWriter pw = frame.getPixelWriter();
        ZBuffer zBuffer = new ZBuffer(width, height);

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

        // ===== источник света — камера =====
        Vector3f lightPos = camera.getPosition();

        for (var polygon : model.getPolygons()) {

            ArrayList<Vertex> verts =
                    projectPolygon(model, polygon, mvp, width, height);

            if (verts.size() < 3) {
                continue;
            }

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
