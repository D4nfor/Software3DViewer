package com.cgvsu.utils.math;

public class Vertex {
    public final float x, y;   // экранные координаты
    public final float z;      // глубина (NDC Z)
    public final float invW;   // 1 / W
    public final float u, v;   // UV координаты

    public Vertex(float x, float y, float z, float invW, float u, float v) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.invW = invW;
        this.u = u;
        this.v = v;
    }
}