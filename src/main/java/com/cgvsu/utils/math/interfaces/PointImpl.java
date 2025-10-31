package com.cgvsu.utils.math.interfaces;

public interface PointImpl<T extends PointImpl<T, V>, V extends VectorImpl<V>> {

    T translate(V vector);   // переместить точку на вектор
    V subtract(T other);     // разность двух точек как вектор
    float distance(T other); // расстояние до другой точки

    boolean equals(Object obj);
    int hashCode();
    String toString();
}
