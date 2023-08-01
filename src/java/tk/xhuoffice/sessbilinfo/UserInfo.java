package tk.xhuoffice.sessbilinfo;

import tk.xhuoffice.sessbilinfo.Http;
import tk.xhuoffice.sessbilinfo.Json;


public class UserInfo {
    
    public static final String BILI_API_USER_CARD = "https://api.bilibili.com/x/web-interface/card";
    
    public static void card(String mid) {
        // 向 API 发送 GET 请求
        String rawJson = Http.get(BILI_API_USER_CARD+"?mid="+mid);
        // 格式化请求的 JSON
        String okJson = Json.formatJson(rawJson);
        // 输出结果
        System.out.println(okJson);
    }
    
}
