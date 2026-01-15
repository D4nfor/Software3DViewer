package com.cgvsu.model;

import com.cgvsu.render_engine.transform.Transform;
import com.cgvsu.utils.math.Vector2f;
import com.cgvsu.utils.math.Vector3f;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.util.*;
import java.util.List;

public class Model {
    private ArrayList<Vector3f> vertices;
    private ArrayList<Vector2f> textureVertices;
    private ArrayList<Vector3f> normals;
    private ArrayList<Polygon> polygons;
    private String name;
    private final ObjectProperty<Transform> transform = new SimpleObjectProperty<>(new Transform());
    private Image texture;

    public ObjectProperty<Transform> transformProperty() {
        return transform;
    }

    public Transform getTransform() {
        return transform.get();
    }

    public void setTransform(Transform transform) {
        this.transform.set(transform != null ? transform : new Transform());
    }

    // Конструктор копирования — создаёт глубокую копию другой модели
    public Model(Model other) {
        this.vertices = new ArrayList<>();
        for (Vector3f v : other.vertices) {
            this.vertices.add(new Vector3f(v.getX(), v.getY(), v.getZ()));
        }

        this.textureVertices = new ArrayList<>();
        for (Vector2f tv : other.textureVertices) {
            this.textureVertices.add(new Vector2f(tv.getX(), tv.getY()));
        }

        this.normals = new ArrayList<>();
        for (Vector3f n : other.normals) {
            this.normals.add(new Vector3f(n.getX(), n.getY(), n.getZ()));
        }

        this.polygons = new ArrayList<>(other.polygons); // предполагаем, что Polygon можно копировать ссылкой
        this.name = other.name;
        this.transform.set(new Transform()); // создаём новый Transform
        this.texture = other.texture;
    }

    // Пустой конструктор — создаёт "пустую" модель
    public Model() {
        this.vertices = new ArrayList<>();
        this.textureVertices = new ArrayList<>();
        this.normals = new ArrayList<>();
        this.polygons = new ArrayList<>();
        this.transform.set(new Transform());
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Vector3f> getVertices() {return vertices;}
    public void setVertices(ArrayList<Vector3f> vertices) {
        this.vertices = vertices != null ? vertices : new ArrayList<>();
    }

    public ArrayList<Vector2f> getTextureVertices() {return textureVertices;}
    public void setTextureVertices(ArrayList<Vector2f> textureVertices) {
        this.textureVertices = textureVertices != null ? textureVertices : new ArrayList<>();
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public void setNormals(List<Vector3f> normals) {
        this.normals = normals != null ? new ArrayList<>(normals) : new ArrayList<>();
    }


    public ArrayList<Polygon> getPolygons() {return polygons;}
    public void setPolygons(ArrayList<Polygon> polygons) {
        this.polygons = polygons != null ? polygons : new ArrayList<>();
    }

    public boolean deletePolygon(int polygonIndex) {
        if (polygonIndex < 0 || polygonIndex >= polygons.size()) {
            return false;
        }
        polygons.remove(polygonIndex);
        return true;
    }

    public int deletePolygons(List<Integer> polygonIndices) {
        if (polygonIndices == null || polygonIndices.isEmpty()) {
            return 0;
        }

        List<Integer> sortedIndices = new ArrayList<>(new HashSet<>(polygonIndices));
        sortedIndices.sort(Collections.reverseOrder());

        int deletedCount = 0;
        for (int index : sortedIndices) {
            if (index >= 0 && index < polygons.size()) {
                polygons.remove(index);
                deletedCount++;
            }
        }
        return deletedCount;
    }

    public boolean deleteVertex(int vertexIndex) {
        if (vertexIndex < 0 || vertexIndex >= vertices.size()) {
            return false;
        }

        deletePolygonsContainingVertex(vertexIndex);

        vertices.remove(vertexIndex);

        for (Polygon polygon : polygons) {
            List<Integer> updatedIndices = new ArrayList<>();
            for (int idx : polygon.getVertexIndices()) {
                if (idx > vertexIndex) {
                    updatedIndices.add(idx - 1);
                } else {
                    updatedIndices.add(idx);
                }
            }

            Polygon updatedPolygon = createUpdatedPolygon(polygon, updatedIndices);
            polygons.set(polygons.indexOf(polygon), updatedPolygon);
        }

        return true;
    }

    public int deleteVertices(List<Integer> vertexIndices) {
        if (vertexIndices == null || vertexIndices.isEmpty()) {
            return 0;
        }

        List<Integer> sortedIndices = new ArrayList<>(new HashSet<>(vertexIndices));
        sortedIndices.sort(Collections.reverseOrder());

        for (int index : sortedIndices) {
            if (index < 0 || index >= vertices.size()) {
                throw new IllegalArgumentException("Invalid vertex index: " + index);
            }
        }

        int deletedCount = 0;

        for (int vertexIndex : sortedIndices) {
            deletePolygonsContainingVertex(vertexIndex);

            vertices.remove(vertexIndex);
            deletedCount++;

            updatePolygonIndicesAfterSingleDeletion(vertexIndex);
        }

        return deletedCount;
    }

    private void updatePolygonIndicesAfterSingleDeletion(int deletedIndex) {
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            List<Integer> updatedIndices = new ArrayList<>();

            for (int idx : polygon.getVertexIndices()) {
                if (idx > deletedIndex) {
                    updatedIndices.add(idx - 1);
                } else {
                    updatedIndices.add(idx);
                }
            }

            Polygon updatedPolygon = createUpdatedPolygon(polygon, updatedIndices);
            polygons.set(i, updatedPolygon);
        }
    }

    public int deleteUnusedVertices() {
        Set<Integer> usedVertexIndices = new HashSet<>();

        for (Polygon polygon : polygons) {
            usedVertexIndices.addAll(polygon.getVertexIndices());
        }

        List<Integer> unusedIndices = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (!usedVertexIndices.contains(i)) {
                unusedIndices.add(i);
            }
        }

        return deleteVertices(unusedIndices);
    }

