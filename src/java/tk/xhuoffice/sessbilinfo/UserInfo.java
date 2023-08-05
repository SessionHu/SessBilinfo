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
        // 提示输入信息
        System.out.println(
                "[INFO] 请输入被查询用户的 Mid 信息\n"+
                "[INFO] 示例: 645769214"
                );
        // 向用户获取 Mid
        String mid = getMid();
        // 获取被查询B站用户信息
        card(mid);
    }
    
    public static String getMid() {
        // 定义并初始化变量
        String input = "";
        while(true) {
            // 提示输入
            System.out.print("> ");
            try {
                // 获取输入
                input = scan.nextLine();
            } catch(Exception e) {
                // 异常处理
                System.err.println("\n[FATAL] 无效的 Mid");
                System.exit(1);
            }
            try {
                // 检测输入是否大于0
                if(Integer.parseInt(input)>0) {
                    // 返回结果
                    return input;
                } else {
                    // 输出警告
                    System.err.println("[WARN] 无效的 Mid");
                }
            } catch(Exception e) {
                // 输出警告
                System.err.println("[WARN] 无效的 Mid");
            }
        }
    }
    
    public static void card(String mid) {
        // 提示信息
        System.out.println("[INFO] 正在请求数据...");
        // 向 API 发送 GET 请求
        String rawJson = Http.get(BILI_API_USER_CARD+"?mid="+mid);
        // 输出结果(用于调试)
        //System.out.println("[DEBUG] "+JsonLib.formatJson(rawJson));
        // 获取返回值及可能的错误信息
        int code = JsonLib.getRootObjectInt(rawJson,"code");
        String message = JsonLib.getRootObjectString(rawJson,"message");
        if(code==0) {
            // 解析返回内容
            String data_card_name = JsonLib.getSubSubObjectString(rawJson,"data","card","name");
            String data_card_sign = JsonLib.getSubSubObjectString(rawJson,"data","card","sign");
            int data_card_fans = JsonLib.getSubSubObjectInt(rawJson,"data","card","fans");
            // 输出解析结果
            System.out.println("[INFO] 昵称: "+data_card_name);
            System.out.println("[INFO] 签名: "+data_card_sign);
            System.out.println("[INFO] 粉丝数: "+data_card_fans);
        } else {
            // 输出错误信息
            System.err.print("[ERROR] 请求代码: "+code+" ");
            Error.code(code);
            System.out.println("[INFO] 详细信息: "+message);
        }
    }
    
}
