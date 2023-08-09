package tk.xhuoffice.sessbilinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class JsonLib {
    
    public static final Gson GSON = new Gson();
    
    // 格式化 JSON
    public static String formatJson(String inputJson) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(GSON.fromJson(inputJson,JsonObject.class));
    }
    
    // 获取 String
    public static String getString(String inputJson, String... path) {
        JsonElement element = GSON.fromJson(inputJson, JsonObject.class);
        for (String key : path) {
            element = element.getAsJsonObject().get(key);
        }
        return GSON.fromJson(element, String.class);
    }
    
    // 获取 int
    public static int getInt(String inputJson, String... path) {
        JsonElement element = GSON.fromJson(inputJson, JsonObject.class);
        for (String key : path) {
            element = element.getAsJsonObject().get(key);
        }
        return GSON.fromJson(element, Integer.class);
    }
}
