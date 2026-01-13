package com.cgvsu.model.processing;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;


public class Triangulator {


    // Триангулируем все полигоны модели методом веера

    public static Model triangulate(Model model) {
        if (model == null || model.getPolygons() == null) {
            return model;
        }

        ArrayList<Polygon> originalPolygons = model.getPolygons();
        ArrayList<Polygon> triangles = new ArrayList<>();

        for (Polygon polygon : originalPolygons) {
            triangles.addAll(triangulatePolygon(polygon));
        }

        model.setPolygons(triangles);
        return model;
    }


    // Триангулируем один полигон

    private static List<Polygon> triangulatePolygon(Polygon polygon) {
        List<Polygon> triangles = new ArrayList<>();

        List<Integer> vertices = polygon.getVertexIndices();
        List<Integer> textures = polygon.getTextureVertexIndices();
        List<Integer> normals = polygon.getNormalIndices();

        int vertexCount = vertices.size();

        // Если уже треугольник - возвращаем как есть
        if (vertexCount == 3) {
            triangles.add(polygon);
            return triangles;
        }

        // Проверяем наличие текстур и нормалей
        boolean hasTextures = !textures.isEmpty();
        boolean hasNormals = !normals.isEmpty();

        // Проверяем, что количество текстур/нормалей соответствует вершинам
        boolean texturesMatch = hasTextures && textures.size() == vertexCount;
        boolean normalsMatch = hasNormals && normals.size() == vertexCount;

        // Триангуляция веером от первой вершины
        for (int i = 1; i < vertexCount - 1; i++) {
            Polygon.Builder builder = Polygon.builder()
                    .addVertexIndex(vertices.get(0))
                    .addVertexIndex(vertices.get(i))
                    .addVertexIndex(vertices.get(i + 1));

            // Сохраняем текстурные координаты если есть и соответствуют
            if (texturesMatch) {
                builder.addTextureVertexIndex(textures.get(0))
                        .addTextureVertexIndex(textures.get(i))
                        .addTextureVertexIndex(textures.get(i + 1));
            }

            // Сохраняем нормали если есть и соответствуют
            if (normalsMatch) {
                builder.addNormalIndex(normals.get(0))
                        .addNormalIndex(normals.get(i))
                        .addNormalIndex(normals.get(i + 1));
            }

            triangles.add(builder.build());
        }

        return triangles;
    }
}