    public int deletePolygonsContainingVertex(int vertexIndex) {
        List<Integer> polygonsToDelete = new ArrayList<>();

        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            if (polygon.getVertexIndices().contains(vertexIndex)) {
                polygonsToDelete.add(i);
            }
        }

        return deletePolygons(polygonsToDelete);
    }

    public int deletePolygonsContainingVertices(List<Integer> vertexIndices) {
        Set<Integer> vertexSet = new HashSet<>(vertexIndices);
        List<Integer> polygonsToDelete = new ArrayList<>();

        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            boolean containsAny = false;

            for (int vertexIdx : polygon.getVertexIndices()) {
                if (vertexSet.contains(vertexIdx)) {
                    containsAny = true;
                    break;
                }
            }

            if (containsAny) {
                polygonsToDelete.add(i);
            }
        }

        return deletePolygons(polygonsToDelete);
    }

    private Polygon createUpdatedPolygon(Polygon original, List<Integer> newVertexIndices) {
        Polygon.Builder builder = original.toBuilder()
                .setVertexIndices(newVertexIndices);

        if (!original.getTextureVertexIndices().isEmpty()) {
            builder.setTextureVertexIndices(new ArrayList<>(original.getTextureVertexIndices()));
        }

        if (!original.getNormalIndices().isEmpty()) {
            builder.setNormalIndices(new ArrayList<>(original.getNormalIndices()));
        }

        return builder.build();
    }

    public boolean validateModel() {
        for (Polygon polygon : polygons) {
            List<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() < 3) {
                return false;
            }

            for (int vertexIndex : vertexIndices) {
                if (vertexIndex < 0 || vertexIndex >= vertices.size()) {
                    return false;
                }
            }
        }
        return true;
    }

    public int cleanInvalidPolygons() {
        List<Polygon> validPolygons = new ArrayList<>();

        for (Polygon polygon : polygons) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            boolean isValid = true;

            if (vertexIndices.size() < 3) {
                isValid = false;
            }

            if (isValid) {
                for (int index : vertexIndices) {
                    if (index < 0 || index >= vertices.size()) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) {
                validPolygons.add(polygon);
            }
        }

        int removedCount = polygons.size() - validPolygons.size();
        polygons = new ArrayList<>(validPolygons);
        return removedCount;
    }

    public Set<Integer> getUsedVertices() {
        Set<Integer> usedVertices = new HashSet<>();
        for (Polygon polygon : polygons) {
            usedVertices.addAll(polygon.getVertexIndices());
        }
        return usedVertices;
    }

    public List<Integer> getUnusedVertices() {
        Set<Integer> usedVertices = getUsedVertices();
        List<Integer> unused = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            if (!usedVertices.contains(i)) {
                unused.add(i);
            }
        }

        return unused;
    }

    public void dropPolygonNormals() {
        for (Polygon p : polygons) {
            p.getNormalIndices().clear();
        }
    }

    public void setTexture(Image texture) {
        this.texture = texture;
    }

    public Image getTexture() {
        return texture;
    }

}