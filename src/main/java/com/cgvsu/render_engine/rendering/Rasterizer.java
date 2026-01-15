package com.cgvsu.render_engine.rendering;

import com.cgvsu.model.Model;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import com.cgvsu.utils.math.Vertex;

import java.util.ArrayList;

public class Rasterizer {

    private static final Color FILL_COLOR = Color.LIGHTGRAY;

    public static void triangulateAndRasterize(ArrayList<Vertex> poly,
                                               Model model,
                                               ZBuffer zBuffer,
                                               PixelWriter pw) {
        if (poly.size() < 3) return;
        for (int i = 1; i < poly.size() - 1; i++) {
            rasterizeTriangle(poly.get(0), poly.get(i), poly.get(i + 1),
                    model, zBuffer, pw);
        }
    }

    public static void rasterizeTriangle(Vertex v0, Vertex v1, Vertex v2,
                                         Model model,
                                         ZBuffer zBuffer,
                                         PixelWriter pw) {
        int width = zBuffer.getWidth();
        int height = zBuffer.getHeight();

        int minX = (int) Math.max(0, Math.floor(Math.min(v0.x, Math.min(v1.x, v2.x))));
        int maxX = (int) Math.min(width - 1, Math.ceil(Math.max(v0.x, Math.max(v1.x, v2.x))));
        int minY = (int) Math.max(0, Math.floor(Math.min(v0.y, Math.min(v1.y, v2.y))));
        int maxY = (int) Math.min(height - 1, Math.ceil(Math.max(v0.y, Math.max(v1.y, v2.y))));

        float area = edge(v0, v1, v2);
        if (area == 0) return;

        Image tex = model.getTexture();

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

                    if (zBuffer.testAndSet(x, y, z)) {
                        if (tex != null) {
                            // Интерполяция координат текстуры с учетом w
                            float invW = w0 * v0.invW + w1 * v1.invW + w2 * v2.invW;
                            float u = (w0 * v0.u * v0.invW + w1 * v1.u * v1.invW + w2 * v2.u * v2.invW) / invW;
                            float v = (w0 * v0.v * v0.invW + w1 * v1.v * v1.invW + w2 * v2.v * v2.invW) / invW;

                            int tx = clamp((float)(u * (tex.getWidth() - 1)), 0, (int)tex.getWidth() - 1);
                            int ty = clamp((float)((1 - v) * (tex.getHeight() - 1)), 0, (int)tex.getHeight() - 1);

                            pw.setColor(x, y, tex.getPixelReader().getColor(tx, ty));
                        } else {
                            pw.setColor(x, y, FILL_COLOR);
                        }
                    }
                }
            }
        }
    }

    private static float edge(Vertex a, Vertex b, Vertex c) {
        return (c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x);
    }

    private static int clamp(float val, int min, int max) {
        return Math.max(min, Math.min(max, (int)val));
    }
}
