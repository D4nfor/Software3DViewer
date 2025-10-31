package com.cgvsu.render_engine;

import com.cgvsu.utils.math.Matrix4f;
import com.cgvsu.utils.math.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f target;
    private Vector3f up;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.up = new Vector3f(0, 1, 0);
    }

    public void moveForward(float distance) {
        Vector3f direction = getForwardDirection();
        position = position.add(direction.multiply(distance));
        target = target.add(direction.multiply(distance));
    }

    public void moveBackward(float distance) {
        Vector3f direction = getForwardDirection();
        position = position.subtract(direction.multiply(distance));
        target = target.subtract(direction.multiply(distance));
    }

    public void moveRight(float distance) {
        Vector3f direction = getRightDirection();
        position = position.add(direction.multiply(distance));
        target = target.add(direction.multiply(distance));
    }

    public void moveLeft(float distance) {
        Vector3f direction = getRightDirection();
        position = position.subtract(direction.multiply(distance));
        target = target.subtract(direction.multiply(distance));
    }

    public void moveUp(float distance) {
        position = position.add(up.multiply(distance));
        target = target.add(up.multiply(distance));
    }

    public void moveDown(float distance) {
        position = position.subtract(up.multiply(distance));
        target = target.subtract(up.multiply(distance));
    }

    public void rotateHorizontal(float angleDegrees) {
        Vector3f dir = target.subtract(position);
        Matrix4f rotation = GraphicConveyor.rotateY((float)Math.toRadians(angleDegrees));
        Vector3f newDir = GraphicConveyor.multiplyMatrix4ByVector3(rotation, dir);
        target = position.add(newDir);
    }

    public void rotateVertical(float angleDegrees) {
        Vector3f dir = target.subtract(position);
        Vector3f right = getRightDirection();
        Matrix4f rotation = createRotationAroundAxis(right, (float)Math.toRadians(angleDegrees));
        Vector3f newDir = GraphicConveyor.multiplyMatrix4ByVector3(rotation, dir);
        target = position.add(newDir);
    }

    public void zoom(float distance) {
        Vector3f direction = getFullForwardDirection();
        float length = target.subtract(position).length();
        float minDistance = 0.1f;

        if (length - distance > minDistance) {
            position = position.add(direction.multiply(distance));
        } else {
            position = target.subtract(direction.multiply(minDistance));
        }
    }


    public void orbitHorizontal(float angleDegrees) {
        Vector3f toCamera = position.subtract(target);
        Matrix4f rotation = GraphicConveyor.rotateY((float)Math.toRadians(angleDegrees));
        Vector3f newToCamera = GraphicConveyor.multiplyMatrix4ByVector3(rotation, toCamera);
        position = target.add(newToCamera);
    }

    public void orbitVertical(float angleDegrees) {
        Vector3f toCamera = position.subtract(target);
        Vector3f right = getRightDirection();
        Matrix4f rotation = createRotationAroundAxis(right, (float)Math.toRadians(angleDegrees));
        Vector3f newToCamera = GraphicConveyor.multiplyMatrix4ByVector3(rotation, toCamera);
        float dot = newToCamera.normalize().dot(new Vector3f(0, 1, 0));
        if (dot < 0.95f && dot > -0.95f) {
            position = target.add(newToCamera);
        }
    }

    private Matrix4f createRotationAroundAxis(Vector3f axis, float angle) {
        float x = axis.getX(), y = axis.getY(), z = axis.getZ();
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        float oneMinusCos = 1 - cos;

        float[][] data = {
                {cos + x*x*oneMinusCos,     x*y*oneMinusCos - z*sin, x*z*oneMinusCos + y*sin, 0},
                {y*x*oneMinusCos + z*sin,   cos + y*y*oneMinusCos,   y*z*oneMinusCos - x*sin, 0},
                {z*x*oneMinusCos - y*sin,   z*y*oneMinusCos + x*sin, cos + z*z*oneMinusCos,   0},
                {0, 0, 0, 1}
        };

        return new Matrix4f(data);
    }

    private Vector3f getForwardDirection() {
        Vector3f direction = target.subtract(position);
        return new Vector3f(direction.getX(), 0, direction.getZ()).normalize();
    }

    private Vector3f getFullForwardDirection() {
        Vector3f direction = target.subtract(position);
        if (direction.length() < 1e-6f) {
            return new Vector3f(0, 0, -1);
        }
        return direction.normalize();
    }

    private Vector3f getRightDirection() {
        Vector3f forward = getForwardDirection();
        return forward.cross(up).normalize();
    }


    public void reset(Vector3f newPosition, Vector3f newTarget) {
        this.position = newPosition;
        this.target = newTarget;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public Matrix4f getViewMatrix() {
        return GraphicConveyor.lookAt(position, target, up);
    }

    public Matrix4f getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }
}