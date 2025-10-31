package com.cgvsu.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Polygon {
    private final List<Integer> vertexIndices;
    private final List<Integer> textureVertexIndices;
    private final List<Integer> normalIndices;

    private Polygon(List<Integer> vertexIndices, List<Integer> textureVertexIndices, List<Integer> normalIndices) {
        this.vertexIndices = vertexIndices != null ?
                Collections.unmodifiableList(new ArrayList<>(vertexIndices)) : Collections.emptyList();
        this.textureVertexIndices = textureVertexIndices != null ?
                Collections.unmodifiableList(new ArrayList<>(textureVertexIndices)) : Collections.emptyList();
        this.normalIndices = normalIndices != null ?
                Collections.unmodifiableList(new ArrayList<>(normalIndices)) : Collections.emptyList();
    }

    public static class Builder {
        private List<Integer> vertexIndices = new ArrayList<>();
        private List<Integer> textureVertexIndices = new ArrayList<>();
        private List<Integer> normalIndices = new ArrayList<>();

        public Builder addVertexIndex(int index) {
            this.vertexIndices.add(index);
            return this;
        }

        public Builder addTextureVertexIndex(int index) {
            this.textureVertexIndices.add(index);
            return this;
        }

        public Builder addNormalIndex(int index) {
            this.normalIndices.add(index);
            return this;
        }

        public Builder setVertexIndices(List<Integer> indices) {
            this.vertexIndices = new ArrayList<>(indices);
            return this;
        }

        public Builder setTextureVertexIndices(List<Integer> indices) {
            this.textureVertexIndices = new ArrayList<>(indices);
            return this;
        }

        public Builder setNormalIndices(List<Integer> indices) {
            this.normalIndices = new ArrayList<>(indices);
            return this;
        }

        public Polygon build() {
            return new Polygon(vertexIndices, textureVertexIndices, normalIndices);
        }
    }

    public List<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public List<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public List<Integer> getNormalIndices() {
        return normalIndices;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .setVertexIndices(vertexIndices)
                .setTextureVertexIndices(textureVertexIndices)
                .setNormalIndices(normalIndices);
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "vertices=" + vertexIndices.size() +
                ", textures=" + textureVertexIndices.size() +
                ", normals=" + normalIndices.size() +
                '}';
    }
}