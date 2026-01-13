package com.cgvsu.render_engine.rendering;

import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.Transform;
import com.cgvsu.utils.math.Vector3f;
import com.cgvsu.utils.math.Point2f;
import com.cgvsu.utils.math.Matrix4f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import javafx.scene.canvas.GraphicsContext;
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
            ArrayList<Vector3f> projected = projectPolygon(model, polygon, mvp, width, height);
            triangulateAndRasterize(projected, gc, zBuffer, width, height);
        }
    }

    private ArrayList<Vector3f> projectPolygon(Model model, Polygon polygon,
                                               Matrix4f mvp, int width, int height) {

        ArrayList<Vector3f> projected = new ArrayList<>();

        for (int index : polygon.getVertexIndices()) {
            Vector3f v = model.getVertices().get(index);
            Vector3f clip = multiplyMatrix4ByVector3(mvp, v);
            Point2f screen = vertexToPoint(clip, width, height);

            projected.add(new Vector3f(screen.getX(), screen.getY(), clip.getZ()));
        }

        return projected;
    }

    private void triangulateAndRasterize(ArrayList<Vector3f> poly,
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

    private void renderModel(GraphicsContext graphicsContext, Model mesh,
                             Matrix4f modelViewProjectionMatrix, int width, int height) {

        float[][] zBuffer = new float[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                zBuffer[x][y] = Float.POSITIVE_INFINITY;

        final int nPolygons = mesh.getPolygons().size();

        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            Polygon polygon = mesh.getPolygons().get(polygonInd);
            final int nVerticesInPolygon = polygon.getVertexIndices().size();

            if (nVerticesInPolygon < 2) continue;

            ArrayList<Vector3f> projected = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                int vertexIndex = polygon.getVertexIndices().get(vertexInPolygonInd);
                if (vertexIndex < 0 || vertexIndex >= mesh.getVertices().size()) {
                    continue;
                }

                Vector3f vertex = mesh.getVertices().get(vertexIndex);

                Vector3f transformedVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertex);

                Point2f screen = vertexToPoint(transformedVertex, width, height);

// x,y = screen coords, z = depth
                projected.add(new Vector3f(
                        screen.getX(),
                        screen.getY(),
                        transformedVertex.getZ()
                ));

            }

            if (projected.size() < 3) continue;

            for (int i = 1; i < projected.size() - 1; i++) {
                Vector3f v0 = projected.get(0);
                Vector3f v1 = projected.get(i);
                Vector3f v2 = projected.get(i + 1);

                rasterizeTriangle(v0, v1, v2, graphicsContext, zBuffer, width, height);
            }
        }
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

        transformedModel.setTextureVertices(new ArrayList<>(originalModel.getTextureVertices()));
        transformedModel.setNormals(new ArrayList<>(originalModel.getNormals()));
        transformedModel.setPolygons(new ArrayList<>(originalModel.getPolygons()));

        return transformedModel;
    }

    private Matrix4f createModelMatrix(Transform transform) {
        return GraphicConveyor.createModelMatrix(
                transform.scaleX, transform.scaleY, transform.scaleZ,
                transform.rotateX, transform.rotateY, transform.rotateZ,
                transform.translateX, transform.translateY, transform.translateZ
        );
    }

    private void rasterizeTriangle(Vector3f v0, Vector3f v1, Vector3f v2,
                                   GraphicsContext gc, float[][] zBuffer,
                                   int width, int height) {

        int minX = (int) Math.max(0, Math.floor(Math.min(v0.getX(), Math.min(v1.getX(), v2.getX()))));
        int maxX = (int) Math.min(width - 1, Math.ceil(Math.max(v0.getX(), Math.max(v1.getX(), v2.getX()))));
        int minY = (int) Math.max(0, Math.floor(Math.min(v0.getY(), Math.min(v1.getY(), v2.getY()))));
        int maxY = (int) Math.min(height - 1, Math.ceil(Math.max(v0.getY(), Math.max(v1.getY(), v2.getY()))));

        float area = edge(v0, v1, v2);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                Vector3f p = new Vector3f(x + 0.5f, y + 0.5f, 0);

                float w0 = edge(v1, v2, p);
                float w1 = edge(v2, v0, p);
                float w2 = edge(v0, v1, p);

                if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                    w0 /= area; w1 /= area; w2 /= area;

                    float z = w0 * v0.getZ() + w1 * v1.getZ() + w2 * v2.getZ();

                    if (z < zBuffer[x][y]) {
                        zBuffer[x][y] = z;
                        gc.getPixelWriter().setColor(x, y, FILL_COLOR);
                    }
                }
            }
        }
    }


    private float edge(Vector3f a, Vector3f b, Vector3f c) {
        return (c.getX() - a.getX()) * (b.getY() - a.getY())
                - (c.getY() - a.getY()) * (b.getX() - a.getX());
    }
}