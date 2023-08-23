package tk.xhuoffice.sessbilinfo;

import tk.xhuoffice.sessbilinfo.Http;
import tk.xhuoffice.sessbilinfo.JsonLib;
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
        Logger.println("请输入关键词 (不区分大小写)",1);
        String keyword = OutFormat.getString("关键词").trim();
        // 进行搜索
        Logger.println("正在请求数据...",1);
        String result = "";
        try {
            result += "\n";
            result += "------------------------\n\n";
            result += all(keyword);
            result += "------------------------";
        } catch(Exception e) {
            Logger.println("搜索发生未知异常",4);
            OutFormat.outException(e,4);
            System.exit(2);
        }
        Logger.println("请求完毕",1);
        // 输出结果
        Logger.println(result,1);
    }
    
    public static String all(String keyword) throws Exception {
        // 初始化变量
        String results = "";
        // 发送请求
        String rawJson = Http.get(SEARCH_ALL+"?keyword="+Http.encode(keyword));
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0) {
            // 获取分类结果数目信息 (信息分类依赖API文档)
            // int liveRoomCount = JsonLib.getInt(rawJson,"data","top_tlist","live_room"); // 直播
            // int topicCount = JsonLib.getInt(rawJson,"data","top_tlist","topic"); // 话题
            // int videoCount = JsonLib.getInt(rawJson,"data","top_tlist","video"); // 视频
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
                if(JsonLib.getString(json,"result_type").equals("bili_user")) {
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
                                usrOfficalMsg += "组织认证";
                                usrOfficalMsg += JsonLib.getString(usrJson,"official_verify","desc");
                            }
                        }
                        // 输出数据
                        String usrInfo = "";
                        usrInfo += "共搜索到约 " + biliUserCount + " 个用户\n";
                        usrInfo += "1. Mid: " + usrMid + "\n";
                        usrInfo += "   " + usrName + "   Lv" + usrLevel + "\n";
                        usrInfo += "   粉丝 " + usrFans + "   视频 " + usrVideos + "\n";
                        usrInfo += "   " + usrOfficalMsg + "\n"; 
                        usrInfo += "\n";
                        results += usrInfo;
                    } catch(ArrayIndexOutOfBoundsException e) {
                        // 完全匹配的用户啥也没搜到
                        results += "共搜索到约 " + biliUserCount + " 个用户\n";
                        results += "无详细结果, 请进行 用户搜索 或 直接搜索 Mid\n";
                        results += "\n";
                    }
                }
            }
        } else if(code==-412) {
            Logger.println("请求被拦截, 请检测 Cookie 长度",3);
        } else {
            Error.out(rawJson);
        }
        return results;
    }
    
}