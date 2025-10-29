package com.cgvsu.manager;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class FileManager {
    private FileChooser fileChooser;
    
    public FileManager() {
        initializeFileChooser();
    }
    
    private void initializeFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("3D Models (*.obj)", "*.obj")
        );
        fileChooser.setTitle("Load 3D Model");
    }
    
    public void openModelFile(Window ownerWindow, Consumer<Model> onSuccess, Consumer<String> onError) {
        File file = fileChooser.showOpenDialog(ownerWindow);
        if (file != null) {
            loadModelFromFile(file, onSuccess, onError);
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
}