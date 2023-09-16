package tk.xhuoffice.sessbilinfo;

import java.util.HashMap;
import java.util.Map;
import tk.xhuoffice.sessbilinfo.util.AvBv;
import tk.xhuoffice.sessbilinfo.util.BiliAPIs;
import tk.xhuoffice.sessbilinfo.util.BiliException;
import tk.xhuoffice.sessbilinfo.util.Http;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

// 视频分区来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/video/video_zone.md


public class Video {
    
    public static void getVideoInfo() {
        // 提示信息
        Logger.println("请输入视频AV或BV号");
        // 获取AV号
        String aid = getAid();
        // 获取数据
        Logger.println("正在请求数据...");
        String videoInfo = "";
        try {
            videoInfo += "\n";
            videoInfo += "------------------------\n\n";
            videoInfo += getDetail(aid);
            videoInfo += "------------------------";
        } catch(BiliException e) {
            return; // 返回
        }
        Logger.println("请求完毕");
        // 输出信息
        Logger.println(videoInfo);
    }

    public static String getAid() {
        String aid = "";
        while(true) {
            String vid = OutFormat.getString("AV或BV号");
            try {
                if(vid.toLowerCase().startsWith("av")) {
                    // AV号(avid)
                    aid = verifyAid(vid.substring(2,vid.length()));
                } else if(vid.matches("\\d+")) {
                    // AV号(aid)
                    aid = verifyAid(vid);
                } else if(vid.toLowerCase().startsWith("bv")&&vid.length()==12) {
                    // BV号(标准12位)
                    Logger.debugln("转换BV号为AV号");
                    aid = String.valueOf(new AvBv(vid).aid);
                    if(Integer.valueOf(aid)>0) {
                        aid = verifyAid(aid);
                    } else {
                        throw new NullPointerException();
                    }
                } else if(vid.length()==10) {
                    // BV号(无bv头)
                    Logger.debugln("转换BV号为AV号");
                    aid = String.valueOf(new AvBv("bv"+vid).aid);
                    if(Integer.valueOf(aid)>0) {
                        aid = verifyAid(aid);
                    } else {
                        throw new NullPointerException();
                    }
                } else {
                    Logger.warnln("无效的输入");
                    aid = "";
                }
            } catch(NullPointerException e) {
                Logger.warnln("无效的输入");
                aid = "";
            }
            if(aid==null||aid.trim().isEmpty()) {
                // nothing here...
            } else {
                Logger.debugln("返回 aid");
                return aid;
            }
        }
    }

    private static String verifyAid(String aid) {
        // AV号
        try {
            // 验证AV号
            Logger.debugln("验证AV号");
            return OutFormat.getPositiveLongAsString("AV号",aid);
        } catch(NumberFormatException e) {
            // AV号无效
            return "";
        }
    }
    
