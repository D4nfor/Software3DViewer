package math.tests;

import math.model.Vector2f;
import math.model.Vector3f;
import math.model.Vector4f;
import math.model.Matrix3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlingTest {

    @Test
    public void testDivisionByZeroThrowsException() {
        Vector2f v = new Vector2f(1, 1);
        assertThrows(ArithmeticException.class, () -> v.divide(0));
    }

    @Test
    public void testNormalizeZeroVectorThrowsException() {
        Vector3f zero = new Vector3f(0, 0, 0);
        assertThrows(ArithmeticException.class, zero::normalize);
    }

    @Test
    public void testInvertSingularMatrixThrowsException() {
        Matrix3f singular = new Matrix3f(new float[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });
        assertThrows(ArithmeticException.class, singular::inverse);
    }

    @Test
    public void testVector4fToVector3fWithW0ThrowsException() {
        Vector4f v = new Vector4f(1, 2, 3, 0);
        assertThrows(ArithmeticException.class, v::toVector3f);
    }

    @Test
    public void testSolvingInconsistentSystemThrowsException() {
        Matrix3f inconsistent = new Matrix3f(new float[][]{
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        });
        Vector3f b = new Vector3f(1, 2, 3);
        assertThrows(ArithmeticException.class, () -> inconsistent.solveLinearSystem(b));
    }
}