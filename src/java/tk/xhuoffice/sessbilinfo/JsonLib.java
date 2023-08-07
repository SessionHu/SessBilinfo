package tk.xhuoffice.sessbilinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class JsonLib {
    
    public static final Gson GSON = new Gson();
    
    // 格式化 JSON
    public static String formatJson(String inputJson) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(GSON.fromJson(inputJson,JsonObject.class));
    }
    
    // eg: 获取 {"code":6} 中 "code" 的数值
    public static int getRootObjectInt(String inputJson, String str) {
        return GSON.fromJson(inputJson,JsonObject.class).get(str).getAsInt();
    }
    
    // eg: 获取 {"message":"test"} 中 "message" 的内容
    public static String getRootObjectString(String inputJson, String str) {
        return GSON.fromJson(inputJson,JsonObject.class).get(str).getAsString();
    }
    
    /* eg: 获取下面这段 JSON 中 "like_num" 的数值
        {
            "data": {
                "like_num": 6
            }
        }
    */
    public static int getSubObjectInt(String inputJson, String obj1, String str) {
        return GSON.fromJson(inputJson,JsonObject.class).getAsJsonObject(obj1).get(str).getAsInt();
    }
    
    /* eg: 获取下面这段 JSON 中 "name" 的内容
        {
            "data": {
                "card": {
                    "name": "bishi"
                }
            }
        }
    */
    public static String getSubSubObjectString(String inputJson, String obj1, String obj2, String str) {
        return GSON.fromJson(inputJson,JsonObject.class).getAsJsonObject(obj1).getAsJsonObject(obj2).get(str).getAsString();
    }
    
    /* eg: 获取下面这段 JSON 中 "fans" 的数值
        {
            "data": {
                "card": {
                    "fans": 114
                }
            }
        }
    */
    public static int getSubSubObjectInt(String inputJson, String obj1, String obj2, String str) {
        return GSON.fromJson(inputJson,JsonObject.class).getAsJsonObject(obj1).getAsJsonObject(obj2).get(str).getAsInt();
    }
    
    /* eg: 获取下面这段 JSON 中 "current_level" 的数值
        {
            "data": {
                "card": {
                    "level_info": {
                        "current_level": 6
                    }
                }
            }
        }
    */
    public static int getSubSubSubObjectInt(String inputJson, String obj1, String obj2, String obj3, String str) {
        return GSON.fromJson(inputJson,JsonObject.class).getAsJsonObject(obj1).getAsJsonObject(obj2).getAsJsonObject(obj3).get(str).getAsInt();
    }
    
}
