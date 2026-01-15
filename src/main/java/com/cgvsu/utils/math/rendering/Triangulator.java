package com.cgvsu.utils.math.rendering;


import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Triangulator {

    /**
     * Триангулирует все полигоны модели, если они содержат больше 3 вершин.
     * Используется простой "fan triangulation" (вентиль), подходит для выпуклых многоугольников.
     */
    public static void triangulate(Model model) {
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        for (Polygon polygon : model.getPolygons()) {
            List<Integer> v = polygon.getVertexIndices();

            if (v.size() <= 3) {
                newPolygons.add(polygon); // уже треугольник
                continue;
            }

            // Fan triangulation: вершина 0 соединяется с каждой парой (i, i+1)
            for (int i = 1; i < v.size() - 1; i++) {
                Polygon.Builder builder = Polygon.builder()
                        .addVertexIndex(v.get(0))
                        .addVertexIndex(v.get(i))
                        .addVertexIndex(v.get(i + 1));

                // можно копировать текстурные индексы, если есть
                if (!polygon.getTextureVertexIndices().isEmpty()) {
                    builder.addTextureVertexIndex(polygon.getTextureVertexIndices().get(0));
                    builder.addTextureVertexIndex(polygon.getTextureVertexIndices().get(i));
                    builder.addTextureVertexIndex(polygon.getTextureVertexIndices().get(i + 1));
                }

                newPolygons.add(builder.build());
            }
        }

        model.setPolygons(newPolygons);
    }
}
