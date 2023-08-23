package tk.xhuoffice.sessbilinfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.stream.StreamSupport;


public class JsonLib {
    
    public static final Gson GSON = new Gson();
    
    // 格式化 JSON
    public static String formatJson(String inputJson) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(GSON.fromJson(inputJson,JsonObject.class));
    }
    
    // 获取 String
    public static String getString(String inputJson, String... path) {
        JsonElement element = GSON.fromJson(inputJson, JsonObject.class);
        for(String key : path) {
            element = element.getAsJsonObject().get(key);
        }
        return element.getAsString();
    }
    
    // 获取 int
    public static int getInt(String inputJson, String... path) {
        JsonElement element = GSON.fromJson(inputJson, JsonObject.class);
        for(String key : path) {
            element = element.getAsJsonObject().get(key);
        }
        return element.getAsInt();
    }
    
    // 获取 long
    public static long getLong(String inputJson, String... path) {
        JsonElement element = GSON.fromJson(inputJson, JsonObject.class);
        for(String key : path) {
            element = element.getAsJsonObject().get(key);
        }
        return element.getAsLong();
    }
    
    // 获取 Json Array 中的内容作为 String[]
    public static String[] getArray(String json, String... arr) {
        JsonElement element = GSON.fromJson(json, JsonObject.class);
        for(String key : arr) {
            element = element.getAsJsonObject().get(key);
        }
        JsonArray jsonArray = element.getAsJsonArray();
        return StreamSupport.stream(jsonArray.spliterator(), false)
                .map(GSON::toJson)
                .toArray(String[]::new);
    }
    
}
