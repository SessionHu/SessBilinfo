package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.Error;
import tk.xhuoffice.sessbilinfo.Http;
import tk.xhuoffice.sessbilinfo.JsonLib;

// API来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/info.md
// 认证类型来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/official_role.md

public class UserInfo {
    
    public static Scanner scan = new Scanner(System.in);
    
    // 基本API
    public static final String BASE_URL = "https://api.bilibili.com/x";
    // 用户名片信息
    public static final String USER_CARD = BASE_URL+"/web-interface/card";
    // 用户置顶视频
    public static final String USER_SPACE_TOP = BASE_URL+"/space/top/arc";
    
    public static void getUserInfo() {
        // 提示输入信息
        System.out.println(
                "[INFO] 请输入被查询用户的 Mid 信息\n"+
                "[INFO] 示例: 645769214"
                );
        // 向用户获取 Mid
        String mid = getMid();
        // 获取并打印被查询的B站用户信息
        String usrinfo = "";
        usrinfo += card(mid);
        usrinfo += space(mid);
        System.out.print("----------------\n\n");
        System.out.print(usrinfo);
        System.out.print("----------------\n");
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
    
    public static String card(String mid) {
        // 向 API 发送 GET 请求
        String rawJson = Http.get(USER_CARD+"?mid="+mid,"(1/2)");
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            // 解析返回内容
            String nickname = JsonLib.getString(rawJson,"data","card","name"); // 用户昵称
            String sign = JsonLib.getString(rawJson,"data","card","sign"); // 签名
            int fans = JsonLib.getInt(rawJson,"data","card","fans"); // 粉丝数
            int level = JsonLib.getInt(rawJson,"data","card","level_info","current_level"); // 当前等级
            int friend = JsonLib.getInt(rawJson,"data","card","friend"); // 关注数
            String sex = JsonLib.getString(rawJson,"data","card","sex"); // 性别
            String offical_tag = "";
            String offical_info = "";
            if(JsonLib.getInt(rawJson,"data","card","Official","type")==0) { // 认证信息
                // 处理认证类型
                int offical_typ = JsonLib.getInt(rawJson,"data","card","Official","role");
                offical_tag = offical(offical_typ);
                // 处理认证信息
                offical_info = JsonLib.getString(rawJson,"data","card","Official","title"); // 认证内容
            }
            // 处理无效数据
            if(sign.trim().isEmpty()) { // 签名
                sign = "(这个人很神秘,什么都没有写)";
            }
            if(sex.equals("保密")) { // 性别
                sex = "";
            }
            // 输出解析结果
            String cardinfo = "";
            cardinfo += "[INFO] Lv"+level+"  "+nickname+"  "+sex+"\n";
            cardinfo += "[INFO] 粉丝 "+fans+"   关注 "+friend+"\n";
            if(!offical_tag.trim().isEmpty()) { // 有认证信息时打印
                cardinfo += "[INFO] "+offical_tag+offical_info+"\n";
            }
            cardinfo += "[INFO] 签名 "+sign+"\n";
            cardinfo += "\n";
            return cardinfo;
        } else {
            Error.out(rawJson);
            System.exit(0);
            return "";
        }
    }
    
    public static String space(String mid) {
        String top = spaceTop(mid);
        return top;
    }
    
    public static String spaceTop(String mid) {
        // 向 API 发送 GET 请求
        String rawJson = Http.get(USER_SPACE_TOP+"?vmid="+mid,"(2/2)");
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            // 解析返回信息
            int aid = JsonLib.getInt(rawJson,"data","aid"); // avid
            String title = JsonLib.getString(rawJson,"data","title"); // 标题
            int alltime = JsonLib.getInt(rawJson,"data","duration"); // 总时长
            int view = JsonLib.getInt(rawJson,"data","stat","view"); // 播放
            int danmaku = JsonLib.getInt(rawJson,"data","stat","danmaku"); // 弹幕
            // 处理返回信息
            String avid = "av"+aid; // avid
            // 输出处理结果
            String topinfo = "";
            topinfo += "置顶视频 "+avid+"\n";
            topinfo += title+"\n";
            topinfo += "播放 "+view+"   弹幕 "+danmaku+"\n";
            topinfo += "时长 "+alltime+"s\n";
            topinfo += "\n";
            return topinfo;
        } else if(code==53016) {
            // 无置顶视频...
        } else {
            Error.out(rawJson);
        }
        return "";
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
