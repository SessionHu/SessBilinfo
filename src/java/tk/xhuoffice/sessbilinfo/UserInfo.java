package tk.xhuoffice.sessbilinfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class UserInfo {
    
    public static final String BILI_API_USER_CARD = "https://api.bilibili.com/x/web-interface/card";
    public static final String HTTP_USER_AGENT = "Dalvik/2.1.0 (Linux; U; Android 12; MLD-AL00 Build/HUAWEIMLD-AL00) 7.38.0 os/android model/MLD-AL00 mobi_app/Ai4cCreatorAndroid build/7380300 channel/master innerVer/7380310 osVer/12 network/2 grpc-java-cronet/1.36.1";
    
    public static void card(String mid) {
        try {
            // 创建 URL 对象
            URL url = new URL(BILI_API_USER_CARD+"?mid="+mid);
            // 打开连接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            // 设置请求方法为 GET
            conn.setRequestMethod("GET");
            // 设置 User-Agent 请求头
            conn.setRequestProperty("User-Agent",HTTP_USER_AGENT);
            // 创建输入流并读取返回数据
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // 输出返回的 JSON 数据
            System.out.println(formatJson(response.toString(),4));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String formatJson(String json, int indent) {
        StringBuilder result = new StringBuilder();
        int level = 0; // 当前缩进级别
        boolean inQuote = false; // 是否在引号内
        for(int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if(c == '\"') {
                inQuote = !inQuote;
            }
            if(!inQuote) {
                if(c == '{' || c == '[') { // 遇到左括号，增加缩进级别
                    result.append(c);
                    result.append('\n');
                    level++;
                    for(int j = 0; j < level * indent; j++) {
                        result.append(' ');
                    }
                } else if(c == '}' || c == ']') { // 遇到右括号，减少缩进级别
                    result.append('\n');
                    level--;
                    for(int j = 0; j < level * indent; j++) {
                        result.append(' ');
                    }
                    result.append(c);
                } else if(c == ',') { // 遇到逗号，换行并缩进
                    result.append(c);
                    result.append('\n');
                    for(int j = 0; j < level * indent; j++) {
                        result.append(' ');
                    }
                } else if(c == ':') { // 遇到冒号，添加空格
                    result.append(c);
                    result.append(' ');
                } else {
                result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
}
