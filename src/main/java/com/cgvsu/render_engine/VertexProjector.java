package com.cgvsu.render_engine;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.utils.math.Matrix4f;
import com.cgvsu.utils.math.Vector3f;
import com.cgvsu.utils.math.Vector4f;
import com.cgvsu.utils.math.Vertex;

import java.util.ArrayList;

import static com.cgvsu.render_engine.GraphicConveyor.multiplyMatrix4ByVector4;

public class VertexProjector {

    public static ArrayList<Vertex> projectPolygon(
            Model model,
            Polygon polygon,
            Matrix4f mvp,
            int width,
            int height
    ) {
        ArrayList<Vertex> result = new ArrayList<>();

        for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
            int vi = polygon.getVertexIndices().get(i);

            // мировая позиция вершины
            Vector3f worldPos = model.getVertices().get(vi);

            // нормаль вершины (уже усреднённая)
            Vector3f normal = model.getNormals().get(vi).normalize();

            // проекция в clip space
            Vector4f clip = multiplyMatrix4ByVector4(
                    mvp,
                    new Vector4f(
                            worldPos.getX(),
                            worldPos.getY(),
                            worldPos.getZ(),
                            1.0f
                    )
            );

            if (Math.abs(clip.getW()) < 1e-6f) {
                continue;
            }

            float invW = 1.0f / clip.getW();

            float ndcX = clip.getX() * invW;
            float ndcY = clip.getY() * invW;
            float ndcZ = clip.getZ() * invW;

            // экранные координаты
            float screenX = (ndcX + 1.0f) * 0.5f * width;
            float screenY = (1.0f - ndcY) * 0.5f * height;

            // текстурные координаты (если есть)
            float u = 0.0f;
            float vTex = 0.0f;

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
                    vTex,
                    worldPos,
                    normal
            ));
        }

        return result;
    }
}
