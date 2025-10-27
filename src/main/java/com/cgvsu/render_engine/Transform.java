package com.cgvsu.render_engine;

public class Transform {
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float scaleZ = 1.0f;
    private float rotateX = 0.0f;
    private float rotateY = 0.0f;
    private float rotateZ = 0.0f;
    private float translateX = 0.0f;
    private float translateY = 0.0f;
    private float translateZ = 0.0f;

    // Геттеры
    public float getScaleX() { return scaleX; }
    public float getScaleY() { return scaleY; }
    public float getScaleZ() { return scaleZ; }
    public float getRotateX() { return rotateX; }
    public float getRotateY() { return rotateY; }
    public float getRotateZ() { return rotateZ; }
    public float getTranslateX() { return translateX; }
    public float getTranslateY() { return translateY; }
    public float getTranslateZ() { return translateZ; }

    // Сеттеры
    public void setScaleX(float scaleX) { this.scaleX = scaleX; }
    public void setScaleY(float scaleY) { this.scaleY = scaleY; }
    public void setScaleZ(float scaleZ) { this.scaleZ = scaleZ; }
    public void setRotateX(float rotateX) { this.rotateX = rotateX; }
    public void setRotateY(float rotateY) { this.rotateY = rotateY; }
    public void setRotateZ(float rotateZ) { this.rotateZ = rotateZ; }
    public void setTranslateX(float translateX) { this.translateX = translateX; }
    public void setTranslateY(float translateY) { this.translateY = translateY; }
    public void setTranslateZ(float translateZ) { this.translateZ = translateZ; }

    public Transform() {}

    public Transform(float scaleX, float scaleY, float scaleZ,
                     float rotateX, float rotateY, float rotateZ,
                     float translateX, float translateY, float translateZ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
    }
}