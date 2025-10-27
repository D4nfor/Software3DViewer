package com.cgvsu.controller;

import com.cgvsu.math.*;

public class MathController {

    public Vector2f addVectors(Vector2f a, Vector2f b) { // сложение 2D
        return a.add(b);
    }

    public Vector2f subtractVectors(Vector2f a, Vector2f b) { // вычитание 2D
        return a.subtract(b);
    }

    public Vector3f addVectors(Vector3f a, Vector3f b) { // сложение 3D
        return a.add(b);
    }

    public Vector3f subtractVectors(Vector3f a, Vector3f b) { // вычитание 3D
        return a.subtract(b);
    }

    public Vector4f addVectors(Vector4f a, Vector4f b) { // сложение 4D
        return a.add(b);
    }

    public Vector4f subtractVectors(Vector4f a, Vector4f b) { // вычитание 4D
        return a.subtract(b);
    }

    public Vector3f crossProduct(Vector3f a, Vector3f b) { // векторное произведение для 3D
        return a.cross(b);
    }

    public float dotProduct(Vector2f a, Vector2f b) { // скалярное произведение для 2D
        return a.dot(b);
    }

    public float dotProduct(Vector3f a, Vector3f b) { // скалярное произведение для 3D
        return a.dot(b);
    }

    public float dotProduct(Vector4f a, Vector4f b) { // скалярное произведение для 4D
        return a.dot(b);
    }

    public Vector2f multiplyVector(Vector2f v, float scalar) { // умножение на скаляр 2D
        return v.multiply(scalar);
    }

    public Vector2f divideVector(Vector2f v, float scalar) { // деление на скаляр 2D
        return v.divide(scalar);
    }

    public Vector3f multiplyVector(Vector3f v, float scalar) { // умножение на скаляр 3D
        return v.multiply(scalar);
    }

    public Vector3f divideVector(Vector3f v, float scalar) { // деление на скаляр 3D
        return v.divide(scalar);
    }

    public Vector4f multiplyVector(Vector4f v, float scalar) { // умножение на скаляр 4D
        return v.multiply(scalar);
    }

    public Vector4f divideVector(Vector4f v, float scalar) { // деление на скаляр 4D
        return v.divide(scalar);
    }

    public float vectorLength(Vector2f v) { // длинна 2D
        return v.length();
    }

    public float vectorLength(Vector3f v) { // длинна 3D
        return v.length();
    }

    public float vectorLength(Vector4f v) { // длинна 4D
        return v.length();
    }

    public Vector2f normalizeVector(Vector2f v) { // нормализация 2D
        return v.normalize();
    }

    public Vector3f normalizeVector(Vector3f v) { // нормализация 3D
        return v.normalize();
    }

    public Vector4f normalizeVector(Vector4f v) { // нормализация 4D
        return v.normalize();
    }

    public float distance(Vector2f a, Vector2f b) { // расстояние 2D
        return a.distance(b);
    }

    public float distance(Vector3f a, Vector3f b) { // расстояние 3D
        return a.distance(b);
    }

    public float distance(Vector4f a, Vector4f b) { // расстояние 4D
        return a.distance(b);
    }

    public Matrix3f addMatrices(Matrix3f a, Matrix3f b) { // сложение 3 на 3
        return a.add(b);
    }

    public Matrix3f subtractMatrices(Matrix3f a, Matrix3f b) { // вычитание 3 на 3
        return a.subtract(b);
    }

    public Matrix3f multiplyMatrices(Matrix3f a, Matrix3f b) { // умножение матриц 3 на 3
        return a.multiply(b);
    }

    public Matrix3f multiplyMatrix(Matrix3f m, float scalar) { // умножение на скаляр
        return m.multiply(scalar);
    }

    public Matrix3f transposeMatrix(Matrix3f m) { // транспонирование 3 на 3
        return m.transpose();
    }

    public Matrix3f inverseMatrix(Matrix3f m) { // обратная для 3 на 3
        return m.inverse();
    }

    public Vector3f multiplyMatrixVector(Matrix3f m, Vector3f v) { // умножение матрицы 3 на 3 на вектор 3D
        return m.multiply(v);
    }

    public float matrixDeterminant(Matrix3f m) { // определитель 3 на 3
        return m.determinant();
    }

    public Vector3f solveLinearSystem(Matrix3f A, Vector3f b) { // решение системы лин ур 3 на 3
        return A.solveLinearSystem(b);
    }

    // для 4 на 4 = аналогично

    public Matrix4f addMatrices(Matrix4f a, Matrix4f b) {
        return a.add(b);
    }

    public Matrix4f subtractMatrices(Matrix4f a, Matrix4f b) {
        return a.subtract(b);
    }

    public Matrix4f multiplyMatrices(Matrix4f a, Matrix4f b) {
        return a.multiply(b);
    }

    public Matrix4f multiplyMatrix(Matrix4f m, float scalar) {
        return m.multiply(scalar);
    }

    public Matrix4f transposeMatrix(Matrix4f m) {
        return m.transpose();
    }

    public Matrix4f inverseMatrix(Matrix4f m) {
        return m.inverse();
    }

    public Vector4f multiplyMatrixVector(Matrix4f m, Vector4f v) {
        return m.multiply(v);
    }

    public float matrixDeterminant(Matrix4f m) {
        return m.determinant();
    }

    public Vector4f solveLinearSystem(Matrix4f A, Vector4f b) {
        return A.solveLinearSystem(b);
    }

    // !!!

    public Vector4f vector3fTo4f(Vector3f v) { // преобразование 3D в 4D вектор
        return new Vector4f(v);
    }

    public Vector3f vector4fTo3f(Vector4f v) { // наоборот
        return v.toVector3f();
    }
}
