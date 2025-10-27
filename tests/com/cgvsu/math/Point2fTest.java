package com.cgvsu.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Point2fTest {

    @Test
    public void testPointCreation() {
        Point2f p = new Point2f(3.5f, -2.1f);
        assertEquals(3.5f, p.getX(), 0.0001f);
        assertEquals(-2.1f, p.getY(), 0.0001f);
    }

    @Test
    public void testTranslateByVector() {
        Point2f p = new Point2f(1.0f, 2.0f);
        Vector2f v = new Vector2f(3.0f, -1.0f);
        Point2f translated = p.translate(v);
        assertEquals(new Point2f(4.0f, 1.0f), translated);
    }

    @Test
    public void testSubtractReturnsVector() {
        Point2f p1 = new Point2f(5.0f, 7.0f);
        Point2f p2 = new Point2f(2.0f, 3.0f);
        Vector2f result = p1.subtract(p2);
        assertEquals(new Vector2f(3.0f, 4.0f), result);
    }

    @Test
    public void testDistance() {
        Point2f p1 = new Point2f(1.0f, 2.0f);
        Point2f p2 = new Point2f(4.0f, 6.0f);
        float dist = p1.distance(p2);
        assertEquals(5.0f, dist, 0.0001f); // 3-4-5 triangle
    }

    @Test
    public void testEqualsAndHashCode() {
        // Используем одинаковые значения или меньшую разницу
        Point2f p1 = new Point2f(1.000001f, 2.0f);
        Point2f p2 = new Point2f(1.0000005f, 2.0f); // разница 0.0000005 < 0.000001

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testToString() {
        Point2f p = new Point2f(1.2345f, -6.789f);
        assertEquals("(1,2345, -6,7890)", p.toString());
    }
}
