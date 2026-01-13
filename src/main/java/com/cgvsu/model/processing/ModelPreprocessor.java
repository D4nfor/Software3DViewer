package com.cgvsu.model.processing;

import com.cgvsu.model.Model;
import static com.cgvsu.model.processing.Triangulator.triangulate;

public class ModelPreprocessor {

    public static Model prepare(Model model) {
        return triangulate(model);
    }
}
