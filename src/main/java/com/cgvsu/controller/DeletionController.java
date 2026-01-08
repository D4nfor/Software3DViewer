package com.cgvsu.controller;

import com.cgvsu.manager.SceneManager;
import com.cgvsu.manager.UIManager;
import com.cgvsu.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

public class DeletionController {

    @FXML private VBox deletionPanel;
    @FXML private Label verticesCountLabel;
    @FXML private Label polygonsCountLabel;
    @FXML private Button deleteSelectedBtn;
    @FXML private Button deleteUnusedBtn;
    @FXML private RadioButton vertexRadioButton;
    @FXML private RadioButton polygonRadioButton;
    @FXML private TextArea indicesInput;
    @FXML private Button parseIndicesBtn;
    @FXML private Button deleteIndicesBtn;
    @FXML private VBox indicesInfoBox;
    @FXML private Label selectedIndicesLabel;
    @FXML private Label indicesCountLabel;
    @FXML private Button selectAllBtn;
    @FXML private Button selectNoneBtn;
    @FXML private Label statusLabel;

    private final SceneManager sceneManager;
    private final UIManager uiManager;
    private Runnable onModelChanged;
    private Model currentModel;
    private ToggleGroup deleteTypeGroup;
    private Set<Integer> selectedIndices = new HashSet<>();

    public DeletionController(SceneManager sceneManager, UIManager uiManager) {
        this.sceneManager = sceneManager;
        this.uiManager = uiManager;
    }

    @FXML
    private void initialize() {
        setupUI();
        setupListeners();
        hidePanel();

        sceneManager.activeModelProperty().addListener((obs, oldModel, newModel) -> {
            setModel(newModel);
        });
    }

    private void setupUI() {
        deleteTypeGroup = new ToggleGroup();
        if (vertexRadioButton != null) {
            vertexRadioButton.setToggleGroup(deleteTypeGroup);
        }
        if (polygonRadioButton != null) {
            polygonRadioButton.setToggleGroup(deleteTypeGroup);
        }
        if (vertexRadioButton != null) {
            vertexRadioButton.setSelected(true);
        }

        updateStatistics();
    }

    private void setupListeners() {
        if (deleteSelectedBtn != null) {
            deleteSelectedBtn.setOnAction(e -> handleDeleteSelected());
        }
        if (deleteUnusedBtn != null) {
            deleteUnusedBtn.setOnAction(e -> handleDeleteUnused());
        }
        if (parseIndicesBtn != null) {
            parseIndicesBtn.setOnAction(e -> handleParseIndices());
        }
        if (deleteIndicesBtn != null) {
            deleteIndicesBtn.setOnAction(e -> handleDeleteIndices());
        }
        if (selectAllBtn != null) {
            selectAllBtn.setOnAction(e -> handleSelectAll());
        }
        if (selectNoneBtn != null) {
            selectNoneBtn.setOnAction(e -> handleSelectNone());
        }
    }

    public void setOnModelChanged(Runnable callback) {
        this.onModelChanged = callback;
    }

    public void setModel(Model model) {
        this.currentModel = model;
        updateStatistics();
        clearSelection();
    }

    private void updateStatistics() {
        if (currentModel == null) {
            if (verticesCountLabel != null) {
                verticesCountLabel.setText("0");
            }
            if (polygonsCountLabel != null) {
                polygonsCountLabel.setText("0");
            }
            if (statusLabel != null) {
                statusLabel.setText("Модель не загружена");
            }
            return;
        }

        int verticesCount = currentModel.getVertices().size();
        int polygonsCount = currentModel.getPolygons().size();

        if (verticesCountLabel != null) {
            verticesCountLabel.setText(String.valueOf(verticesCount));
        }
        if (polygonsCountLabel != null) {
            polygonsCountLabel.setText(String.valueOf(polygonsCount));
        }

        if (indicesInput != null && vertexRadioButton != null) {
            boolean isVertexMode = vertexRadioButton.isSelected();
            String type = isVertexMode ? "вершин" : "полигонов";
            int maxIndex = isVertexMode ? verticesCount - 1 : polygonsCount - 1;

            if (maxIndex >= 0) {
                indicesInput.setPromptText(String.format("Индексы %s (0-%d). Пример: 1,2,5 или 0-5 или 1,3-5,10", type, maxIndex));
            }
        }
    }

    @FXML
    private void handleDeleteSelected() {
        if (currentModel == null) {
            showStatus("Модель не загружена", "error");
            return;
        }

        if (selectedIndices.isEmpty()) {
            showStatus("Нет выбранных индексов", "warning");
            return;
        }

        boolean isVertexMode = vertexRadioButton != null && vertexRadioButton.isSelected();
        String type = isVertexMode ? "вершин" : "полигонов";
        int deleted = deleteSelectedItems();

        if (deleted > 0) {
            showStatus(String.format("Удалено %d %s", deleted, type), "success");
            updateStatistics();
            clearSelection();
            notifyModelChanged();
        }
    }

    @FXML
    private void handleDeleteUnused() {
        if (currentModel == null) {
            showStatus("Модель не загружена", "error");
            return;
        }

        int deleted = currentModel.deleteUnusedVertices();
        showStatus(String.format("Удалено %d неиспользуемых вершин", deleted), "success");
        updateStatistics();
        notifyModelChanged();
    }

    @FXML
    private void handleParseIndices() {
        if (indicesInput == null) return;

        String input = indicesInput.getText().trim();
        if (input.isEmpty()) {
            showStatus("Введите индексы", "warning");
            return;
        }

        try {
            selectedIndices = parseIndices(input);
            updateIndicesInfo();
            showStatus("Индексы успешно выбраны", "success");
        } catch (Exception e) {
            showStatus("Ошибка в формате индексов: " + e.getMessage(), "error");
            selectedIndices.clear();
            updateIndicesInfo();
        }
    }

