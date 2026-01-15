package com.cgvsu.render_engine.rendering;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.Transform;
import com.cgvsu.render_engine.TextureStorage;
import com.cgvsu.utils.math.*;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public final class WireframeRenderer implements RendererImpl {

    private static final Color FILL_COLOR = Color.LIGHTGRAY;

    @Override
    public void render(GraphicsContext gc, Camera camera, Model model,
                       int width, int height, Transform transform) {

        if (model == null) return;

        Matrix4f mvp = camera.getProjectionMatrix()
                .multiply(camera.getViewMatrix())
                .multiply(createModelMatrix(transform));

        float[][] zBuffer = createZBuffer(width, height);

        renderModel(gc, model, mvp, zBuffer, width, height);
    }

    // =========================================================

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
            ArrayList<Vertex> verts =
                    projectPolygon(model, polygon, mvp, width, height);

            triangulateAndRasterize(verts, gc, zBuffer, width, height);
        }
    }

    // =========================================================
    // ПРОЕКЦИЯ (ВАЖНО: используем Vector4f и W)

    private ArrayList<Vertex> projectPolygon(Model model,
                                             Polygon polygon,
                                             Matrix4f mvp,
                                             int width, int height) {

        ArrayList<Vertex> result = new ArrayList<>();

        for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
            int vi = polygon.getVertexIndices().get(i);
            Vector3f v = model.getVertices().get(vi);

            Vector4f clip = multiplyMatrix4ByVector4(
                    mvp,
                    new Vector4f(v.getX(), v.getY(), v.getZ(), 1.0f)
            );

            if (clip.getW() == 0) continue;

            float invW = 1.0f / clip.getW();

            float ndcX = clip.getX() * invW;
            float ndcY = clip.getY() * invW;
            float ndcZ = clip.getZ() * invW;

            float screenX = (ndcX + 1f) * 0.5f * width;
            float screenY = (1f - ndcY) * 0.5f * height;

            float u = 0, vTex = 0;
            if (!polygon.getTextureVertexIndices().isEmpty()) {
                int ti = polygon.getTextureVertexIndices().get(i);
                if (ti >= 0 && ti < model.getTextureVertices().size()) {
                    u = model.getTextureVertices().get(ti).getX();
                    vTex = model.getTextureVertices().get(ti).getY();
                }
            }

            result.add(new Vertex(
                    screenX,
                    screenY,
                    ndcZ,
                    invW,
                    u,
                    vTex
            ));
        }

        return result;
    }

    // =========================================================

    private void triangulateAndRasterize(ArrayList<Vertex> poly,
                                         GraphicsContext gc,
                                         float[][] zBuffer,
                                         int width, int height) {

        if (poly.size() < 3) return;

        for (int i = 1; i < poly.size() - 1; i++) {
            rasterizeTriangle(
                    poly.get(0),
                    poly.get(i),
                    poly.get(i + 1),
                    gc, zBuffer, width, height
            );
        }
    }

    // =========================================================
    // РАСТЕРИЗАЦИЯ

    private void rasterizeTriangle(Vertex v0, Vertex v1, Vertex v2,
                                   GraphicsContext gc,
                                   float[][] zBuffer,
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
        if (area == 0) return;

        Image tex = TextureStorage.getTexture();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {

                Vertex p = new Vertex(x + 0.5f, y + 0.5f, 0, 0, 0, 0);

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
                            float invW =
                                    w0 * v0.invW +
                                            w1 * v1.invW +
                                            w2 * v2.invW;

                            float u =
                                    (w0 * v0.u * v0.invW +
                                            w1 * v1.u * v1.invW +
                                            w2 * v2.u * v2.invW) / invW;

                            float v =
                                    (w0 * v0.v * v0.invW +
                                            w1 * v1.v * v1.invW +
                                            w2 * v2.v * v2.invW) / invW;

                            int tx = (int) Math.clamp(
                                    u * (tex.getWidth() - 1),
                                    0, tex.getWidth() - 1);

                            int ty = (int) Math.clamp(
                                    (1 - v) * (tex.getHeight() - 1),
                                    0, tex.getHeight() - 1);

                            gc.getPixelWriter().setColor(
                                    x, y,
                                    tex.getPixelReader().getColor(tx, ty)
                            );
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

    // =========================================================

    @Override
    public Model applyTransform(Model originalModel, Transform transform) {
        if (originalModel == null || transform == null) return null;

        Matrix4f modelMatrix = createModelMatrix(transform);
        Model transformed = new Model();

        ArrayList<Vector3f> verts = new ArrayList<>();
        for (Vector3f v : originalModel.getVertices()) {
            verts.add(multiplyMatrix4ByVector3(modelMatrix, v));
        }

        transformed.setVertices(verts);
        transformed.setTextureVertices(new ArrayList<>(originalModel.getTextureVertices()));
        transformed.setNormals(new ArrayList<>(originalModel.getNormals()));
        transformed.setPolygons(new ArrayList<>(originalModel.getPolygons()));

        return transformed;
    }

    private Matrix4f createModelMatrix(Transform t) {
        return GraphicConveyor.createModelMatrix(
                t.scaleX, t.scaleY, t.scaleZ,
                t.rotateX, t.rotateY, t.rotateZ,
                t.translateX, t.translateY, t.translateZ
        );
    }

    // =========================================================

    private static class Vertex {
        float x, y;
        float z;      // ndcZ
        float invW;   // 1 / w
        float u, v;

        Vertex(float x, float y, float z, float invW, float u, float v) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.invW = invW;
            this.u = u;
            this.v = v;
        }
    }
}
