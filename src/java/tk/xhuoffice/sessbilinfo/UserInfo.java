package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.Error;
import tk.xhuoffice.sessbilinfo.Http;
import tk.xhuoffice.sessbilinfo.JsonLib;

// API来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/info.md

public class UserInfo {
    
    public static Scanner scan = new Scanner(System.in);
    
    public static final String BILI_API_USER_CARD = "https://api.bilibili.com/x/web-interface/card";
    
    public static void getUserInfo() {
        String mid;
        // 提示输入信息
        System.out.println(
                "请输入被查询用户的 Mid 信息\n"+
                "示例: 645769214"
                );
        System.out.print("> ");
        // 获取输入信息
        mid = scan.nextLine();
        // 提示输入完成
        System.out.println("Mid: "+mid);
        // 输出用户信息
        card(mid);
    }
    
    public static void card(String mid) {
        // 向 API 发送 GET 请求
        String rawJson = Http.get(BILI_API_USER_CARD+"?mid="+mid);
        // 输出结果(用于调试)
        System.out.println(JsonLib.formatJson(rawJson));
        // 输出解析结果
        int code = JsonLib.getRootObjectInt(rawJson,"code");
        String message = JsonLib.getRootObjectString(rawJson,"message");
        System.out.print("请求代码: "+code+" ");
        // 输出错误信息
        if(code==0) {
            System.out.println();
        } else {
            Error.code(code);
            System.out.println("错误信息: "+message);
        }
    }
    
}