    @FXML
    private void handleDeleteIndices() {
        if (selectedIndices.isEmpty()) {
            showStatus("Нет индексов", "warning");
            return;
        }

        if (currentModel == null) {
            showStatus("Модель не загружена", "error");
            return;
        }

        boolean isVertexMode = vertexRadioButton != null && vertexRadioButton.isSelected();
        String type = isVertexMode ? "вершин" : "полигонов";
        int maxIndex = isVertexMode ?
                currentModel.getVertices().size() - 1 :
                currentModel.getPolygons().size() - 1;

        for (int index : selectedIndices) {
            if (index < 0 || index > maxIndex) {
                showStatus(String.format("Индекс %d выходит за пределы (0-%d)", index, maxIndex), "error");
                return;
            }
        }

        int deleted = deleteSelectedItems();
        if (deleted > 0) {
            showStatus(String.format("Удалено %d %s", deleted, type), "success");
            updateStatistics();
            clearSelection();
            notifyModelChanged();
        }
    }

    @FXML
    private void handleSelectAll() {
        if (currentModel == null) {
            showStatus("Модель не загружена", "error");
            return;
        }

        boolean isVertexMode = vertexRadioButton != null && vertexRadioButton.isSelected();
        String type = isVertexMode ? "вершин" : "полигонов";
        int maxIndex = isVertexMode ?
                currentModel.getVertices().size() - 1 :
                currentModel.getPolygons().size() - 1;

        selectedIndices.clear();
        for (int i = 0; i <= maxIndex; i++) {
            selectedIndices.add(i);
        }

        if (indicesInput != null) {
            indicesInput.setText("0-" + maxIndex);
        }
        updateIndicesInfo();
        showStatus(String.format("Выбраны все %d %s", selectedIndices.size(), type), "info");
    }

    @FXML
    private void handleSelectNone() {
        selectedIndices.clear();
        if (indicesInput != null) {
            indicesInput.clear();
        }
        updateIndicesInfo();
        showStatus("Выбор сброшен", "info");
    }

    private Set<Integer> parseIndices(String input) {
        Set<Integer> indices = new HashSet<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            if (part.contains("-")) {
                String[] range = part.split("-");
                if (range.length != 2) {
                    throw new IllegalArgumentException("Неверный формат диапазона: " + part);
                }

                int start = Integer.parseInt(range[0].trim());
                int end = Integer.parseInt(range[1].trim());

                if (start > end) {
                    throw new IllegalArgumentException("Начало диапазона больше конца: " + part);
                }

                for (int i = start; i <= end; i++) {
                    indices.add(i);
                }
            } else {
                indices.add(Integer.parseInt(part));
            }
        }

        return indices;
    }

    private int deleteSelectedItems() {
        if (selectedIndices.isEmpty() || currentModel == null) {
            return 0;
        }

        int deleted = 0;
        boolean isVertexMode = vertexRadioButton != null && vertexRadioButton.isSelected();

        if (isVertexMode) {
            List<Integer> indicesList = new ArrayList<>(selectedIndices);
            indicesList.sort(Collections.reverseOrder());
            deleted = currentModel.deleteVertices(indicesList);
        } else {
            List<Integer> indicesList = new ArrayList<>(selectedIndices);
            indicesList.sort(Collections.reverseOrder());
            deleted = currentModel.deletePolygons(indicesList);
        }

        return deleted;
    }

    private void updateIndicesInfo() {
        if (indicesInfoBox == null || selectedIndices.isEmpty()) {
            if (indicesInfoBox != null) {
                indicesInfoBox.setVisible(false);
            }
            return;
        }

        indicesInfoBox.setVisible(true);

        List<Integer> sortedIndices = new ArrayList<>(selectedIndices);
        Collections.sort(sortedIndices);

        String indicesText;
        if (sortedIndices.size() <= 10) {
            indicesText = sortedIndices.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
        } else {
            indicesText = sortedIndices.get(0) + ", " + sortedIndices.get(1) + ", ..., " +
                    sortedIndices.get(sortedIndices.size() - 1);
        }

        if (selectedIndicesLabel != null) {
            selectedIndicesLabel.setText(indicesText);
        }
        if (indicesCountLabel != null) {
            indicesCountLabel.setText(String.format("Всего: %d элементов", selectedIndices.size()));
        }
    }

    private void clearSelection() {
        selectedIndices.clear();
        if (indicesInput != null) {
            indicesInput.clear();
        }
        updateIndicesInfo();
    }

    private void showStatus(String message, String type) {
        if (statusLabel == null) return;

        statusLabel.setText(message);

        // Устанавливаем CSS класс вместо изменения стиля напрямую
        statusLabel.getStyleClass().removeAll("status-success", "status-error", "status-warning", "status-info");

        switch (type) {
            case "success":
                statusLabel.getStyleClass().add("status-success");
                break;
            case "error":
                statusLabel.getStyleClass().add("status-error");
                break;
            case "warning":
                statusLabel.getStyleClass().add("status-warning");
                break;
            default:
                statusLabel.getStyleClass().add("status-info");
        }
    }

    private void notifyModelChanged() {
        if (onModelChanged != null) {
            onModelChanged.run();
        }
    }

    public void showPanel() {
        if (deletionPanel != null) {
            deletionPanel.setVisible(true);
            deletionPanel.setManaged(true);
            if (currentModel != null) {
                updateStatistics();
            }
        }
    }

    public void hidePanel() {
        if (deletionPanel != null) {
            deletionPanel.setVisible(false);
            deletionPanel.setManaged(false);
        }
    }
}