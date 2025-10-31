package com.cgvsu.utils.math;

import com.cgvsu.utils.math.base.AbstractPoint;

public class Point2f extends AbstractPoint<Point2f, Vector2f> {

    public Point2f(float x, float y) {
        super(new float[]{x, y});
    }

    @Override
    protected Point2f createNew(float[] coordinates) {
        return new Point2f(coordinates[0], coordinates[1]);
    }

    @Override
    protected Vector2f createVector(float[] components) {
        return new Vector2f(components[0], components[1]);
    }

    public float getX() { return coordinates[0]; }
    public float getY() { return coordinates[1]; }
}
