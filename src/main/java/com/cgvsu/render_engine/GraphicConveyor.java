package com.cgvsu.render_engine;

import com.cgvsu.math.Point2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Matrix4f;

public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        float[][] identity = new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4f(identity);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0f, 1f, 0f));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f z = target.subtract(eye).normalize();
        Vector3f x = up.cross(z).normalize();
        Vector3f y = z.cross(x).normalize();

        float[][] data = new float[4][4];

        // Для векторов-столбцов
        data[0][0] = x.getX(); data[0][1] = x.getY(); data[0][2] = x.getZ(); data[0][3] = -x.dot(eye);
        data[1][0] = y.getX(); data[1][1] = y.getY(); data[1][2] = y.getZ(); data[1][3] = -y.dot(eye);
        data[2][0] = z.getX(); data[2][1] = z.getY(); data[2][2] = z.getZ(); data[2][3] = -z.dot(eye);
        data[3][0] = 0;       data[3][1] = 0;       data[3][2] = 0;       data[3][3] = 1;

        return new Matrix4f(data);
    }

    public static Matrix4f perspective(float fov, float aspectRatio, float nearPlane, float farPlane) {
        float f = 1.0f / (float)Math.tan(fov / 2.0f);
        float[][] data = new float[4][4];

        data[0][0] = f / aspectRatio; data[0][1] = 0; data[0][2] = 0; data[0][3] = 0;
        data[1][0] = 0; data[1][1] = f; data[1][2] = 0; data[1][3] = 0;
        data[2][0] = 0; data[2][1] = 0; data[2][2] = (farPlane + nearPlane) / (nearPlane - farPlane); data[2][3] = (2 * farPlane * nearPlane) / (nearPlane - farPlane);
        data[3][0] = 0; data[3][1] = 0; data[3][2] = -1; data[3][3] = 0;

        return new Matrix4f(data);
    }

    public static Vector3f multiplyMatrix4ByVector3(Matrix4f matrix, Vector3f vertex) {
        // Умножение матрицы на вектор-столбец
        float x = vertex.getX() * matrix.get(0,0) + vertex.getY() * matrix.get(0,1) + vertex.getZ() * matrix.get(0,2) + matrix.get(0,3);
        float y = vertex.getX() * matrix.get(1,0) + vertex.getY() * matrix.get(1,1) + vertex.getZ() * matrix.get(1,2) + matrix.get(1,3);
        float z = vertex.getX() * matrix.get(2,0) + vertex.getY() * matrix.get(2,1) + vertex.getZ() * matrix.get(2,2) + matrix.get(2,3);
        float w = vertex.getX() * matrix.get(3,0) + vertex.getY() * matrix.get(3,1) + vertex.getZ() * matrix.get(3,2) + matrix.get(3,3);

        return new Vector3f(x / w, y / w, z / w);
    }

    public static Point2f vertexToPoint(Vector3f vertex, int width, int height) {
        return new Point2f(vertex.getX() * width + width / 2.0f, -vertex.getY() * height + height / 2.0f);
    }
}