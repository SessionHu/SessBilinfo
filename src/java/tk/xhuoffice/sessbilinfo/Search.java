package tk.xhuoffice.sessbilinfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.AvBv;
import tk.xhuoffice.sessbilinfo.util.BiliAPIs;
import tk.xhuoffice.sessbilinfo.util.BiliException;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

/**
 * Search information on Bilibili with its APIs. <br>
 * 信息来源: <a href="https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/search/search_response.md">bilibili-API-collect</a>
 */


public class Search {
    
    public static void searchAll() {
        Frame.reset();
        // 获取搜索内容
        Logger.println("请输入关键词 (不区分大小写)");
        String keyword = OutFormat.getString("关键词");
        // 进行搜索
        StringBuilder result = new StringBuilder();
        try {
            // 输出提示
            Logger.println("正在请求数据...");
            // 获取数据
            result.append("------------------------\n \n");
            result.append(redirect(keyword));
            result.append("------------------------");
            Logger.println("请求完毕");
            // 输出结果
            String[] pages = OutFormat.pageBreak(result.toString());
            for(int p = 0; p < pages.length; p++) {
                Frame.reset();
                Logger.println(pages[p]);
                if(p!=pages.length-1) {
                    Logger.enter2continue();
                }
            }
            // 返回
            return;
        } catch(BiliException e) {
            result.append(e.getDetailMessage());
            result.append("\n \n------------------------");
            Logger.errln(result.toString());
            return;
        } catch(Exception e) {
            Logger.fataln("搜索发生未知异常");
            OutFormat.outThrowable(e,4);
        }
    }

    /**
     * Rediret search result.
     * @param keyword  search keyword
     * @return         {@code ""} if cannot be redirected
     */
    public static String redirect(String keyword) {
        // 用户输入是否为mid
        String lwk = keyword.toLowerCase();
        if(lwk.startsWith("mid") || lwk.startsWith("uid") || keyword.length()==16) {
            // 获取字符串中的mid
            Logger.debugln("尝试获取字符串中 Mid");
            Matcher matcher = Pattern.compile("^\\d+$").matcher(keyword);
            if(matcher.find()) {
                // 提取出mid
                String mid = matcher.group(); 
                Logger.debugln("尝试提取 Mid");
                // 验证mid是否有效
                if(Long.parseLong(mid) > 0){
                    // 提示信息
                    Logger.println("检测到您的输入为 Mid, 操作变为获取用户信息");
                    // 获取并返回信息
                    UserInfo usr = new UserInfo(Long.parseLong(mid));
                    String usrInfo = "";
                    usrInfo += usr.card();
                    usrInfo += usr.space();
                    return usrInfo;
                }
            }
        }
        // check input if aid or bvid
        if(lwk.startsWith("av")) {
            // get aid in string
            Logger.debugln("尝试获取字符串中 Aid");
            Matcher matcher = Pattern.compile("\\d+").matcher(keyword);
            if(matcher.find()) {
                // 提取出aid
                String aid = matcher.group(); 
                Logger.debugln("尝试提取 Aid");
                // 验证mid是否有效
                if(Long.parseLong(aid) > 0){
                    // 提示信息
                    Logger.println("检测到您的输入为 Aid, 操作变为获取视频信息");
                    // 获取并返回信息
                    return Video.getDetail(aid).getValue();
                }
            }
        } else if(lwk.startsWith("bv") && keyword.length()==12) {
            // get bvid in string
            Logger.println("检测到您的输入为 Bvid, 操作变为获取视频信息");
            return Video.getDetail(String.valueOf(new AvBv(keyword).getAid())).getValue();
        }
        // all
        return new Search().new All(keyword).getAllSearchResult();
    }

    public class All implements Bilinfo {

        // json
        private String json;

        // count
        int liveRoomCount; // 直播
        int topicCount; // 话题
        int videoCount; // 视频
        int biliUserCount; // 用户
        int mediaFtCount; // 电影
        int articleCount; // 专栏
        int mediaBangumiCount; // 番剧
        int liveCount; // 直播间
        int activityCount; // 活动
        int liveUserCount; // 主播

