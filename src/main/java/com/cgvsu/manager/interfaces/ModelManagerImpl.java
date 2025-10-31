package com.cgvsu.manager.interfaces;

import com.cgvsu.model.Model;
import javafx.stage.Window;

public interface ModelManagerImpl {
    void openModelFile(Window window, ModelLoadCallback onSuccess, ModelErrorCallback onError);
    void saveModelFile(Window window, Model model, ModelSaveCallback onSuccess, ModelErrorCallback onError);
    
    @FunctionalInterface
    interface ModelLoadCallback {
        void onModelLoaded(Model model);
    }
    
    @FunctionalInterface
    interface ModelSaveCallback {
        void onModelSaved(String message);
    }
    
    @FunctionalInterface
    interface ModelErrorCallback {
        void onError(String errorMessage);
    }
}