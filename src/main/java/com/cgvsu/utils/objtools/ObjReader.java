package com.cgvsu.utils.objtools;

import com.cgvsu.utils.math.Vector2f;
import com.cgvsu.utils.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ObjReader {

    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";

    public static Model read(String fileContent) {
        Model result = new Model();

        int lineInd = 0;
        Scanner scanner = new Scanner(removeBOM(fileContent));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            lineInd++;

            if (line.isEmpty() || line.startsWith("#")) continue;

            ArrayList<String> words = new ArrayList<>(Arrays.asList(line.split("\\s+")));
            if (words.isEmpty()) continue;

            String token = words.remove(0);

            try {
                switch (token) {
                    case OBJ_VERTEX_TOKEN ->
                            result.getVertices().add(parseVertex(words, lineInd));
                    case OBJ_TEXTURE_TOKEN ->
                            result.getTextureVertices().add(parseTextureVertex(words, lineInd));
                    case OBJ_NORMAL_TOKEN ->
                            result.getNormals().add(parseNormal(words, lineInd));
                    case OBJ_FACE_TOKEN ->
                            result.getPolygons().add(parseFace(words, lineInd, result));
                    default -> {
                        // Игнорируем неизвестные токены
                    }
                }
            } catch (ObjReaderException e) {
                throw new ObjReaderException(e.getMessage() + " at line " + lineInd, lineInd);
            }
        }

        validateModel(result);

        return result;
    }

    protected static Vector3f parseVertex(ArrayList<String> words, int lineInd) {
        if (words.size() < 3)
            throw new ObjReaderException("Vertex must contain at least 3 numbers.", lineInd);

        if (words.size() > 3) {
            // Некоторые OBJ файлы содержат 4-ю координату w (однородная координата)
            // Игнорируем её для совместимости
            words = new ArrayList<>(words.subList(0, 3));
        }

        try {
            return new Vector3f(
                    Float.parseFloat(words.get(0)),
                    Float.parseFloat(words.get(1)),
                    Float.parseFloat(words.get(2))
            );
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value in vertex.", lineInd);
        }
    }

    protected static Vector2f parseTextureVertex(ArrayList<String> words, int lineInd) {
        if (words.size() < 2)
            throw new ObjReaderException("Texture vertex must contain at least 2 numbers.", lineInd);

        if (words.size() > 2) {
            // Некоторые OBJ файлы содержат 3-ю координату для текстур
            // Игнорируем её для совместимости
            words = new ArrayList<>(words.subList(0, 2));
        }

        try {
            return new Vector2f(
                    Float.parseFloat(words.get(0)),
                    Float.parseFloat(words.get(1))
            );
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value in texture vertex.", lineInd);
        }
    }

    protected static Vector3f parseNormal(ArrayList<String> words, int lineInd) {
        if (words.size() != 3)
            throw new ObjReaderException("Normal must contain exactly 3 numbers.", lineInd);

        try {
            return new Vector3f(
                    Float.parseFloat(words.get(0)),
                    Float.parseFloat(words.get(1)),
                    Float.parseFloat(words.get(2))
            );
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value in normal.", lineInd);
        }
    }

    protected static Polygon parseFace(
            ArrayList<String> words,
            int lineInd,
            Model result
    ) {
        if (words.size() < 3)
            throw new ObjReaderException("Polygon must have at least 3 vertices.", lineInd);

        ArrayList<Integer> vertexIndices = new ArrayList<>();
        ArrayList<Integer> textureIndices = new ArrayList<>();
        ArrayList<Integer> normalIndices = new ArrayList<>();

        boolean hasTexture = false;
        boolean hasNormal = false;
        boolean firstIteration = true;

        for (String word : words) {
            FaceTriple triple = parseFaceWord(word, lineInd);

            // Проверяем согласованность: все вершины должны иметь одинаковый формат
            if (firstIteration) {
                hasTexture = (triple.vt != null);
                hasNormal = (triple.vn != null);
                firstIteration = false;
            } else {
                if (hasTexture != (triple.vt != null)) {
                    throw new ObjReaderException("Inconsistent face format: texture coordinates must be present for all vertices or absent for all.", lineInd);
                }
                if (hasNormal != (triple.vn != null)) {
                    throw new ObjReaderException("Inconsistent face format: normals must be present for all vertices or absent for all.", lineInd);
                }
            }

            vertexIndices.add(resolveIndex(triple.v, result.getVertices().size(), lineInd, "vertex"));

            if (hasTexture) {
                textureIndices.add(resolveIndex(triple.vt, result.getTextureVertices().size(), lineInd, "texture"));
            }

            if (hasNormal) {
                normalIndices.add(resolveIndex(triple.vn, result.getNormals().size(), lineInd, "normal"));
            }
        }

        // Проверяем, что полигон не дегенеративный (все вершины разные)
        if (vertexIndices.size() != vertexIndices.stream().distinct().count()) {
            throw new ObjReaderException("Polygon has duplicate vertices.", lineInd);
        }

        Polygon.Builder builder = Polygon.builder();
        builder.setVertexIndices(vertexIndices);

        if (hasTexture) builder.setTextureVertexIndices(textureIndices);
        if (hasNormal) builder.setNormalIndices(normalIndices);

        return builder.build();
    }

    protected static class FaceTriple {
        Integer v, vt, vn;

        FaceTriple(Integer v, Integer vt, Integer vn) {
            this.v = v;
            this.vt = vt;
            this.vn = vn;
        }
    }

    protected static FaceTriple parseFaceWord(String word, int lineInd) {
        String[] arr = word.split("/", -1);

        if (arr.length < 1 || arr.length > 3)
            throw new ObjReaderException("Invalid face element format: '" + word + "'. Expected format: v/vt/vn, v//vn, v/vt/, or v", lineInd);

        Integer v = parseIndex(arr[0], lineInd, "vertex");
        if (v == 0) {
            throw new ObjReaderException("Vertex index cannot be zero.", lineInd);
        }

        Integer vt = null;
        Integer vn = null;

        if (arr.length >= 2) {
            if (!arr[1].isEmpty()) {
                vt = parseIndex(arr[1], lineInd, "texture");
            }
            // Если строка пустая, оставляем null
        }

        if (arr.length == 3) {
            if (!arr[2].isEmpty()) {
                vn = parseIndex(arr[2], lineInd, "normal");
            }
            // Если строка пустая, оставляем null
        }

        return new FaceTriple(v, vt, vn);
    }

    protected static Integer parseIndex(String s, int lineInd, String type) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse " + type + " index: '" + s + "'", lineInd);
        }
    }

    protected static int resolveIndex(int index, int listSize, int lineInd, String type) {
        if (index == 0) {
            throw new ObjReaderException(type + " index cannot be zero.", lineInd);
        }

        if (listSize == 0) {
            throw new ObjReaderException("Face references " + type + " data that does not exist.", lineInd);
        }

        if (index > 0) {
            int zeroBased = index - 1;
            if (zeroBased >= listSize) {
                throw new ObjReaderException(type + " index out of range: " + index + " (max: " + listSize + ")", lineInd);
            }
            return zeroBased;
        }

        // Отрицательный индекс
        int resolved = listSize + index;  // index отрицательный
        if (resolved < 0) {
            throw new ObjReaderException("Relative " + type + " index out of range: " + index + " (would resolve to: " + resolved + ")", lineInd);
        }

        return resolved;
    }

    protected static void validateModel(Model m) {
        if (m.getVertices().isEmpty())
            throw new ObjReaderException("OBJ file contains no vertices.", 0);
        if (m.getPolygons().isEmpty())
            throw new ObjReaderException("OBJ file contains no faces.", 0);

        // Дополнительная проверка: все полигоны должны ссылаться на существующие вершины
        int vertexCount = m.getVertices().size();
        for (int i = 0; i < m.getPolygons().size(); i++) {
            Polygon poly = m.getPolygons().get(i);
            for (int vertexIndex : poly.getVertexIndices()) {
                if (vertexIndex < 0 || vertexIndex >= vertexCount) {
                    throw new ObjReaderException("Polygon " + i + " references non-existent vertex: " + vertexIndex, 0);
                }
            }
        }
    }

    private static String removeBOM(String text) {
        if (text.startsWith("\uFEFF")) {
            return text.substring(1);
        }
        return text;
    }
}