package com.cgvsu.math.interfaces;

public interface Vector<T extends Vector<T>> {

    T add(T other); // сложение
    T subtract(T other); // вычитание
    T multiply(float scalar); // умн на скаляр
    T divide(float scalar); // деление на скаляр
    float length(); // длинна
    T normalize(); // нормализация
    float dot(T other); // скалярное произв

    boolean equals(Object obj); // сравнение
    int hashCode(); // для корректной работы
    String toString(); // в строку

}