    public static String getDetail(String aid) {
        // 发送请求
        String rawJson = Http.get(BiliAPIs.VIEW_DETAIL+"?aid="+aid);
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0){
            // 解析返回数据
            Video video = new Video(rawJson);
            // 处理解析数据
            String cprt = ""; { // 视频类型
                if(video.original) {
                    cprt = "原创";
                } else {
                    cprt = "转载";
                }
            }
            String date = OutFormat.fullDateTime(video.pubdate); // 发布日期
            String alltime = OutFormat.time(video.duration); // 总时长
            String view = OutFormat.num(video.view); // 播放量
            String danmaku = OutFormat.num(video.danmaku); // 弹幕数
            String reply = OutFormat.num(video.reply); // 评论数
            String fav = OutFormat.num(video.fav); // 收藏数
            String coin = OutFormat.num(video.coin); // 投币数
            String share = OutFormat.num(video.share); // 分享数
            String like = OutFormat.num(video.like); // 点赞数
            String tags = ""; { // TAG
                try {
                    for(int i = 0; i < video.tag.length; i++) {
                        Logger.debugln("第 "+i+" 个 TAG");
                        tags += video.tag[i]+", ";
                    }
                    tags = tags.substring(0,tags.length()-2);
                } catch(StringIndexOutOfBoundsException e) {
                    // 看起来似乎没有TAG
                    tags = "无";
                }
            }
            // 格式化处理数据
            String formatted = "";
            formatted += String.format("%s\n", video.title);
            formatted += String.format("%s-%s  %s   UP主 %s\n", video.mtname, video.tname, cprt, video.uploader);
            formatted += String.format("播放 %s   弹幕 %s   %s\n", view, danmaku, date);
            formatted += String.format("点赞 %s   投币 %s   收藏 %s   分享 %s\n", like, coin, fav, share);
            formatted += String.format("TAG: %s\n", tags);
            formatted += String.format("总时长 %s   评论 %s", alltime, reply);
            // 返回数据
            return formatted+"\n\n";
        } else if(code==62002) {
            return "稿件不可见\n\n";
        } else if(code==62004) {
            return "稿件审核中\n\n";
        } else {
            // 抛出异常
            throw new BiliException(BiliAPIs.outCodeErr(rawJson));
        }
    }
    
    public String bvid;
    public long aid;
    public String tname; // 子分区名
    public String mtname; // 主分区名
    public boolean original; // 视频类型
    public String title;
    public long pubdate; // 发布时间
    public String desc; // 视频简介
    public long duration; // 总时长
    public long view;
    public long danmaku;
    public long reply; // 评论数
    public long fav; // 收藏数
    public long coin; // 投币数
    public long share; // 分享数
    public long like; // 点赞数
    public String uploader; // UP主
    public long mid; // UP主Mid
    public String[] tag;
    
    public Video(long aid) {
        videoVars(Http.get(BiliAPIs.VIEW_DETAIL+"?aid="+aid));
    }
    
    public Video(String json) {
        videoVars(json);
    }
    
    public void videoVars(String json) {
        // .data.View
        {
            // .
            bvid = JsonLib.getString(json,"data","View","bvid");
            aid = JsonLib.getLong(json,"data","View","aid");
            tname = JsonLib.getString(json,"data","View","tname");
            mtname = tidSubToMain(JsonLib.getInt(json,"data","View","tid"));
            original = JsonLib.getInt(json,"data","View","copyright") == 1;
            title = JsonLib.getString(json,"data","View","title");
            pubdate = JsonLib.getLong(json,"data","View","pubdate");
            desc = JsonLib.getString(json,"data","View","desc");
            duration = JsonLib.getLong(json,"data","View","duration");
            // ..stat
            {
                view = JsonLib.getLong(json,"data","View","stat","view");
                danmaku = JsonLib.getLong(json,"data","View","stat","danmaku");
                reply = JsonLib.getLong(json,"data","View","stat","reply");
                fav = JsonLib.getLong(json,"data","View","stat","favorite");
                coin = JsonLib.getLong(json,"data","View","stat","coin");
                share = JsonLib.getLong(json,"data","View","stat","share");
                like = JsonLib.getLong(json,"data","View","stat","like");
            }
        }
        // .data.Card
        {
            // ..card
            {
                mid = JsonLib.getLong(json,"data","Card","card","mid");
                uploader = JsonLib.getString(json,"data","Card","card","name");
            }
        }
        // .data.Tags
        {
            String[] tagJson = JsonLib.getArray(json,"data","Tags");
            String[] tagName = new String[tagJson.length];
            for(int i = 0; i < tagJson.length; i++) {
                tagName[i] = JsonLib.getString(tagJson[i],"tag_name");
            }
            tag = tagName;
        }
    }
    
    public static String tidSubToMain(int tid) {
        Map<Integer,String> zone = new HashMap<>();
        int[][] data = {
            {1,24,25,47,210,86,253,27}, // 动画
            {13,51,152,32,33}, // 番剧
            {167,153,168,169,170,195}, // 国创
            {3,28,31,30,59,193,29,130,243,244,194}, // 音乐
            {129,20,154,156,198,199,200}, // 舞蹈
            {4,17,171,172,65,173,121,136,19}, // 游戏
            {36,201,124,228,207,208,209,229,122,39,96,98}, // 知识
            {188,95,230,231,232,233,189,190,191}, // 科技
            {234,235,249,164,236,237,238}, // 运动
            {223,245,246,247,248,240,227,176,224,225,226}, // 汽车
            {160,138,250,251,239,161,162,21,163,174}, // 生活
            {211,76,212,213,214,215}, // 美食
            {217,218,219,220,221,222,75}, // 动物圈
            {119,22,26,126,216,127}, // 鬼畜
            {155,157,252,158,159,192}, // 时尚
            {202,203,204,205,206}, // 资讯
            // {165,166}, // 广告
            {5,71,241,242,137,131}, // 娱乐
            {181,182,183,85,184}, // 影视
            {177,37,178,179,180}, // 纪录片
            {23,147,145,146,83}, // 电影
            {11,185,187} // 电视剧
        };
        String[] labels = {
            "动画","番剧","国创","音乐","舞蹈","游戏","知识","科技","运动",
            "汽车","生活","美食","动物圈","鬼畜","时尚","资讯",/*"广告",*/
            "娱乐","影视","纪录片","电影","电视剧"
        };
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[i].length; j++) {
                zone.put(data[i][j], labels[i]);
            }
        }
        // 根据情况返回结果
        return zone.getOrDefault(
                // 输入值
                tid,
                // 若输入值不符合前面的内容输出下面
                "\n未知的子分区 "+tid
                );
    }    
    
}