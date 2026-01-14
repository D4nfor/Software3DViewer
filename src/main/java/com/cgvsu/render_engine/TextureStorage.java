package com.cgvsu.render_engine;

import javafx.scene.image.Image;

public class TextureStorage {
    private static Image texture;

    public static void setTexture(Image img) {
        texture = img;
    }

    public static Image getTexture() {
        return texture;
    }
}
