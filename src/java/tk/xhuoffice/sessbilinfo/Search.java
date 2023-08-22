package tk.xhuoffice.sessbilinfo;

import tk.xhuoffice.sessbilinfo.Http;
import tk.xhuoffice.sessbilinfo.Logger;
import tk.xhuoffice.sessbilinfo.OutFormat;

// API来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/search/search_request.md


public class Search {

    // 基本API
    public static final String BASE_URL = "https://api.bilibili.com/x";
    // 综合搜索
    public static final String SEARCH_ALL = BASE_URL+"/web-interface/search/all/v2";
    
    public static void search() {
        // 获取搜索内容
        Logger.println("请输入关键词",1);
        String keyword = OutFormat.getString("关键词");
        // 进行搜索
        String result = all(keyword);
        // 输出结果
        Logger.println(result,1);
    }
    
    public static String all(String keyword) {
        // 初始化变量
        String result = "";
        // 发送请求
        String rawJson = Http.get(SEARCH_ALL+"?keyword="+keyword);
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            result += "OK";
        } else if(code==-412) {
            Logger.println("请求被拦截, 请检测 Cookie 长度",3);
        } else {
            Error.out(rawJson);
        }
        return result;
    }

}