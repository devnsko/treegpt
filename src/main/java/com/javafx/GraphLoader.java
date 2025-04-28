package com.javafx;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GraphLoader {
    public static List<GraphNode> load(String fileName) {
        List<GraphNode> list = new ArrayList<>();
        try {
            String json = Files.readString(Paths.get(fileName));
            JsonArray arr = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                System.out.println(obj.get("title"));
                JsonObject pos = obj.getAsJsonObject("position");

                String id = obj.get("id").getAsString();
                String parent = obj.has("parent") && !obj.get("parent").isJsonNull()
                                ? obj.get("parent").getAsString()
                                : null;
                int cluster = obj.get("cluster").getAsInt();
                double x = pos.get("x").getAsDouble();
                double y = pos.get("y").getAsDouble();
                double z = pos.get("z").getAsDouble();

                list.add(new GraphNode(id, parent, cluster, x, y, z));
            }
            System.out.println(list.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getLatestGraphFile(String subFolderPath) {
        String userHome = System.getProperty("user.home");
        File folder = new File(userHome + File.separator + subFolderPath);
    
        File[] files = folder.listFiles((dir, name) ->
            name.startsWith("graph_data_") && name.endsWith(".json")
        );
    
        if (files == null || files.length == 0) {
            System.out.println("❌ Нет файлов в папке: " + folder.getAbsolutePath());
            return null;
        }
    
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0].getAbsolutePath();
    }
    
}
