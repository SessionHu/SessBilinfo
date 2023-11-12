package tk.xhuoffice.sessbilinfo.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.stream.StreamSupport;


public class JsonLib {
    
    private static final Gson GSON = new Gson();
    
    // 格式化 JSON
    public static String formatJson(String inputJson) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(GSON.fromJson(inputJson,JsonObject.class));
    }
    
    private static JsonElement getJsonElement(String inputJson, String[] path) {
        JsonElement element = GSON.fromJson(inputJson, JsonObject.class);
        for(String key : path) {
            element = element.getAsJsonObject().get(key);
        }
        return element;
    }
    
    // 获取 String
    public static String getString(String inputJson, String... path) {
        try {
            return getJsonElement(inputJson,path).getAsString();
        } catch(NullPointerException e) {
            return null;
        }
    }
    
    // 获取 int
    public static int getInt(String inputJson, String... path) {
        return getJsonElement(inputJson,path).getAsInt();
    }
    
    // 获取 long
    public static long getLong(String inputJson, String... path) {
        return getJsonElement(inputJson,path).getAsLong();
    }
    
    // 获取 float
    public static float getFloat(String inputJson, String... path) {
        return getJsonElement(inputJson,path).getAsFloat();
    }
    
    // 获取 Json Array 中的内容作为 String[]
    public static String[] getArray(String json, String... path) {
        JsonArray jsonArr = getJsonElement(json,path).getAsJsonArray();
        return StreamSupport.stream(jsonArr.spliterator(),false).map(GSON::toJson).toArray(String[]::new);
    }
    
}
