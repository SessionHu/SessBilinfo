package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.Error;
import tk.xhuoffice.sessbilinfo.Http;
import tk.xhuoffice.sessbilinfo.Logger;
import tk.xhuoffice.sessbilinfo.JsonLib;
import tk.xhuoffice.sessbilinfo.OutFormat;

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
    // 用户代表作
    public static final String USER_SPACE_MASTERPIECE = BASE_URL+"/space/masterpiece";

    public static void getUserInfo() {
        // 提示输入信息
        Logger.println(
                "请输入被查询用户的 Mid 信息\n"+
                "示例: 645769214"
                ,1);
        // 向用户获取 Mid
        String mid = getMid();
        // 获取并打印被查询的B站用户信息
        String usrinfo = "";
        usrinfo += "\n";
        usrinfo += "------------------------\n\n";
        usrinfo += card(mid);
        usrinfo += space(mid);
        usrinfo += "------------------------";
        Logger.println(usrinfo,1);
    }
    
    public static String getMid() {
        // 定义并初始化变量
        String input = "";
        while(true) {
            // 提示输入
            Logger.inputHere();
            try {
                // 获取输入
                input = scan.nextLine();
            } catch(Exception e) {
                // 异常处理
                Logger.ln();
                Logger.println("无效的 Mid",4);
                System.exit(1);
            }
            try {
                // 将输入转换为 int
                int mid = Integer.parseInt(input);
                // 检测输入是否大于0
                if(mid>0) {
                    // 提示并返回结果
                    Logger.println("Mid: "+mid,1);
                    return input;
                } else {
                    // 输出警告
                    Logger.println("无效的 Mid "+input,2);
                }
            } catch(Exception e) {
                // 输出警告
                Logger.println("过大或非数字不能作为 Mid",2);
            }
        }
    }
    
    public static String card(String mid) {
        // 向 API 发送 GET 请求
        String rawJson = Http.get(USER_CARD+"?mid="+mid,"(1/3)");
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
            // 处理返回内容
            if(sign.trim().isEmpty()) { // 签名
                sign = "(这个人很神秘,什么都没有写)";
            }
            String strFans = OutFormat.num(fans); // 粉丝数
            String strFriend = OutFormat.num(friend); // 关注数
            if(sex.equals("保密")) { // 性别
                sex = "";
            }
            // 输出解析结果
            String cardinfo = "";
            cardinfo += "Lv"+level+"  "+nickname+"  "+sex+"\n";
            cardinfo += "粉丝 "+strFans+"   关注 "+strFriend+"\n";
            if(!offical_tag.trim().isEmpty()) { // 有认证信息时打印
                cardinfo += offical_tag+offical_info+"\n";
            }
            cardinfo += "签名 "+sign+"\n";
            cardinfo += "\n";
            return cardinfo;
        } else {
            Error.out(rawJson);
            System.exit(0);
            return "";
        }
    }
    
    public static String space(String mid) {
        String space = "";
        space += spaceTop(mid);
        space += spaceMasterpiece(mid);
        return space;
    }
    
    public static String spaceTop(String mid) {
        // 向 API 发送 GET 请求
        String rawJson = Http.get(USER_SPACE_TOP+"?vmid="+mid,"(2/3)");
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            // 解析返回信息
            long aid = JsonLib.getLong(rawJson,"data","aid"); // avid
            String title = JsonLib.getString(rawJson,"data","title"); // 标题
            int allsec = JsonLib.getInt(rawJson,"data","duration"); // 总时长(s)
            int view = JsonLib.getInt(rawJson,"data","stat","view"); // 播放
            int danmaku = JsonLib.getInt(rawJson,"data","stat","danmaku"); // 弹幕
            long pubdate= JsonLib.getLong(rawJson,"data","pubdate"); // 发布时间
            String desc = JsonLib.getString(rawJson,"data","desc"); // 简介
            // 处理返回信息
            String avid = "av"+aid; // avid
            String playtime = OutFormat.time(allsec); // 总时长((hh:m)m:ss)
            String strView = OutFormat.num(view); // 播放
            String strDanmaku = OutFormat.num(danmaku); // 弹幕
            String date = OutFormat.date(pubdate); // 发布时间
            String dscpt = OutFormat.formatString(desc,"     ");
            // 输出处理结果
            String topinfo = "";
            topinfo += "置顶视频\n";
            topinfo += "标题 "+title+"\n";
            topinfo += "AV号 "+avid+"   "+date+"   时长 "+playtime+"\n";
            topinfo += "播放 "+strView+"   弹幕 "+strDanmaku+"\n";
            topinfo += "简介 "+dscpt+"\n";
            topinfo += "\n";
            return topinfo;
        } else if(code==53016) {
            // 无置顶视频
        } else {
            Error.out(rawJson);
        }
        return "";
    }

    public static String spaceMasterpiece(String mid) {
        // 向 API 发送 GET 请求
        String rawJson = Http.get(USER_SPACE_MASTERPIECE+"?vmid="+mid,"(3/3)");
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            // 提取返回信息
            String[] jsons = JsonLib.getArrayObject(rawJson,"data");
            // 处理返回信息
            if(jsons.length>0) {
                // 定义变量
                String result = "代表作\n";
                String videoinfo = "";
                String json = "";
                // 依次处理信息
                for(int i = 0; i < jsons.length; i++) {
                    videoinfo = "";
                    json = jsons[i];
                    // 解析信息
                    long aid = JsonLib.getLong(json,"aid"); // avid
                    String title = JsonLib.getString(json,"title"); // 标题
                    int allsec = JsonLib.getInt(json,"duration"); // 总时长(s)
                    int view = JsonLib.getInt(json,"stat","view"); // 播放
                    int danmaku = JsonLib.getInt(json,"stat","danmaku"); // 弹幕
                    // 处理信息
                    String avid = "av"+aid; // avid
                    String playtime = OutFormat.time(allsec); // 总时长((hh:m)m:ss)
                    String strView = OutFormat.num(view); // 播放
                    String strDanmaku = OutFormat.num(danmaku); // 弹幕
                    // 输出信息
                    videoinfo += (i+1) + ". " + title + "\n";
                    videoinfo += "   " + playtime + "   播放 " + strView + "   弹幕 " + strDanmaku + "\n";
                    result += videoinfo;
                }
                // 返回信息
                result += "\n";
                return result;
            }
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
                Logger.println("未知的认证类型 "+typ,3);
                Logger.println("请向 SocialSisterYi/bilibili-API-collect 与 SessionHu/SessBilinfo 提交 Issue, 内容请包含 有关请求的 Mid 的信息 与 上一行认证类型输出 的截图",3);
        }
        return offical_tag;
    }
    
}
