package com.cgvsu.render_engine.rendering;

import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.Transform;
import com.cgvsu.render_engine.TextureStorage;
import com.cgvsu.utils.math.Vector3f;
import com.cgvsu.utils.math.Point2f;
import com.cgvsu.utils.math.Matrix4f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public final class WireframeRenderer implements RendererImpl {

    private static final Color FILL_COLOR = Color.LIGHTGRAY;
    private static final double WIREFRAME_LINE_WIDTH = 0.75;

    @Override
    public void render(GraphicsContext gc, Camera camera, Model model,
                       int width, int height, Transform transform) {

        if (model == null) return;

        Matrix4f mvp = camera.getProjectionMatrix()
                .multiply(camera.getViewMatrix())
                .multiply(createModelMatrix(transform));

        float[][] zBuffer = createZBuffer(width, height);

        setupGraphicsContext(gc);
        renderModel(gc, model, mvp, zBuffer, width, height);
    }

    private float[][] createZBuffer(int width, int height) {
        float[][] buffer = new float[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                buffer[x][y] = Float.POSITIVE_INFINITY;
        return buffer;
    }

    private void renderModel(GraphicsContext gc, Model model,
                             Matrix4f mvp, float[][] zBuffer,
                             int width, int height) {

        for (Polygon polygon : model.getPolygons()) {
            ArrayList<Vertex> projected =
                    projectPolygon(model, polygon, mvp, width, height);

            triangulateAndRasterize(projected, gc, zBuffer, width, height);
        }
    }

    private ArrayList<Vertex> projectPolygon(Model model, Polygon polygon,
                                             Matrix4f mvp, int width, int height) {
        ArrayList<Vertex> projected = new ArrayList<>();

        for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
            int vi = polygon.getVertexIndices().get(i);
            int ti = polygon.getTextureVertexIndices().isEmpty()
                    ? -1
                    : polygon.getTextureVertexIndices().get(i);

            Vector3f v = model.getVertices().get(vi);
            Vector3f clip = multiplyMatrix4ByVector3(mvp, v);
            Point2f screen = vertexToPoint(clip, width, height);

            float u = 0, vTex = 0;
            if (ti >= 0 && ti < model.getTextureVertices().size()) {
                u = model.getTextureVertices().get(ti).getX();
                vTex = model.getTextureVertices().get(ti).getY();
            }

            projected.add(new Vertex(
                    screen.getX(),
                    screen.getY(),
                    clip.getZ(),
                    u, vTex
            ));
        }
        return projected;
    }

    private void triangulateAndRasterize(ArrayList<Vertex> poly,
                                         GraphicsContext gc,
                                         float[][] zBuffer,
                                         int width, int height) {

        if (poly.size() < 3) return;

        for (int i = 1; i < poly.size() - 1; i++) {
            rasterizeTriangle(poly.get(0), poly.get(i), poly.get(i + 1),
                    gc, zBuffer, width, height);
        }
    }

    private void setupGraphicsContext(GraphicsContext gc) {
        gc.setImageSmoothing(true);
        gc.setStroke(FILL_COLOR);
        gc.setLineWidth(WIREFRAME_LINE_WIDTH);
        gc.setLineDashes(null);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
    }

    private void rasterizeTriangle(Vertex v0, Vertex v1, Vertex v2,
                                   GraphicsContext gc, float[][] zBuffer,
                                   int width, int height) {

        int minX = (int) Math.max(0,
                Math.floor(Math.min(v0.x, Math.min(v1.x, v2.x))));
        int maxX = (int) Math.min(width - 1,
                Math.ceil(Math.max(v0.x, Math.max(v1.x, v2.x))));
        int minY = (int) Math.max(0,
                Math.floor(Math.min(v0.y, Math.min(v1.y, v2.y))));
        int maxY = (int) Math.min(height - 1,
                Math.ceil(Math.max(v0.y, Math.max(v1.y, v2.y))));

        float area = edge(v0, v1, v2);

        Image tex = TextureStorage.getTexture();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                Vertex p = new Vertex(x + 0.5f, y + 0.5f, 0, 0, 0);

                float w0 = edge(v1, v2, p);
                float w1 = edge(v2, v0, p);
                float w2 = edge(v0, v1, p);

                if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                    w0 /= area;
                    w1 /= area;
                    w2 /= area;

                    float z = w0 * v0.z + w1 * v1.z + w2 * v2.z;

                    if (z < zBuffer[x][y]) {
                        zBuffer[x][y] = z;

                        if (tex != null) {
                            float u = w0 * v0.u + w1 * v1.u + w2 * v2.u;
                            float v = w0 * v0.v + w1 * v1.v + w2 * v2.v;

                            int tx = (int) Math.clamp(u * (tex.getWidth() - 1), 0, tex.getWidth() - 1);
                            int ty = (int) Math.clamp((1 - v) * (tex.getHeight() - 1), 0, tex.getHeight() - 1);

                            Color color = tex.getPixelReader().getColor(tx, ty);
                            gc.getPixelWriter().setColor(x, y, color);
                        } else {
                            gc.getPixelWriter().setColor(x, y, FILL_COLOR);
                        }
                    }
                }
            }
        }
    }

    private float edge(Vertex a, Vertex b, Vertex c) {
        return (c.x - a.x) * (b.y - a.y)
                - (c.y - a.y) * (b.x - a.x);
    }

    @Override
    public Model applyTransform(Model originalModel, Transform transform) {
        if (originalModel == null || transform == null) {
            return null;
        }

        Matrix4f modelMatrix = createModelMatrix(transform);
        Model transformedModel = new Model();

        ArrayList<Vector3f> newVertices = new ArrayList<>();
        for (Vector3f vertex : originalModel.getVertices()) {
            newVertices.add(multiplyMatrix4ByVector3(modelMatrix, vertex));
        }
        transformedModel.setVertices(newVertices);

        transformedModel.setTextureVertices(
                new ArrayList<>(originalModel.getTextureVertices()));
        transformedModel.setNormals(
                new ArrayList<>(originalModel.getNormals()));
        transformedModel.setPolygons(
                new ArrayList<>(originalModel.getPolygons()));

        return transformedModel;
    }

    private Matrix4f createModelMatrix(Transform transform) {
        return GraphicConveyor.createModelMatrix(
                transform.scaleX, transform.scaleY, transform.scaleZ,
                transform.rotateX, transform.rotateY, transform.rotateZ,
                transform.translateX, transform.translateY, transform.translateZ
        );
    }

    private static class Vertex {
        float x, y, z;
        float u, v;

        Vertex(float x, float y, float z, float u, float v) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
        }
    }
}
