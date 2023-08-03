package tk.xhuoffice.sessbilinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class JsonLib {
    
    public static Gson gson = new Gson();
    
    public static String formatJson(String inputJson) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(gson.fromJson(inputJson,JsonObject.class));
    }
    
    public static int getRootObjectInt(String inputJson, String str) {
        return gson.fromJson(inputJson,JsonObject.class).get(str).getAsInt();
    }
    
    public static String getRootObjectString(String inputJson, String str) {
        return gson.fromJson(inputJson,JsonObject.class).get(str).getAsString();
    }
    
    public static String getSubSubObjectString(String inputJson, String objName1, String objName2, String str) {
        return gson.fromJson(inputJson,JsonObject.class).getAsJsonObject(objName1).getAsJsonObject(objName2).get(str).getAsString();
    }
    
    public static int getSubSubObjectInt(String inputJson, String objName1, String objName2, String str) {
        return gson.fromJson(inputJson,JsonObject.class).getAsJsonObject(objName1).getAsJsonObject(objName2).get(str).getAsInt();
    }
    
}
