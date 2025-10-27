package com.cgvsu.render_engine;

public class Transform {
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float scaleZ = 1.0f;
    public float rotateX = 0.0f;
    public float rotateY = 0.0f;
    public float rotateZ = 0.0f;
    public float translateX = 0.0f;
    public float translateY = 0.0f;
    public float translateZ = 0.0f;

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