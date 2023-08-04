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
        int mid = getMid();
        // 获取被查询B站用户信息
        card(mid);
    }
    
    public static int getMid() {
        int mid = 0;
        int trytime = 0;
        try {
            while(mid < 1) {
                if(trytime!=0) {
                    // 尝试次数不为0时输出警告
                    System.err.println("[WARN] 无效的 Mid");
                }
                // 提示输入
                System.out.print("> ");
                trytime++;
                // 检查输入是否为整数
                while(!scan.hasNextInt()) {
                    // 消耗错误的输入
                    scan.next();
                    // 输出警告
                    System.err.println("[WARN] 无效的 Mid");
                    // 提示重新输入
                    System.out.print("> ");
                    trytime++;
                }
                // 获取输入信息
                mid = scan.nextInt();
                scan.nextLine(); // 消耗换行符
            }
        } catch(Exception e) {
            System.err.println("\n[FATAL] 无效的 Mid");
            System.exit(1);
        }
        return mid;
    }
    
    public static void card(int mid) {
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