        /**
         * Search all.
         * @param keyword  keyword
         */
        public All(String keyword) {
            // 发送请求
            this.json = BiliAPIs.getSearchAll(keyword);
            // 获取返回值
            int code = JsonLib.getInt(this.json,"code");
            if(code==0) {
                // 获取分类结果数目信息 (信息分类依赖API文档)
                Logger.debugln("获取分类结果数目信息");
                this.liveRoomCount = JsonLib.getInt(this.json,"data","top_tlist","live_room"); // 直播
                this.topicCount = JsonLib.getInt(this.json,"data","top_tlist","topic"); // 话题
                this.videoCount = JsonLib.getInt(this.json,"data","top_tlist","video"); // 视频
                this.biliUserCount = JsonLib.getInt(this.json,"data","top_tlist","bili_user"); // 用户
                this.mediaFtCount = JsonLib.getInt(this.json,"data","top_tlist","media_ft"); // 电影
                this.articleCount = JsonLib.getInt(this.json,"data","top_tlist","article"); // 专栏
                this.mediaBangumiCount = JsonLib.getInt(this.json,"data","top_tlist","media_bangumi"); // 番剧
                this.liveCount = JsonLib.getInt(this.json,"data","top_tlist","live"); // 直播间
                this.activityCount = JsonLib.getInt(this.json,"data","top_tlist","activity"); // 活动
                this.liveUserCount = JsonLib.getInt(this.json,"data","top_tlist","live_user"); // 主播
            } else if(code==-110) {
                throw new BiliException("-110 未绑定手机", "错误信息:   -110 未绑定手机\n可能的原因: 输入的信息包含敏感词");
            } else if(code==-111) {
                throw new BiliException("-111 csrf 校验失败", "错误信息:   -111 csrf 校验失败\n可能的原因: 输入的信息包含敏感词");
            } else if(code==-412) {
                throw new BiliException("请求被拦截, 请检测 Cookie 长度");
            } else {
                throw BiliAPIs.codeErrExceptionBuilder(this.json);
            }
        }

        @Override
        public String toJson() {
            return this.json;
        }

        /**
         * 获取查询结果.
         * @return result
         */
        public String getAllSearchResult() {
            String results = "";
            String[] result = JsonLib.getArray(this.json,"data","result");
            for(int i = 0; i < result.length; i++) {
                this.resultJson = result[i];
                // 用户
                results += getUserSearchResult();
                // 视频
                results += getVideoSearchResult();
            }
            return results;
        }

        private String resultJson;
        
        private String userSearchResult = "";

        private String getUserSearchResult() {
            if(!JsonLib.getString(this.resultJson,"result_type").equals("bili_user")) {
                return "";
            }
            if(!this.userSearchResult.isEmpty()) {
                return this.userSearchResult;
            }
            String results = "";
            Logger.debugln("获取用户信息");
            try {
                // 获取 data 数组作为 JSON
                String usrJson = JsonLib.getArray(this.resultJson,"data")[0]; // 默认仅使用第一个结果
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
                usrInfo += "共搜索到约 " + OutFormat.num(this.biliUserCount) + " 个用户\n";
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
                results += "共搜索到约 " + OutFormat.num(this.biliUserCount) + " 个用户\n";
                results += "无详细结果, 请进行用户搜索\n \n";
            }
            return this.userSearchResult = results;
        }

        private String videoSearchResult = "";

        private String getVideoSearchResult() {
            if(!JsonLib.getString(this.resultJson,"result_type").equals("video")) {
                return "";
            }
            if(!this.videoSearchResult.isEmpty()) {
                return this.videoSearchResult;
            }
            String results = "";
            Logger.debugln("获取视频信息");
            // 获取 data 数组作为 JSON
            String[] videoJson = JsonLib.getArray(this.resultJson,"data");
            // 初始化变量
            String vInfo = "";
            vInfo += "共搜索到约 " + OutFormat.num(this.videoCount) + " 个视频\n";
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
            vInfo += " \n";
            results += OutFormat.xmlToANSI(vInfo); 
            return this.videoSearchResult = results;
        }

    }

}
