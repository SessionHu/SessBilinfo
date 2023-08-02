package tk.xhuoffice.sessbilinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class JsonLib {

    public static String formatJson(String inputJson) {
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = gsonBuilder.fromJson(inputJson, JsonObject.class);
        return gsonBuilder.toJson(jsonObject);
    }
    
    public static int getRootObjectInt(String inputJson, String str) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(inputJson, JsonObject.class);
        int num = jsonObject.get(str).getAsInt();
        return num;
    }
    
}