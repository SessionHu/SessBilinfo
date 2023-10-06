package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import tk.xhuoffice.sessbilinfo.net.Http;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.BiliAPIs;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

// 认证类型来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/official_role.md


public class UserInfo {
    
    public static Scanner scan = new Scanner(System.in);
    
    public static void getUserInfo() {
        Frame.reset();
        // 提示输入信息
        Logger.println(
                "请输入被查询用户的 Mid 信息\n"+
                "示例: 645769214");
        // 向用户获取 Mid
        String mid = OutFormat.getPositiveLongAsString("Mid");
        // 获取数据
        Logger.println("正在请求数据...");
        String usrinfo = "";
        usrinfo += "------------------------\n\n";
        usrinfo += card(mid);
        usrinfo += space(mid);
        usrinfo += "------------------------";
        Logger.println("请求完毕");
        // 输出数据
        Frame.reset();
        Logger.println(usrinfo);
    }
    
    public static String card(String mid) {
        // 向 API 发送 GET 请求
        Logger.debugln("获取用户名片信息");
        String rawJson = Http.get(BiliAPIs.USER_CARD+"?mid="+mid);
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
                offical_tag = offical(offical_typ,mid);
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
            BiliAPIs.outCodeErr(rawJson);
            System.exit(0);
            return "";
        }
    }
    
    public static String space(String mid) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Future<String> spaceTag = executor.submit(() -> spaceTag(mid));
        Future<String> spaceNotice = executor.submit(() -> spaceNotice(mid));
        Future<String> spaceTop = executor.submit(() -> spaceTop(mid));
        Future<String> spaceMasterpiece = executor.submit(() -> spaceMasterpiece(mid));
        String result = "";
        try {
            result += spaceTag.get();
            result += spaceNotice.get();
            result += spaceTop.get();
            result += spaceMasterpiece.get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            OutFormat.outThrowable(e,3);
        }
        executor.shutdown();
        return result;
    }
    
    public static String spaceNotice(String mid) {
        // 发送请求
        Logger.debugln("获取用户空间公告");
        String json = Http.get(BiliAPIs.USER_SPACE_NOTICE+"?mid="+mid);
        // 获取返回值
        int code = JsonLib.getInt(json,"code");
        if(code==0) {
            // 解析返回信息
            String data = JsonLib.getString(json,"data");
            // 处理返回结果
            if(data==null||data.trim().isEmpty()) {
                // 返回结果为空时不输出
                return "";
            } else {
                // 输出处理结果
                return "空间公告\n"+data+"\n\n";
            }
        } else {
            // 输出错误信息
            BiliAPIs.outCodeErr(json);
        }
        return "";
    }
    
    public static String spaceTag(String mid) {
        // 发送请求
        Logger.debugln("获取用户空间TAG");
        String rawJson = Http.get(BiliAPIs.USER_SPACE_TAG+"?mid="+mid);
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            try {
                // 提取返回信息
                String data = JsonLib.getArray(rawJson,"data")[0];
                String[] tags = JsonLib.getArray(data,"tags");
                if(tags.length>0) {
                    // 处理返回信息
                    String listag = "TAG: ";
                    for(int i = 0; i < tags.length; i++) {
                        // 获取 tag
                        String tag = tags[i];
                        // 处理首尾引号
                        tag = tag.substring(1, tag.length() - 1);
                        // 将处理好的 tag 进行添加
                        listag += tag + ", ";
                    }
                    // 整理处理信息
                    listag = listag.substring(0, listag.length() - 2);
                    listag += "\n\n";
                    // 输出处理信息
                    return listag;
                }
            } catch(NullPointerException e) {
                // 空指针异常
                // 一般是 tags 为空导致的
                // 不需要处理
            }
        } else {
            // 输出错误
            BiliAPIs.outCodeErr(rawJson);
        }
        return "";
    }
    
    public static String spaceTop(String mid) {
        // 向 API 发送 GET 请求
        Logger.debugln("获取用户置顶视频");
        String rawJson = Http.get(BiliAPIs.USER_SPACE_TOP+"?vmid="+mid);
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
            String playtime = OutFormat.time(allsec); // 总时长((hh:m)m:ss)
            String strView = OutFormat.num(view); // 播放
            String strDanmaku = OutFormat.num(danmaku); // 弹幕
            String date = OutFormat.date(pubdate); // 发布时间
            String dscpt = OutFormat.formatString(desc,"     ");
            // 输出处理结果
            String topinfo = "";
            topinfo += "置顶视频\n";
            topinfo += "标题 "+title+"\n";
            topinfo += "AV号 "+aid+"   "+date+"   时长 "+playtime+"\n";
            topinfo += "播放 "+strView+"   弹幕 "+strDanmaku+"\n";
            topinfo += "简介 "+dscpt+"\n";
            topinfo += "\n";
            return topinfo;
        } else if(code==53016) {
            // 无置顶视频
        } else {
            BiliAPIs.outCodeErr(rawJson);
        }
        return "";
    }

    public static String spaceMasterpiece(String mid) {
        // 向 API 发送 GET 请求
        Logger.debugln("获取用户代表作");
        String rawJson = Http.get(BiliAPIs.USER_SPACE_MASTERPIECE+"?vmid="+mid);
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            // 提取返回信息
            String[] jsons = JsonLib.getArray(rawJson,"data");
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
                    String title = JsonLib.getString(json,"title"); // 标题
                    int allsec = JsonLib.getInt(json,"duration"); // 总时长(s)
                    int view = JsonLib.getInt(json,"stat","view"); // 播放
                    int danmaku = JsonLib.getInt(json,"stat","danmaku"); // 弹幕
                    // 处理信息
                    String playtime = OutFormat.time(allsec); // 总时长((hh:)mm:ss)
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
            BiliAPIs.outCodeErr(rawJson);
        }
        return "";
    }
    
    public static String offical(int typ, String... mid) {
        Logger.debugln("处理用户认证信息");
        String offical_tag = "unknown";
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
                Logger.errln("喜报");
                Logger.errln("未知的认证类型 "+typ);
                Logger.errln("请向 SocialSisterYi/bilibili-API-collect 与 SessionHu/SessBilinfo 提交 Issue, 提交前请确认使用最新版本且本项目最新修改在2个月内");
                Logger.errln("可以复制本信息: Bilibili 用户 Mid "+mid+" 的 认证类型(role) 为 "+typ+", 在[此页](https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/official_role.md)中似乎并没有找到, 希望尽早解决该问题");
                try {
                    Thread.sleep(9999);
                } catch(InterruptedException e) {}
        }
        return offical_tag;
    }
    
}
