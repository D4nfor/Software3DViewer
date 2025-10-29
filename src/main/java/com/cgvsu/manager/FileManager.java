package com.cgvsu.manager;

import com.cgvsu.model.Model;
import com.cgvsu.objtools.ObjReader;
import com.cgvsu.objtools.ObjWriter;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

// работа с файлами
public class FileManager {

    public void openModelFile(Window ownerWindow, Consumer<Model> onSuccess, Consumer<String> onError) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("3D Models (*.obj)", "*.obj")
        );
        fileChooser.setTitle("Open 3D Model");
        File file = fileChooser.showOpenDialog(ownerWindow);
        if (file != null) {
            loadModelFromFile(file, onSuccess, onError);
        }
    }

    public void saveModelFile(Window ownerWindow, Model model, Consumer<String> onSuccess, Consumer<String> onError) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("3D Models (*.obj)", "*.obj")
        );
        fileChooser.setTitle("Save 3D Model");
        fileChooser.setInitialFileName("model.obj");
        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file != null) {
            saveModelToFile(file, model, onSuccess, onError);
        }
    }
    
    private void loadModelFromFile(File file, Consumer<Model> onSuccess, Consumer<String> onError) {
        try {
            String fileContent = Files.readString(file.toPath());
            Model model = ObjReader.read(fileContent);
            onSuccess.accept(model);
        } catch (IOException exception) {
            onError.accept("Failed to load model: " + exception.getMessage());
        } catch (Exception exception) {
            onError.accept("Error parsing model: " + exception.getMessage());
        }
    }

    public void saveModelToFile(File file, Model model, Consumer<String> onSuccess, Consumer<String> onError) {
        try {
            if (!file.getName().toLowerCase().endsWith(".obj")) {
                file = new File(file.getAbsolutePath() + ".obj");
            }
            String content = ObjWriter.modelToString(model);
            Files.writeString(file.toPath(), content);
            onSuccess.accept("Model saved successfully");
        } catch (IOException exception) {
            onError.accept("Failed to save model: " + exception.getMessage());
        } catch (Exception exception) {
            onError.accept("Error saving model: " + exception.getMessage());
        }

    }
}