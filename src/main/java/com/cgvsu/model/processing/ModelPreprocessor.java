package com.cgvsu.model.processing;

import com.cgvsu.model.Model;
import com.cgvsu.utils.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.cgvsu.model.processing.Triangulator.triangulate;

public class ModelPreprocessor {

    public static Model prepare(Model model) {
        triangulate(model);
        recalculateNormals(model);
        return model;
    }

    private static void recalculateNormals(Model model) {

        int n = model.getVertices().size();
        List<Vector3f> normals = new ArrayList<>();

        // 1. Обнуляем нормали
        for (int i = 0; i < n; i++) {
            normals.add(new Vector3f(0, 0, 0));
        }

        // 2. Для каждого треугольника считаем его нормаль
        for (var poly : model.getPolygons()) {

            // мы гарантировали, что после триангуляции тут ТОЛЬКО треугольники
            int i1 = poly.getVertexIndices().get(0);
            int i2 = poly.getVertexIndices().get(1);
            int i3 = poly.getVertexIndices().get(2);

            Vector3f v1 = model.getVertices().get(i1);
            Vector3f v2 = model.getVertices().get(i2);
            Vector3f v3 = model.getVertices().get(i3);

            Vector3f faceNormal = computeFaceNormal(v1, v2, v3);

            normals.set(i1, normals.get(i1).add(faceNormal));
            normals.set(i2, normals.get(i2).add(faceNormal));
            normals.set(i3, normals.get(i3).add(faceNormal));
        }

        // 3. Нормализуем нормали вершин
        for (int i = 0; i < normals.size(); i++) {
            normals.set(i, normals.get(i).normalize());
        }

        model.setNormals(normals);
    }

    private static Vector3f computeFaceNormal(Vector3f a, Vector3f b, Vector3f c) {
        Vector3f u = b.subtract(a);
        Vector3f v = c.subtract(a);

        return u.cross(v).normalize();
    }

}
