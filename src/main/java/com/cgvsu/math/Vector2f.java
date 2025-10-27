package com.cgvsu.math;

import com.cgvsu.math.base.AbstractVector;

public class Vector2f extends AbstractVector<Vector2f> {

    public Vector2f(float x, float y) {
        super(new float[]{x, y});
    }

    @Override
    protected Vector2f createNew(float[] components) {
        return new Vector2f(components[0], components[1]);
    }

    public float getX() { return components[0]; }
    public float getY() { return components[1]; }

    public float distance(Vector2f other) { // расстояние

        float dx = getX() - other.getX();
        float dy = getY() - other.getY();

        return (float) Math.sqrt(dx * dx + dy * dy);

    }

    @Override
    public String toString() {

        return String.format("(%.4f, %.4f)", getX(), getY());

    }
}
