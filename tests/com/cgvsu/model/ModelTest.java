package com.cgvsu.model;

import com.cgvsu.render_engine.Transform;
import com.cgvsu.utils.math.Vector2f;
import com.cgvsu.utils.math.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    // Вспомогательный метод для создания простого полигона
    private Polygon createPolygon(int... vertexIndices) {
        Polygon.Builder builder = Polygon.builder();
        for (int index : vertexIndices) {
            builder.addVertexIndex(index);
        }
        return builder.build();
    }

    @Test
    public void testEmptyModelInitialization() {
        Model model = new Model();
        assertNotNull(model.getVertices());
        assertNotNull(model.getNormals());
        assertNotNull(model.getPolygons());
        assertNotNull(model.getTextureVertices());
        assertNotNull(model.getTransform());
    }

    @Test
    public void testSetAndGetTransform() {
        Model model = new Model();
        Transform t = new Transform();
        t.translateX = 1.0f;
        model.setTransform(t);
        assertEquals(1.0f, model.getTransform().translateX);
    }

    @Test
    public void testVertexAdditionAndRetrieval() {
        Model model = new Model();
        Vector3f v = new Vector3f(1, 2, 3);
        model.getVertices().add(v);
        assertEquals(1, model.getVertices().size());
        assertEquals(v, model.getVertices().get(0));
    }

    @Test
    public void testPolygonAdditionAndRetrieval() {
        Model model = new Model();
        Polygon polygon = createPolygon(0, 1, 2);
        model.getPolygons().add(polygon);
        assertEquals(1, model.getPolygons().size());
        assertEquals(polygon, model.getPolygons().get(0));
    }

    @Test
    public void testDeletePolygon() {
        Model model = new Model();
        Polygon p1 = createPolygon(0, 1, 2);
        Polygon p2 = createPolygon(1, 2, 3);
        model.getPolygons().addAll(Arrays.asList(p1, p2));

        assertTrue(model.deletePolygon(0));
        assertEquals(1, model.getPolygons().size());
        assertEquals(p2, model.getPolygons().get(0));

        assertFalse(model.deletePolygon(5));
    }

    @Test
    public void testDeleteVerticesAndUpdatePolygons() {
        Model model = new Model();
        model.getVertices().addAll(Arrays.asList(
                new Vector3f(0,0,0),
                new Vector3f(1,0,0),
                new Vector3f(0,1,0)
        ));
        Polygon p = createPolygon(0, 1, 2);
        model.getPolygons().add(p);

        int deletedCount = model.deleteVertices(Collections.singletonList(1));
        assertEquals(1, deletedCount);
        assertEquals(2, model.getVertices().size());

        assertTrue(model.getPolygons().isEmpty());
    }


    @Test
    public void testDeleteUnusedVertices() {
        Model model = new Model();
        model.getVertices().addAll(Arrays.asList(
                new Vector3f(0,0,0),
                new Vector3f(1,0,0),
                new Vector3f(0,1,0)
        ));
        Polygon p = createPolygon(0, 2, 0);
        model.getPolygons().add(p);

        int deleted = model.deleteUnusedVertices();
        assertEquals(1, deleted);
        assertEquals(2, model.getVertices().size());
    }

    @Test
    public void testValidateModel() {
        Model model = new Model();
        model.getVertices().addAll(Arrays.asList(
                new Vector3f(0,0,0),
                new Vector3f(1,0,0),
                new Vector3f(0,1,0)
        ));
        Polygon validPolygon = createPolygon(0, 1, 2);
        model.getPolygons().add(validPolygon);
        assertTrue(model.validateModel());

        Polygon invalidPolygon = createPolygon(0, 1, 5);
        model.getPolygons().add(invalidPolygon);
        assertFalse(model.validateModel());
    }

    @Test
    public void testCleanInvalidPolygons() {
        Model model = new Model();
        model.getVertices().addAll(Arrays.asList(
                new Vector3f(0,0,0),
                new Vector3f(1,0,0),
                new Vector3f(0,1,0)
        ));

        Polygon validPolygon = createPolygon(0,1,2);
        Polygon invalidPolygon = createPolygon(0,1,5);
        model.getPolygons().addAll(Arrays.asList(validPolygon, invalidPolygon));

        int removed = model.cleanInvalidPolygons();
        assertEquals(1, removed);
        assertEquals(1, model.getPolygons().size());
        assertEquals(validPolygon, model.getPolygons().get(0));
    }

    @Test
    public void testGetUsedAndUnusedVertices() {
        Model model = new Model();
        model.getVertices().addAll(Arrays.asList(
                new Vector3f(0,0,0),
                new Vector3f(1,0,0),
                new Vector3f(0,1,0)
        ));
        Polygon polygon = createPolygon(0,2,0);
        model.getPolygons().add(polygon);

        assertEquals(2, model.getUsedVertices().size());
        assertEquals(Collections.singletonList(1), model.getUnusedVertices());
    }
}
