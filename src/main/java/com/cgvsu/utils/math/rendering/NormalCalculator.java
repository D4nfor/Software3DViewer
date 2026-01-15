package com.cgvsu.utils.math.rendering;


import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.utils.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class NormalCalculator {

    /**
     * Вычисляет нормали для всех треугольников и вершин модели.
     * Нормали вершин усредняются по полигонам, которые их используют.
     */
    public static void calculateNormals(Model model) {
        List<Vector3f> vertices = model.getVertices();
        List<Polygon> polygons = model.getPolygons();

        List<Vector3f> vertexNormals = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            vertexNormals.add(new Vector3f(0, 0, 0));
        }

        List<Integer> counts = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) counts.add(0);

        List<Vector3f> polygonNormals = new ArrayList<>();

        for (Polygon polygon : polygons) {
            List<Integer> inds = polygon.getVertexIndices();
            if (inds.size() < 3) continue;

            Vector3f v0 = vertices.get(inds.get(0));
            Vector3f v1 = vertices.get(inds.get(1));
            Vector3f v2 = vertices.get(inds.get(2));

            // нормаль полигона
            Vector3f edge1 = v1.subtract(v0);
            Vector3f edge2 = v2.subtract(v0);
            Vector3f normal = edge1.cross(edge2).normalize();

            polygonNormals.add(normal);

            // добавляем вклад в нормали вершин
            for (int idx : inds) {
                vertexNormals.set(idx, vertexNormals.get(idx).add(normal));
                counts.set(idx, counts.get(idx) + 1);
            }
        }

        // усредняем нормали вершин
        for (int i = 0; i < vertexNormals.size(); i++) {
            if (counts.get(i) > 0) {
                vertexNormals.set(i, vertexNormals.get(i).divide((float) counts.get(i)).normalize());
            }
        }

        model.setNormals(vertexNormals);
    }
}
