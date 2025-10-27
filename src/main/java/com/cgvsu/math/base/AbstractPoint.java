package com.cgvsu.math.base;

import com.cgvsu.math.interfaces.PointImpl;
import com.cgvsu.math.interfaces.VectorImpl;

public abstract class AbstractPoint<T extends AbstractPoint<T, V>, V extends VectorImpl<V>>
        implements PointImpl<T, V> {

    protected final float[] coordinates;

    protected AbstractPoint(float[] coordinates) {
        this.coordinates = coordinates.clone();
    }

    protected abstract T createNew(float[] coordinates);

    protected abstract V createVector(float[] components);

    @Override
    public T translate(V vector) {
        float[] result = new float[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            result[i] = coordinates[i] + vector.getComponent(i);
        }
        return createNew(result);
    }

    @Override
    public V subtract(T other) {
        float[] result = new float[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            result[i] = coordinates[i] - other.coordinates[i];
        }
        return createVector(result);
    }

    @Override
    public float distance(T other) {
        float sum = 0;
        for (int i = 0; i < coordinates.length; i++) {
            float diff = coordinates[i] - other.coordinates[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AbstractPoint<?, ?> other = (AbstractPoint<?, ?>) obj;
        if (coordinates.length != other.coordinates.length) return false;

        for (int i = 0; i < coordinates.length; i++) {
            if (Math.abs(coordinates[i] - other.coordinates[i]) >= 1e-6f) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (float c : coordinates) result = 31 * result + Float.hashCode(c);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < coordinates.length; i++) {
            sb.append(String.format("%.4f", coordinates[i]));
            if (i < coordinates.length - 1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}
