package tk.xhuoffice.sessbilinfo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.stream.StreamSupport;


public class JsonLib {
    
    // NO <init>
    private JsonLib() {}
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();
    
    /**
     * 格式化 JSON.
     * @param inputJson Raw JSON.
     * @return          Formatted JSON.
     */
    public static String formatJson(String inputJson) {
        return GSON.toJson(GSON.fromJson(inputJson,JsonObject.class));
    }
    
    private static JsonElement getJsonElement(String inputJson, String[] path) {
        try {
            JsonElement element = GSON.fromJson(inputJson, JsonObject.class);
            for(String key : path) {
                if(element==null || !element.getAsJsonObject().has(key)) {
                    return null;
                } else {
                    element = element.getAsJsonObject().get(key);
                }
            }
            return element;
        } catch(com.google.gson.JsonSyntaxException e) {
            OutFormat.outThrowable(e,3);
            return null;
        }
    }
    
    /**
     * 获取 JSON 中的数据.
     * @param inputJson JSON
     * @param type      Data type
     * @param <T>       Data type
     * @param path      Data path
     * @return          Data in type
     */
    public static <T>T get(String inputJson, Class<T> type, String... path) {
        JsonElement element = getJsonElement(inputJson,path);
        if(element!=null) {
            return GSON.fromJson(element,type);
        } else {
            return null;
        }
    }
    
    /**
     * 获取 JSON 中的 {@link String}.
     * @param inputJson JSON
     * @param path      Data path
     * @return          Data in {@link String}
     */
    public static String getString(String inputJson, String... path) {
        return get(inputJson,String.class,path);
    }
    
    /**
     * 获取 JSON 中的 {@code int}.
     * @param inputJson JSON
     * @param path      Data path
     * @return          Data in {@code int}
     */
    public static int getInt(String inputJson, String... path) {
        try {
            return get(inputJson,int.class,path);
        } catch(NullPointerException e) {
            return 0;
        }
    }
    
    /**
     * 获取 JSON 中的 {@code long}.
     * @param inputJson JSON
     * @param path      Data path
     * @return          Data in {@code long}
     */
    public static long getLong(String inputJson, String... path) {
        try {
            return get(inputJson,long.class,path);
        } catch(NullPointerException e) {
            return 0L;
        }
    }
    
    /**
     * 获取 JSON 中的 {@code float}.
     * @param inputJson JSON
     * @param path      Data path
     * @return          Data in {@code float}
     */
    public static float getFloat(String inputJson, String... path) {
        try {
            return get(inputJson,float.class,path);
        } catch(NullPointerException e) {
            return 0.0f;
        }
    }
    
    /**
     * 获取 Json Array 中的内容作为 {@code String[]}.
     * @param json JSON
     * @param path Array path
     * @return     JSON in {@code String[]}
     */
    public static String[] getArray(String json, String... path) {
        JsonArray jsonArr = getJsonElement(json,path).getAsJsonArray();
        return StreamSupport.stream(jsonArr.spliterator(),false).map(GSON::toJson).toArray(String[]::new);
    }
    
}
