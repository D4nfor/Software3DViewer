package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.UIManager;
import com.cgvsu.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ModelsController {

    @FXML private VBox modelsPanel;
    @FXML private ListView<Model> modelsList;
    @FXML private Button deleteButton;
    @FXML private Label activeModelLabel;

    private final SceneManager sceneManager;
    private final UIManager uiManager;

    public ModelsController(SceneManager sceneManager, UIManager uiManager) {
        this.sceneManager = sceneManager;
        this.uiManager = uiManager;
    }

    @FXML
    private void initialize() {
        modelsList.setItems(sceneManager.getModels());

        modelsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Model model, boolean empty) {
                super.updateItem(model, empty);
                if (empty || model == null) {
                    setText(null);
                } else {
                    setText(model.getName());
                }
            }
        });

        modelsList.getSelectionModel().selectedItemProperty().addListener((obs, oldM, newM) -> {
            if (newM != null) {
                sceneManager.setActiveModel(newM);
            }
        });

        sceneManager.activeModelProperty().addListener((obs, o, n) -> updateActiveLabel(n));

        updateActiveLabel(sceneManager.getActiveModel());
        hidePanel();
    }

    @FXML
    private void handleDeleteModel() {
        Model selected = modelsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            sceneManager.removeModel(selected);
        }
    }

    private void updateActiveLabel(Model model) {
        if (model == null) {
            activeModelLabel.setText("Активная модель: нет");
        } else {
            activeModelLabel.setText("Активная модель: " + sceneManager.getActiveModel().getName());
            modelsList.getSelectionModel().select(model);
        }
    }

    public void showPanel() {
        modelsPanel.setVisible(true);
        modelsPanel.setManaged(true);
    }

    public void hidePanel() {
        modelsPanel.setVisible(false);
        modelsPanel.setManaged(false);
    }
}
