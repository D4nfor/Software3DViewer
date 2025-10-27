package math.tests;

import math.model.Vector2f;
import math.model.Vector3f;
import math.model.Matrix3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EdgeCasesTest {

    @Test
    public void testVector2fWithVerySmallValues() {
        Vector2f v = new Vector2f(1e-10f, 1e-10f);
        Vector2f normalized = v.normalize();
        assertEquals(1.0f, normalized.length(), 1e-6f);
    }

    @Test
    public void testVector3fWithLargeValues() {
        Vector3f v = new Vector3f(1e6f, 2e6f, 3e6f);
        Vector3f result = v.multiply(1e-6f);
        assertEquals(new Vector3f(1, 2, 3), result);
    }

    @Test
    public void testMatrixOperationsWithIdentity() {
        Matrix3f identity = Matrix3f.identity();
        Matrix3f matrix = new Matrix3f(new float[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });

        Matrix3f result = matrix.multiply(identity);
        assertEquals(matrix, result);
    }

    @Test
    public void testVectorOperationsWithZeroVector() {
        Vector3f zero = new Vector3f(0, 0, 0);
        Vector3f v = new Vector3f(1, 2, 3);

        Vector3f result = zero.add(v);
        assertEquals(v, result);
    }
}