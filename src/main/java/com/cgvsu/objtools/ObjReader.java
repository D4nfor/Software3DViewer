package com.cgvsu.objtools;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
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
        Scanner scanner = new Scanner(fileContent);
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            ArrayList<String> wordsInLine = new ArrayList<String>(Arrays.asList(line.split("\\s+")));
            if (wordsInLine.isEmpty()) {
                continue;
            }

            final String token = wordsInLine.get(0);
            wordsInLine.remove(0);

            ++lineInd;
            switch (token) {
                // Для структур типа вершин методы написаны так, чтобы ничего не знать о внешней среде.
                // Они принимают только то, что им нужно для работы, а возвращают только то, что могут создать.
                // Исключение - индекс строки. Он прокидывается, чтобы выводить сообщение об ошибке.
                // Могло быть иначе. Например, метод parseVertex мог вместо возвращения вершины принимать вектор вершин
                // модели или сам класс модели, работать с ним.
                // Но такой подход может привести к большему количеству ошибок в коде. Например, в нем что-то может
                // тайно сделаться с классом модели.
                // А еще это портит читаемость
                // И не стоит забывать про тесты. Чем проще вам задать данные для теста, проверить, что метод рабочий,
                // тем лучше.
                case OBJ_VERTEX_TOKEN -> result.getVertices().add(parseVertex(wordsInLine, lineInd));
                case OBJ_TEXTURE_TOKEN -> result.getTextureVertices().add(parseTextureVertex(wordsInLine, lineInd));
                case OBJ_NORMAL_TOKEN -> result.getNormals().add(parseNormal(wordsInLine, lineInd));
                case OBJ_FACE_TOKEN -> result.getPolygons().add(parseFace(wordsInLine, lineInd));
                default -> {}
            }
        }

        return result;
    }

    // Всем методам кроме основного я поставил модификатор доступа protected, чтобы обращаться к ним в тестах
    protected static Vector3f parseVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        try {
            return new Vector3f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)),
                    Float.parseFloat(wordsInLineWithoutToken.get(2)));

        } catch(NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);

        } catch(IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few vertex arguments.", lineInd);
        }
    }

    protected static Vector2f parseTextureVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        try {
            return new Vector2f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)));

        } catch(NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);

        } catch(IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few texture vertex arguments.", lineInd);
        }
    }

    protected static Vector3f parseNormal(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        try {
            return new Vector3f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)),
                    Float.parseFloat(wordsInLineWithoutToken.get(2)));

        } catch(NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);

        } catch(IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few normal arguments.", lineInd);
        }
    }

    protected static Polygon parseFace(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        // Создаем билдер для полигона
        Polygon.Builder builder = Polygon.builder();

        for (String vertexStr : wordsInLineWithoutToken) {
            parseFaceWord(vertexStr, builder, lineInd);
        }

        // Проверяем, что есть хотя бы 3 вершины
        Polygon polygon = builder.build();
        if (polygon.getVertexIndices().size() < 3) {
            throw new ObjReaderException("Polygon must have at least 3 vertices.", lineInd);
        }

        return polygon;
    }

    protected static void parseFaceWord(String vertexStr, Polygon.Builder builder, int lineInd) {
        String[] vertexData = vertexStr.split("/");

        try {
            // Индекс вершины (обязательный)
            if (vertexData.length > 0 && !vertexData[0].isEmpty()) {
                int vertexIndex = Integer.parseInt(vertexData[0]) - 1; // OBJ uses 1-based indexing
                builder.addVertexIndex(vertexIndex);
            } else {
                throw new ObjReaderException("Vertex index is missing.", lineInd);
            }

            // Текстурные координаты (опционально)
            if (vertexData.length > 1 && !vertexData[1].isEmpty()) {
                int textureIndex = Integer.parseInt(vertexData[1]) - 1;
                builder.addTextureVertexIndex(textureIndex);
            }

            // Нормали (опционально)
            if (vertexData.length > 2 && !vertexData[2].isEmpty()) {
                int normalIndex = Integer.parseInt(vertexData[2]) - 1;
                builder.addNormalIndex(normalIndex);
            }
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse face element: " + vertexStr, lineInd);
        }
    }

    // Обратите внимание, что для чтения полигонов я выделил еще один вспомогательный метод.
    // Это бывает очень полезно и с точки зрения структурирования алгоритма в голове, и с точки зрения тестирования.
    // В радикальных случаях не бойтесь выносить в отдельные методы и тестировать код из одной-двух строчек.
    protected static void parseFaceWord(
            String wordInLine,
            ArrayList<Integer> onePolygonVertexIndices,
            ArrayList<Integer> onePolygonTextureVertexIndices,
            ArrayList<Integer> onePolygonNormalIndices,
            int lineInd) {
        try {
            String[] wordIndices = wordInLine.split("/");
            switch (wordIndices.length) {
                case 1 -> {
                    // f v1 v2 v3
                    onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                }
                case 2 -> {
                    // f v1/vt1 v2/vt2 v3/vt3
                    onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                    onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) - 1);
                }
                case 3 -> {
                    // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
                    //  f v1//vn1 v2//vn2 v3//vn3
                    onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);

                    if (!wordIndices[1].isEmpty()) {
                        onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) - 1);
                    }

                    if (!wordIndices[2].isEmpty()) {
                        onePolygonNormalIndices.add(Integer.parseInt(wordIndices[2]) - 1);
                    }
                }
                default ->
                        throw new ObjReaderException("Invalid element size.", lineInd);
            }

        } catch(NumberFormatException e) {
            throw new ObjReaderException("Failed to parse int value.", lineInd);

        } catch(IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few arguments.", lineInd);
        }
    }
}