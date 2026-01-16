package com.cgvsu.render_engine.rendering;

import javafx.scene.paint.Color;

public class RenderSettings {
    private boolean wireframe = false;
    private boolean useTexture = false;
    private boolean useLighting = false;
    private Color baseColor = Color.GRAY;

    // wireframe
    public boolean isWireframe() {
        return wireframe;
    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    // useTexture
    public boolean isUseTexture() {
        return useTexture;
    }

    public void setUseTexture(boolean useTexture) {
        this.useTexture = useTexture;
    }

    // useLighting
    public boolean isUseLighting() {
        return useLighting;
    }

    public void setUseLighting(boolean useLighting) {
        this.useLighting = useLighting;
    }

    // baseColor
    public Color getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor = baseColor;
    }
}
