package com.cgvsu.model;
import com.cgvsu.utils.math.Vector2f;
import com.cgvsu.utils.math.Vector3f;

import java.util.*;

public class Model {

    private ArrayList<Vector3f> vertices;
    private ArrayList<Vector2f> textureVertices;
    private ArrayList<Vector3f> normals;
    private ArrayList<Polygon> polygons;

    public Model() {
        this.vertices = new ArrayList<>();
        this.textureVertices = new ArrayList<>();
        this.normals = new ArrayList<>();
        this.polygons = new ArrayList<>();
    }

    public ArrayList<Vector3f> getVertices() {return vertices;}
    public void setVertices(ArrayList<Vector3f> vertices) {
        this.vertices = vertices != null ? vertices : new ArrayList<>();
    }

    public ArrayList<Vector2f> getTextureVertices() {return textureVertices;}
    public void setTextureVertices(ArrayList<Vector2f> textureVertices) {
        this.textureVertices = textureVertices != null ? textureVertices : new ArrayList<>();
    }

    public ArrayList<Vector3f> getNormals() {return normals;}
    public void setNormals(ArrayList<Vector3f> normals) {
        this.normals = normals != null ? normals : new ArrayList<>();
    }

    public ArrayList<Polygon> getPolygons() {return polygons;}
    public void setPolygons(ArrayList<Polygon> polygons) {
        this.polygons = polygons != null ? polygons : new ArrayList<>();
    }
}