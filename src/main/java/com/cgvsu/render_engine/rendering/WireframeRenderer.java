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

import java.util.ArrayList;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public final class WireframeRenderer implements RendererImpl {

    @Override
    public void render(GraphicsContext graphicsContext, Camera camera, Model model,
                       int width, int height, Transform transform) {
        if (model == null) return;

        Matrix4f modelMatrix = createModelMatrix(transform);
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);

        renderModel(graphicsContext, model, modelViewProjectionMatrix, width, height);
    }

    private void renderModel(GraphicsContext graphicsContext, Model mesh,
                             Matrix4f modelViewProjectionMatrix, int width, int height) {

        final int nPolygons = mesh.getPolygons().size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            Polygon polygon = mesh.getPolygons().get(polygonInd);
            final int nVerticesInPolygon = polygon.getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                int vertexIndex = polygon.getVertexIndices().get(vertexInPolygonInd);
                Vector3f vertex = mesh.getVertices().get(vertexIndex);

                Vector3f transformedVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertex);

                Point2f resultPoint = vertexToPoint(transformedVertex, width, height);
                resultPoints.add(resultPoint);
            }

            // Рисуем рёбра полигона
            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Point2f p0 = resultPoints.get(vertexInPolygonInd - 1);
                Point2f p1 = resultPoints.get(vertexInPolygonInd);
                graphicsContext.strokeLine(p0.getX(), p0.getY(), p1.getX(), p1.getY());
            }

            // Замыкаем контур полигона
            if (nVerticesInPolygon > 0) {
                Point2f first = resultPoints.get(0);
                Point2f last = resultPoints.get(nVerticesInPolygon - 1);
                graphicsContext.strokeLine(last.getX(), last.getY(), first.getX(), first.getY());
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
}