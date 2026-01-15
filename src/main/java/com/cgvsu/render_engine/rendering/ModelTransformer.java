package com.cgvsu.render_engine.rendering;

import com.cgvsu.model.Model;
import com.cgvsu.render_engine.Transform;
import com.cgvsu.utils.math.Matrix4f;
import com.cgvsu.utils.math.Vector3f;

import java.util.ArrayList;

import static com.cgvsu.render_engine.GraphicConveyor.multiplyMatrix4ByVector3;

public class ModelTransformer {

    public static Model applyTransform(Model original, Transform t) {
        if (original == null || t == null) return null;

        Matrix4f modelMatrix = com.cgvsu.render_engine.GraphicConveyor.createModelMatrix(
                t.scaleX, t.scaleY, t.scaleZ,
                t.rotateX, t.rotateY, t.rotateZ,
                t.translateX, t.translateY, t.translateZ
        );

        Model transformed = new Model();
        ArrayList<Vector3f> verts = new ArrayList<>();
        for (Vector3f v : original.getVertices()) {
            verts.add(multiplyMatrix4ByVector3(modelMatrix, v));
        }

        transformed.setVertices(verts);
        transformed.setTextureVertices(new ArrayList<>(original.getTextureVertices()));
        transformed.setNormals(new ArrayList<>(original.getNormals()));
        transformed.setPolygons(new ArrayList<>(original.getPolygons()));
        transformed.setTexture(original.getTexture());

        return transformed;
    }
}
