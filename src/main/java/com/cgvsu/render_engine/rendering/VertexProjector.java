package com.cgvsu.render_engine.rendering;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.utils.math.Matrix4f;
import com.cgvsu.utils.math.Vector3f;
import com.cgvsu.utils.math.Vector4f;
import com.cgvsu.utils.math.Vertex;

import java.util.ArrayList;

import static com.cgvsu.render_engine.GraphicConveyor.multiplyMatrix4ByVector4;

public class VertexProjector {

    public static ArrayList<Vertex> projectPolygon(Model model, Polygon polygon,
                                                   Matrix4f mvp, int width, int height) {
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

            result.add(new Vertex(screenX, screenY, ndcZ, invW, u, vTex));
        }

        return result;
    }
}
