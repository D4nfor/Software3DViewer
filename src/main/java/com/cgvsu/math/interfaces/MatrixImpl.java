package com.cgvsu.math.interfaces;

public interface MatrixImpl<T extends MatrixImpl<T, V>, V extends VectorImpl<V>> {

    T add(T other); // сложение
    T subtract(T other); // разность
    T multiply(float scalar); // на скаляр умножение
    V multiply(V vector); // умножение матрицы на вектор столбец !!!
    T multiply(T other); // просто умножение матриц

    T transpose(); // транспонирование
    float determinant(); // определитель
    T inverse(); // обратная
    V solveLinearSystem(V vector); // Гаусс

    boolean equals(Object obj); // сравнение
    int hashCode(); // для корректной работы
    String toString(); // в строку

}
