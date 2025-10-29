package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Point2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphicConveyorTest {

    private static final float EPS = 1e-5f;

    // === SCALE ===
    @Test
    void testScaleMatrix() {
        Matrix4f m = GraphicConveyor.scale(2, 3, 4);
        assertEquals(2, m.get(0,0), EPS);
        assertEquals(3, m.get(1,1), EPS);
        assertEquals(4, m.get(2,2), EPS);
        assertEquals(1, m.get(3,3), EPS);
    }

    // === ROTATE X ===
    @Test
    void testRotateXMatrix() {
        Matrix4f m = GraphicConveyor.rotateX((float)Math.toRadians(90f));
        assertEquals(1, m.get(0,0), EPS);
        assertEquals(0, m.get(1,1), EPS);
        assertEquals(-1, m.get(1,2), EPS);
        assertEquals(1, m.get(2,1), EPS);
        assertEquals(0, m.get(2,2), EPS);

    }

    // === ROTATE Y ===
    @Test
    void testRotateYMatrix() {
        Matrix4f m = GraphicConveyor.rotateY((float)Math.PI / 2);
        assertEquals(0, m.get(0,0), EPS);
        assertEquals(1, m.get(1,1), EPS);
        assertEquals(1, m.get(0,2), EPS);
        assertEquals(-1, m.get(2,0), EPS);
        assertEquals(0, m.get(2,2), EPS);
    }

    // === ROTATE Z ===
    @Test
    void testRotateZMatrix() {
        Matrix4f m = GraphicConveyor.rotateZ((float)Math.PI / 2);
        assertEquals(0, m.get(0,0), EPS);
        assertEquals(-1, m.get(0,1), EPS);
        assertEquals(1, m.get(1,0), EPS);
        assertEquals(0, m.get(1,1), EPS);
        assertEquals(1, m.get(2,2), EPS);
        assertEquals(1, m.get(3,3), EPS);
    }

    // === TRANSLATE ===
    @Test
    void testTranslateMatrix() {
        Matrix4f m = GraphicConveyor.translate(1, 2, 3);
        assertEquals(1, m.get(0,3), EPS);
        assertEquals(2, m.get(1,3), EPS);
        assertEquals(3, m.get(2,3), EPS);
        assertEquals(1, m.get(3,3), EPS);
    }

    // === CREATE MODEL MATRIX ===
    @Test
    void testCreateModelMatrix() {
        Matrix4f model = GraphicConveyor.createModelMatrix(
                2, 2, 2,
                0, 0, 0,
                1, 2, 3);

        // Проверяем масштаб и перенос
        assertEquals(2, model.get(0,0), EPS);
        assertEquals(2, model.get(1,1), EPS);
        assertEquals(2, model.get(2,2), EPS);
        assertEquals(1, model.get(0,3), EPS);
        assertEquals(2, model.get(1,3), EPS);
        assertEquals(3, model.get(2,3), EPS);
    }

    // === LOOK AT ===
    @Test
    void testLookAt() {
        Vector3f eye = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);

        Matrix4f view = GraphicConveyor.lookAt(eye, target);

        // Z ось должна смотреть "вниз" по Z
        assertTrue(view.get(2,2) < 0.0f || view.get(2,2) > 0.0f); // не ноль
        assertEquals(10, view.get(2,3), 1e-4); // должно быть -z.dot(eye)
    }

    // === PERSPECTIVE ===
    @Test
    void testPerspectiveMatrix() {
        float fov = (float)Math.toRadians(90);
        float aspect = 16f / 9f;
        float near = 0.1f;
        float far = 100f;

        Matrix4f p = GraphicConveyor.perspective(fov, aspect, near, far);

        assertEquals(1 / (float)Math.tan(fov / 2) / aspect, p.get(0,0), EPS);
        assertEquals(1 / (float)Math.tan(fov / 2), p.get(1,1), EPS);
        assertEquals(-1, p.get(3,2), EPS);
    }

    // === MULTIPLY MATRIX BY VECTOR ===
    @Test
    void testMultiplyMatrixByVector() {
        Matrix4f m = GraphicConveyor.translate(1, 2, 3);
        Vector3f v = new Vector3f(1, 1, 1);
        Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(m, v);

        // Должен просто добавиться перенос
        assertEquals(2, result.getX(), EPS);
        assertEquals(3, result.getY(), EPS);
        assertEquals(4, result.getZ(), EPS);
    }

    // === VERTEX TO POINT ===
    @Test
    void testVertexToPoint() {
        Vector3f v = new Vector3f(0, 0, 0);
        Point2f p = GraphicConveyor.vertexToPoint(v, 800, 600);
        assertEquals(400, p.getX(), EPS);
        assertEquals(300, p.getY(), EPS);
    }
}
