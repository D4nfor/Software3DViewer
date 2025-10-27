package com.cgvsu.math;

import com.cgvsu.math.base.AbstractVector;

public class Vector3f extends AbstractVector<Vector3f> {

    public Vector3f(float x, float y, float z) {
        super(new float[]{x, y, z});
    }

    @Override
    protected Vector3f createNew(float[] components) {
        return new Vector3f(components[0], components[1], components[2]);
    }

    public float getX() { return components[0]; }
    public float getY() { return components[1]; }
    public float getZ() { return components[2]; }

    public Vector3f cross(Vector3f other) { // векторное произведение
        return new Vector3f(
                getY() * other.getZ() - getZ() * other.getY(),
                getZ() * other.getX() - getX() * other.getZ(),
                getX() * other.getY() - getY() * other.getX()
        );
    }

    public float distance(Vector3f other) { // расстояние

        float dx = getX() - other.getX();
        float dy = getY() - other.getY();
        float dz = getZ() - other.getZ();

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return String.format("(%.4f, %.4f, %.4f)", getX(), getY(), getZ());
    }
}