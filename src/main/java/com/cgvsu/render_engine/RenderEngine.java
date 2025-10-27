package com.cgvsu.render_engine;

import java.util.ArrayList;

import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Point2f;
import com.cgvsu.math.Matrix4f;
import javafx.scene.canvas.GraphicsContext;
import com.cgvsu.model.Model;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {
    // Старый метод для обратной совместимости
    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) {

        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);

        renderModel(graphicsContext, mesh, modelViewProjectionMatrix, width, height);
    }

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Transform transform)
    {
        // Матрицы преобразования
        Matrix4f modelMatrix = createModelMatrix(transform);
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        // Правильный порядок для векторов-столбцов
        // MVP = projection * view * model
        Matrix4f modelViewProjectionMatrix = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);

        renderModel(graphicsContext, mesh, modelViewProjectionMatrix, width, height);
    }

    private static void renderModel(
            final GraphicsContext graphicsContext,
            final Model mesh,
            final Matrix4f modelViewProjectionMatrix,
            final int width,
            final int height) {

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                // Берём вершину
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd)
                        .getVertexIndices().get(vertexInPolygonInd));

                // Преобразуем вершину через MVP
                Vector3f transformedVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertex);

                // Переводим в координаты экрана
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

    private static Matrix4f createModelMatrix(Transform transform) {
        return GraphicConveyor.createModelMatrix(
                transform.getScaleX(), transform.getScaleY(), transform.getScaleZ(),
                transform.getRotateX(), transform.getRotateY(), transform.getRotateZ(),
                transform.getTranslateX(), transform.getTranslateY(), transform.getTranslateZ()
        );
    }
}