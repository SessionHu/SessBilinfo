package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.Error;
import tk.xhuoffice.sessbilinfo.Http;
import tk.xhuoffice.sessbilinfo.JsonLib;

// API来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/info.md
// 认证类型来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/official_role.md

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
                // 将输入转换为 int
                int mid = Integer.parseInt(input);
                // 检测输入是否大于0
                if(mid>0) {
                    // 提示并返回结果
                    System.out.println("[INFO] Mid: "+mid);
                    return input;
                } else {
                    // 输出警告
                    System.err.println("[WARN] 无效的 Mid "+input);
                }
            } catch(Exception e) {
                // 输出警告
                System.err.println("[WARN] 过大或非数字不能作为 Mid");
            }
        }
    }
    
    public static void card(String mid) {
        // 向 API 发送 GET 请求
        String rawJson = Http.get(BILI_API_USER_CARD+"?mid="+mid);
        // 输出结果(用于调试)
        //System.out.println("[DEBUG] "+JsonLib.formatJson(rawJson));
        // 获取返回值及可能的错误信息
        int code = JsonLib.getRootObjectInt(rawJson,"code"); // 返回值
        String message = JsonLib.getRootObjectString(rawJson,"message"); // 错误信息
        if(code==0) {
            // 解析返回内容
            String nickname = JsonLib.getSubSubObjectString(rawJson,"data","card","name"); // 用户昵称
            String sign = JsonLib.getSubSubObjectString(rawJson,"data","card","sign"); // 签名
            int fans = JsonLib.getSubSubObjectInt(rawJson,"data","card","fans"); // 粉丝数
            int level = JsonLib.getSubSubSubObjectInt(rawJson,"data","card","level_info","current_level"); // 当前等级
            int friend = JsonLib.getSubSubObjectInt(rawJson,"data","card","friend"); // 关注数
            String sex = JsonLib.getSubSubObjectString(rawJson,"data","card","sex"); // 性别
            int offical_typ = -1;
            String offical_tag = "";
            String offical_info = "";
            if(JsonLib.getSubSubSubObjectInt(rawJson,"data","card","Official","type")==0) { // 认证信息
                // 处理认证类型
                offical_typ = JsonLib.getSubSubSubObjectInt(rawJson,"data","card","Official","role");
                offical_tag = offical(offical_typ);
                // 处理认证信息
                offical_info = JsonLib.getSubSubSubObjectString(rawJson,"data","card","Official","title"); // 认证内容
            }
            // 处理无效数据
            if(sign.trim().isEmpty()) {
                sign = "(这个人很神秘,什么都没有写)";
            } // 签名
            if(sex.equals("保密")) {
                sex = "";
            }
            // 输出解析结果
            System.out.println("[INFO] --------------------");
            System.out.println("[INFO] Lv"+level+"  "+nickname+"  "+sex);
            System.out.println("[INFO] 粉丝 "+fans+"   关注 "+friend);
            System.out.println("[INFO] "+offical_tag+offical_info);
            System.out.println("[INFO] 签名 "+sign);
            System.out.println("[INFO] --------------------");
        } else {
            // 输出错误信息
            System.err.print("[ERROR] 返回值: "+code+" ");
            Error.code(code);
            System.out.println("[ERROR] 错误信息: "+message);
        }
    }
    
    public static String offical(int typ) {
        String offical_tag = "";
        switch(typ) {
            // 个人认证
            case 1:
                offical_tag = "UP主(知名UP主)认证: ";
                break;
            case 2:
                offical_tag = "UP主(大V达人)认证: ";
                break;
            case 7:
                offical_tag = "UP主(高能主播)认证: ";
                break;
            case 9:
                offical_tag = "UP主(社会知名人士)认证: ";
                break;
            // 机构认证
            case 3:
                offical_tag = "机构(企业)认证: ";
                break;
            case 4:
                offical_tag = "机构(组织)认证: ";
                break;
            case 5:
                offical_tag = "机构(媒体)认证: ";
                break;
            case 6:
                offical_tag = "机构(政府)认证: ";
                break;
            default:
                // 我也不知道 role 为 8 时是什么
                System.err.println("[ERROR] 未知的认证类型 "+typ);
                System.err.println("[ERROR] 请向 SocialSisterYi/bilibili-API-collect 与 SessionHu/SessBilinfo 提交 Issue, 内容请包含 有关请求的 Mid 的信息 与 上一行认证类型输出 的截图");
        }
        return offical_tag;
    }
}
