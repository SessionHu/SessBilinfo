package tk.xhuoffice.sessbilinfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tk.xhuoffice.sessbilinfo.net.Http;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.BiliAPIs;
import tk.xhuoffice.sessbilinfo.util.BiliException;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

// 信息来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/search/search_response.md


public class Search {
    
    public static void search() {
        Frame.reset();
        // 获取搜索内容
        Logger.println("请输入关键词 (不区分大小写)");
        String keyword = OutFormat.getString("关键词");
        // 进行搜索
        try {
            // 输出提示
            Logger.println("正在请求数据...");
            // 获取数据
            String result = "";
            result += "------------------------\n\n";
            result += all(keyword);
            result += "------------------------";
            Logger.println("请求完毕");
            // 输出结果
            Frame.reset();
            Logger.println(result);
            // 返回
            return;
        } catch(BiliException e) {
            Logger.errln(e.toString());
            return;
        } catch(Exception e) {
            Logger.fataln("搜索发生未知异常");
            OutFormat.outThrowable(e,4);
        }
    }
    
    public static String all(String keyword) {
        // 初始化变量
        String results = "";
        // 用户输入是否为mid
        if(keyword.matches(".*uid.*|.*mid.*") || keyword.length()==16) {
            // 获取字符串中的mid
            Logger.debugln("尝试获取字符串中 Mid");
            Matcher matcher = Pattern.compile("\\d+").matcher(keyword);
            if(matcher.find()) {
                // 提取出mid
                String mid = matcher.group(); 
                Logger.debugln("尝试提取 Mid");
                // 验证mid是否有效
                if(Long.parseLong(mid) > 0){
                    // 提示信息
                    Logger.println("检测到您的输入为 Mid, 操作变为获取用户信息");
                    // 获取并返回信息   
                    String usrInfo = "";
                    usrInfo += UserInfo.card(mid);
                    usrInfo += UserInfo.space(mid);
                    return usrInfo;
                }
            }
        }
        // 发送请求
        String rawJson = Http.get(BiliAPIs.SEARCH_ALL+"?keyword="+Http.encode(keyword));
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            // 获取分类结果数目信息 (信息分类依赖API文档)
            Logger.debugln("获取分类结果数目信息");
            // int liveRoomCount = JsonLib.getInt(rawJson,"data","top_tlist","live_room"); // 直播
            // int topicCount = JsonLib.getInt(rawJson,"data","top_tlist","topic"); // 话题
            String videoCount = null; { // 视频
                int video = JsonLib.getInt(rawJson,"data","top_tlist","video");
                videoCount = OutFormat.num(video);
            }
            String biliUserCount = null; { // 用户
                int biliUser = JsonLib.getInt(rawJson,"data","top_tlist","bili_user");
                biliUserCount = OutFormat.num(biliUser);
            }
            // int mediaFtCount = JsonLib.getInt(rawJson,"data","top_tlist","media_ft"); // 电影
            // int articleCount = JsonLib.getInt(rawJson,"data","top_tlist","article"); // 专栏
            // int mediaBangumiCount = JsonLib.getInt(rawJson,"data","top_tlist","media_bangumi"); // 番剧
            // int liveCount = JsonLib.getInt(rawJson,"data","top_tlist","live"); // 直播间
            // int activityCount = JsonLib.getInt(rawJson,"data","top_tlist","activity"); // 活动
            // int liveUserCount = JsonLib.getInt(rawJson,"data","top_tlist","live_user"); // 主播
            // 获取查询结果
            String[] result = JsonLib.getArray(rawJson,"data","result");
            for(int i = 0; i < result.length; i++) {
                String json = result[i];
                // 用户
                results += getUserSearchResult(json,biliUserCount);
                // 视频
                results += getVideoSearchResult(json,videoCount);
            }
        } else if(code==-412) {
            throw new BiliException("请求被拦截, 请检测 Cookie 长度");
        } else {
            BiliAPIs.outCodeErr(rawJson);
        }
        return results;
    }

    private static String getUserSearchResult(String json, String biliUserCount) {
        String results = "";
        if(JsonLib.getString(json,"result_type").equals("bili_user")) {
            Logger.debugln("获取用户信息");
            try {
                // 获取 data 数组作为 JSON
                String usrJson = JsonLib.getArray(json,"data")[0]; // 默认仅使用第一个结果
                // 解析数据
                String usrMid = JsonLib.getString(usrJson,"mid"); // mid
                String usrName = JsonLib.getString(usrJson,"uname"); // 昵称
                String usrSign = JsonLib.getString(usrJson,"usign"); // 签名
                int usrLevel = JsonLib.getInt(usrJson,"level"); // 等级
                int usrFans = JsonLib.getInt(usrJson,"fans"); // 粉丝数
                int usrVideos = JsonLib.getInt(usrJson,"videos"); // 视频数
                int usrOfficalType = JsonLib.getInt(usrJson,"official_verify","type"); // 认证类型
                // 处理数据
                String usrStrFans = OutFormat.num(usrFans); // 粉丝数
                String usrStrVideos = OutFormat.num(usrVideos); // 视频数
                String usrOfficalMsg = ""; { // 认证信息
                    if(usrOfficalType==0) {
                        // 个人认证
                        usrOfficalMsg += "个人认证: ";
                        usrOfficalMsg += JsonLib.getString(usrJson,"official_verify","desc");
                    } else if(usrOfficalType==1) {
                        // 组织认证
                        usrOfficalMsg += "组织认证: ";
                        usrOfficalMsg += JsonLib.getString(usrJson,"official_verify","desc");
                    }
                }
                // 输出数据
                String usrInfo = "";
                usrInfo += "共搜索到约 " + biliUserCount + " 个用户\n";
                usrInfo += "1. Mid: " + usrMid + "\n";
                usrInfo += "   " + usrName + "   Lv" + usrLevel + "\n";
                usrInfo += "   粉丝 " + usrStrFans + "   视频 " + usrStrVideos + "\n";
                if(!usrOfficalMsg.trim().isEmpty()) {
                    usrInfo += "   " + usrOfficalMsg + "\n";
                } else {
                    usrInfo += "   " + usrSign + "\n";
                }
                usrInfo += "\n";
                results += usrInfo;
            } catch(ArrayIndexOutOfBoundsException e) {
                // 完全匹配的用户啥也没搜到
                results += "共搜索到约 " + biliUserCount + " 个用户\n";
                results += "无详细结果, 请进行用户搜索\n";
                results += "\n";
            }
        }
        return results;
    }

    private static String getVideoSearchResult(String json, String videoCount) {
        String results = "";
        if(JsonLib.getString(json,"result_type").equals("video")) {
            Logger.debugln("获取视频信息");
            // 获取 data 数组作为 JSON
            String[] videoJson = JsonLib.getArray(json,"data");
            // 初始化变量
            String vInfo = "";
            vInfo += "共搜索到约 " + videoCount + " 个视频\n";
            for(int v = 0; v < videoJson.length; v++) {
                Logger.debugln("读取第 "+(v+1)+" 个视频");
                // 获取当前视频 JSON
                String vJson = videoJson[v];
                // 解析数据
                long aid = JsonLib.getLong(vJson,"id"); // avid
                long play = JsonLib.getLong(vJson,"play"); // 播放
                long videoReview = JsonLib.getLong(vJson,"video_review"); // 弹幕
                long senddate = JsonLib.getLong(vJson,"senddate"); // 发布时间
                String title = JsonLib.getString(vJson,"title"); // 标题
                String duration = JsonLib.getString(vJson,"duration"); // 时长((HH:)MM:SS)
                String author = JsonLib.getString(vJson,"author"); // UP主昵称
                // 处理数据
                String view = OutFormat.num(play); // 播放
                String danmaku = OutFormat.num(videoReview); // 弹幕
                String date = OutFormat.date(senddate); // 发布日期
                // 输出数据
                vInfo += String.format("%02d.",v+1) + " " + title + "\n";
                vInfo += "    " + duration + "    播放 " + view + "   弹幕 " + danmaku + "\n";
                vInfo += "    AV号 " + aid + "   UP主 " + author + "   " + date + "\n";
            }
            if(videoJson.length==0) {
                vInfo += "无结果, 请检查关键词\n";
            }
            vInfo += "\n";
            results += OutFormat.xmlToANSI(vInfo); 
        }
        return results;
    }
    
}