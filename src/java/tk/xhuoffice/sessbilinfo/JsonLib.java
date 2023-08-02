package tk.xhuoffice.sessbilinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class JsonLib {
    
    public static Gson gson = new Gson();
    
    public static String formatJson(String inputJson) {
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = gsonBuilder.fromJson(inputJson, JsonObject.class);
        return gsonBuilder.toJson(jsonObject);
    }
    
    public static int getRootObjectInt(String inputJson, String str) {
        JsonObject jsonObject = gson.fromJson(inputJson, JsonObject.class);
        int num = jsonObject.get(str).getAsInt();
        return num;
    }
    
    public static String getRootObjectString(String inputJson, String str) {
        JsonObject jsonObject = gson.fromJson(inputJson, JsonObject.class);
        String mssag = jsonObject.get(str).getAsString();
        return mssag;
    }
    
}