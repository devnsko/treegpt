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

                // JsonObject promptParts = obj.has("prompt") && !obj.get("prompt").isJsonNull() 
                //                         ? obj.getAsJsonObject("prompt").getAsJsonObject("parts") 
                //                         : null;
                // String promptText = promptParts != null && promptParts.size() > 0 ? promptParts.get("text").getAsString() : null;
                
                JsonArray replyParts = obj.has("reply") && !obj.get("reply").isJsonNull() 
                                        ? obj.getAsJsonObject("reply").getAsJsonArray("parts") 
                                        : null;
                String replyText = replyParts != null && replyParts.size() > 0 ? replyParts.get(0).getAsJsonObject().get("text").getAsString() : null;

                // TODO: SKIP pairs without reply text
                if (replyText == null || replyText.length() == 0) continue;

                System.out.println(obj.get("title"));
                JsonObject pos = obj.getAsJsonObject("position");

                String promptId = obj.getAsJsonObject("prompt").get("message_id").getAsString();
                String replyId = obj.getAsJsonObject("reply").get("message_id").getAsString();
                String parentId = obj.has("parent_id") && !obj.get("parent_id").isJsonNull()
                                ? obj.get("parent_id").getAsString()
                                : null;
                List<String> childrenIds = new ArrayList<>();
                if (obj.has("children_id") && obj.get("children_id").isJsonArray()) {
                    for (JsonElement childId : obj.getAsJsonArray("children_id")) {
                        childrenIds.add(childId.getAsString());
                    }
                }
                String conversationId = obj.get("conversation_id").getAsString();
                String conversationTitle = obj.get("title").getAsString();

                int cluster = obj.get("cluster").getAsInt();
                double x = pos.get("x").getAsDouble();
                double y = pos.get("y").getAsDouble();
                double z = pos.get("z").getAsDouble();

                System.out.println(promptId + " - " + replyId);
                list.add(new GraphNode( promptId, 
                                        replyId, 
                                        childrenIds, 
                                        parentId, 
                                        conversationTitle,
                                        conversationId, 
                                        cluster, 
                                        x, y, z));
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
