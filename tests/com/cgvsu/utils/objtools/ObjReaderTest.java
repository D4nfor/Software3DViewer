package com.cgvsu.utils.objtools;

import com.cgvsu.utils.math.Vector2f;
import com.cgvsu.utils.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class ObjReaderTest {

    /* ---------------------- VERTEX TESTS ---------------------- */

    @Test
    public void testParseVertex_Valid() {
        var args = new ArrayList<>(Arrays.asList("1.5", "2.5", "3.5"));
        Vector3f v = ObjReader.parseVertex(args, 1);
        Assertions.assertEquals(new Vector3f(1.5f, 2.5f, 3.5f), v);
    }

    @Test
    public void testParseVertex_WithFourCoordinates() {
        // OBJ может содержать однородную координату w
        var args = new ArrayList<>(Arrays.asList("1.5", "2.5", "3.5", "1.0"));
        Vector3f v = ObjReader.parseVertex(args, 1);
        Assertions.assertEquals(new Vector3f(1.5f, 2.5f, 3.5f), v);
    }

    @Test
    public void testParseVertex_TooFew() {
        var args = new ArrayList<>(Arrays.asList("1", "2"));
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseVertex(args, 3)
        );
        Assertions.assertTrue(ex.getMessage().contains("Vertex must contain at least 3 numbers"));
    }

    @Test
    public void testParseVertex_InvalidFormat() {
        var args = new ArrayList<>(Arrays.asList("abc", "2", "3"));
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseVertex(args, 4)
        );
        Assertions.assertTrue(ex.getMessage().contains("Failed to parse float value in vertex"));
    }

    /* ---------------------- TEXTURE VERTEX TESTS ---------------------- */

    @Test
    public void testParseTextureVertex_Valid() {
        var args = new ArrayList<>(Arrays.asList("0.3", "0.8"));
        Vector2f v = ObjReader.parseTextureVertex(args, 1);
        Assertions.assertEquals(new Vector2f(0.3f, 0.8f), v);
    }

    @Test
    public void testParseTextureVertex_WithThreeCoordinates() {
        // OBJ может содержать 3 координаты для текстур (u, v, w)
        var args = new ArrayList<>(Arrays.asList("0.3", "0.8", "0.5"));
        Vector2f v = ObjReader.parseTextureVertex(args, 1);
        Assertions.assertEquals(new Vector2f(0.3f, 0.8f), v);
    }

    @Test
    public void testParseTextureVertex_InvalidCount() {
        var args = new ArrayList<>(Arrays.asList("0.3"));
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseTextureVertex(args, 5)
        );
        Assertions.assertTrue(ex.getMessage().contains("Texture vertex must contain at least 2 numbers"));
    }

    @Test
    public void testParseTextureVertex_InvalidNumber() {
        var args = new ArrayList<>(Arrays.asList("0.3", "abc"));
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseTextureVertex(args, 6)
        );
        Assertions.assertTrue(ex.getMessage().contains("Failed to parse float value in texture vertex"));
    }

    /* ---------------------- NORMAL TESTS ---------------------- */

    @Test
    public void testParseNormal_Valid() {
        var args = new ArrayList<>(Arrays.asList("1", "0", "-1"));
        Vector3f v = ObjReader.parseNormal(args, 2);
        Assertions.assertEquals(new Vector3f(1f, 0f, -1f), v);
    }

    @Test
    public void testParseNormal_WrongCount() {
        var args = new ArrayList<>(Arrays.asList("1", "0"));
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseNormal(args, 7)
        );
        Assertions.assertTrue(ex.getMessage().contains("Normal must contain exactly 3 numbers"));
    }

    @Test
    public void testParseNormal_InvalidNumber() {
        var args = new ArrayList<>(Arrays.asList("1", "abc", "0"));
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseNormal(args, 7)
        );
        Assertions.assertTrue(ex.getMessage().contains("Failed to parse float value in normal"));
    }

    /* ---------------------- FACE WORD TESTS ---------------------- */

    @Test
    public void testParseFaceWord_v() { // f 3
        ObjReader.FaceTriple t = ObjReader.parseFaceWord("3", 1);
        Assertions.assertEquals(3, t.v);
        Assertions.assertNull(t.vt);
        Assertions.assertNull(t.vn);
    }

    @Test
    public void testParseFaceWord_v_WithZeroIndex() {
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseFaceWord("0", 1)
        );
        Assertions.assertTrue(ex.getMessage().contains("Vertex index cannot be zero"));
    }

    @Test
    public void testParseFaceWord_v_vt() { // f 3/4
        ObjReader.FaceTriple t = ObjReader.parseFaceWord("3/4", 1);
        Assertions.assertEquals(3, t.v);
        Assertions.assertEquals(4, t.vt);
        Assertions.assertNull(t.vn);
    }

    @Test
    public void testParseFaceWord_v_vt_EmptyTexture() { // f 3/
        ObjReader.FaceTriple t = ObjReader.parseFaceWord("3/", 1);
        Assertions.assertEquals(3, t.v);
        Assertions.assertNull(t.vt);
        Assertions.assertNull(t.vn);
    }

    @Test
    public void testParseFaceWord_v_vn() { // f 5//7
        ObjReader.FaceTriple t = ObjReader.parseFaceWord("5//7", 1);
        Assertions.assertEquals(5, t.v);
        Assertions.assertNull(t.vt);
        Assertions.assertEquals(7, t.vn);
    }

    @Test
    public void testParseFaceWord_v_vn_EmptyNormal() { // f 5//
        ObjReader.FaceTriple t = ObjReader.parseFaceWord("5//", 1);
        Assertions.assertEquals(5, t.v);
        Assertions.assertNull(t.vt);
        Assertions.assertNull(t.vn);
    }

    @Test
    public void testParseFaceWord_v_vt_vn() { // f 1/2/3
        ObjReader.FaceTriple t = ObjReader.parseFaceWord("1/2/3", 1);
        Assertions.assertEquals(1, t.v);
        Assertions.assertEquals(2, t.vt);
        Assertions.assertEquals(3, t.vn);
    }

    @Test
    public void testParseFaceWord_v_vt_vn_EmptyTexture() { // f 1//3
        ObjReader.FaceTriple t = ObjReader.parseFaceWord("1//3", 1);
        Assertions.assertEquals(1, t.v);
        Assertions.assertNull(t.vt);
        Assertions.assertEquals(3, t.vn);
    }

    @Test
    public void testParseFaceWord_InvalidFormat() {
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseFaceWord("1/2/3/4", 10)
        );
        Assertions.assertTrue(ex.getMessage().contains("Invalid face element format"));
    }

    @Test
    public void testParseFaceWord_InvalidInt() {
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.parseFaceWord("a/b/c", 11)
        );
        Assertions.assertTrue(ex.getMessage().contains("Failed to parse vertex index"));
    }

    /* ---------------------- INDEX RESOLVE TESTS ---------------------- */

    @Test
    public void testResolveIndex_Positive() {
        Assertions.assertEquals(0, ObjReader.resolveIndex(1, 3, 1, "vertex"));
        Assertions.assertEquals(2, ObjReader.resolveIndex(3, 3, 1, "vertex"));
    }

    @Test
    public void testResolveIndex_PositiveOutOfRange() {
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.resolveIndex(5, 3, 1, "vertex")
        );
        Assertions.assertTrue(ex.getMessage().contains("index out of range"));
    }

    @Test
    public void testResolveIndex_Relative() {
        Assertions.assertEquals(3, ObjReader.resolveIndex(-1, 4, 1, "vertex"));
        Assertions.assertEquals(2, ObjReader.resolveIndex(-2, 4, 1, "vertex"));
        Assertions.assertEquals(0, ObjReader.resolveIndex(-4, 4, 1, "vertex"));
    }

    @Test
    public void testResolveIndex_RelativeOutOfRange() {
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.resolveIndex(-10, 3, 1, "vertex")
        );
        Assertions.assertTrue(ex.getMessage().contains("Relative vertex index out of range"));
    }


    @Test
    public void testResolveIndex_EmptyList() {
        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.resolveIndex(1, 0, 1, "vertex")
        );
        Assertions.assertTrue(ex.getMessage().contains("Face references vertex data that does not exist"));
    }

    /* ---------------------- FULL READ TESTS ---------------------- */

    @Test
    public void testRead_SimpleCube() {
        String obj = """
                # Simple cube
                v 0 0 0
                v 1 0 0
                v 0 1 0
                v 1 1 0
                v 0 0 1
                v 1 0 1
                v 0 1 1
                v 1 1 1
                
                # Faces
                f 1 2 3
                f 2 4 3
                f 5 6 7
                f 6 8 7
                """;

        Model m = ObjReader.read(obj);

        Assertions.assertEquals(8, m.getVertices().size());
        Assertions.assertEquals(4, m.getPolygons().size());
        Assertions.assertTrue(m.getTextureVertices().isEmpty());
        Assertions.assertTrue(m.getNormals().isEmpty());
    }

    @Test
    public void testRead_NoVertices() {
        String obj = """
                f 1 2 3
                """;

        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.read(obj)
        );
        Assertions.assertTrue(ex.getMessage().contains("Face references vertex data that does not exist"));
    }

    @Test
    public void testRead_NoFaces() {
        String obj = """
                v 0 0 0
                v 1 0 0
                vt 0 0
                vt 1 0
                """;

        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.read(obj)
        );
        Assertions.assertTrue(ex.getMessage().contains("contains no faces"));
    }

    @Test
    public void testRead_FaceIndexOutOfRange() {
        String obj = """
                v 0 0 0
                f 2 3 4
                """;

        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.read(obj)
        );
        Assertions.assertTrue(ex.getMessage().contains("vertex index out of range"));
    }

    @Test
    public void testRead_RelativeIndices() {
        String obj = """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                f -1 -2 -3
                """;

        Model m = ObjReader.read(obj);

        Polygon p = m.getPolygons().get(0);
        Assertions.assertEquals(Arrays.asList(2, 1, 0), p.getVertexIndices());
    }

    @Test
    public void testRead_WithTextureAndNormal() {
        String obj = """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                vt 0 0
                vt 1 0
                vt 0 1
                vn 0 0 1
                vn 0 0 1
                vn 0 0 1
                f 1/1/1 2/2/2 3/3/3
                """;

        Model m = ObjReader.read(obj);

        Assertions.assertEquals(3, m.getVertices().size());
        Assertions.assertEquals(3, m.getTextureVertices().size());
        Assertions.assertEquals(3, m.getNormals().size());
        Assertions.assertEquals(1, m.getPolygons().size());

        Polygon p = m.getPolygons().get(0);
        Assertions.assertEquals(Arrays.asList(0, 1, 2), p.getVertexIndices());
        Assertions.assertEquals(Arrays.asList(0, 1, 2), p.getTextureVertexIndices());
        Assertions.assertEquals(Arrays.asList(0, 1, 2), p.getNormalIndices());
    }

    @Test
    public void testRead_InconsistentFaceFormat() {
        String obj = """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                vt 0 0
                vt 1 0
                f 1/1 2 3/3
                """;

        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.read(obj)
        );
        Assertions.assertTrue(ex.getMessage().contains("Inconsistent face format"));
    }

    @Test
    public void testRead_QuadFace() {
        String obj = """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                v 1 1 0
                f 1 2 3 4
                """;

        Model m = ObjReader.read(obj);

        Assertions.assertEquals(4, m.getVertices().size());
        Assertions.assertEquals(1, m.getPolygons().size());

        Polygon p = m.getPolygons().get(0);
        Assertions.assertEquals(Arrays.asList(0, 1, 2, 3), p.getVertexIndices());
    }

    @Test
    public void testRead_DuplicateVerticesInFace() {
        String obj = """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                f 1 2 1
                """;

        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.read(obj)
        );
        Assertions.assertTrue(ex.getMessage().contains("Polygon has duplicate vertices"));
    }

    @Test
    public void testRead_ComplexMixedFormats() {
        String obj = """
                # Mixed format file
                v 0.0 0.0 0.0
                v 1.0 0.0 0.0
                v 0.0 1.0 0.0
                
                vt 0.0 0.0
                vt 1.0 0.0
                vt 0.0 1.0
                
                vn 0.0 0.0 1.0
                vn 0.0 0.0 1.0
                vn 0.0 0.0 1.0
                
                # Different face formats
                f 1 2 3
                f 1/1 2/2 3/3
                f 1//1 2//2 3//3
                f 1/1/1 2/2/2 3/3/3
                """;

        Model m = ObjReader.read(obj);

        Assertions.assertEquals(3, m.getVertices().size());
        Assertions.assertEquals(3, m.getTextureVertices().size());
        Assertions.assertEquals(3, m.getNormals().size());
        Assertions.assertEquals(4, m.getPolygons().size());
    }

    @Test
    public void testRead_InvalidPolygonSize() {
        String obj = """
                v 0 0 0
                v 1 0 0
                f 1 2
                """;

        ObjReaderException ex = Assertions.assertThrows(
                ObjReaderException.class,
                () -> ObjReader.read(obj)
        );
        Assertions.assertTrue(ex.getMessage().contains("Polygon must have at least 3 vertices"));
    }

    @Test
    public void testRead_ValidModelWithValidation() {
        String obj = """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                f 1 2 3
                """;

        Model m = ObjReader.read(obj);

        // Дополнительная проверка валидности модели
        Assertions.assertTrue(m.getVertices().size() > 0);
        Assertions.assertTrue(m.getPolygons().size() > 0);

        // Проверка, что все индексы в полигонах корректны
        for (Polygon poly : m.getPolygons()) {
            for (int vertexIndex : poly.getVertexIndices()) {
                Assertions.assertTrue(vertexIndex >= 0 && vertexIndex < m.getVertices().size(),
                        "Invalid vertex index in polygon");
            }
        }
    }
}