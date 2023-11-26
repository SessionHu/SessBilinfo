package tk.xhuoffice.sessbilinfo;

import java.util.HashMap;
import java.util.Map;
import tk.xhuoffice.sessbilinfo.net.Downloader;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.AvBv;
import tk.xhuoffice.sessbilinfo.util.BiliAPIs;
import tk.xhuoffice.sessbilinfo.util.BiliException;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

/**
 * Get and download a video from Bilibili. <br> Also can create an Object of Video. <br>
 * 视频分区来源: <a href="https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/video/video_zone.md">bilibili-API-collect</a>
 */


public class Video {
    
    private static volatile Video video = null;
    
    public static void simpleViewer() {
        video = null;
        getVideoInfo();
        downloadVideo();
    }
    
    public static void getVideoInfo() {
        Frame.reset();
        // 提示信息
        Logger.println("请输入视频AV或BV号");
        // 获取AV号
        String aid = getAid();
        // 获取数据
        Logger.println("正在请求数据...");
        String videoInfo = "";
        try {
            videoInfo += "------------------------\n\n";
            videoInfo += getDetail(aid);
            videoInfo += "------------------------";
        } catch(BiliException e) {
            Logger.errln("获取视频信息时发生未知异常");
            OutFormat.outThrowable(e,3);
            return; // 返回
        }
        Logger.println("请求完毕");
        // 输出信息
        Frame.reset();
        Logger.println(videoInfo);
    }

    public static String getAid() {
        String aid = "";
        while(true) {
            String vid = OutFormat.getString("AV或BV号");
            if(vid.toLowerCase().startsWith("av")) {
                // AV号(avid)
                aid = verifyAid(vid.substring(2,vid.length()));
            } else if(vid.matches("\\d+")) {
                // AV号(aid)
                aid = verifyAid(vid);
            } else if(vid.toLowerCase().startsWith("bv")&&vid.length()==12) {
                // BV号(标准12位)
                Logger.debugln("转换BV号为AV号");
                aid = verifyAid(String.valueOf(new AvBv(vid).getAid()));
            } else if(vid.length()==10) {
                // BV号(无bv头)
                Logger.debugln("转换BV号为AV号");
                aid = verifyAid(String.valueOf(new AvBv("bv"+vid).getAid()));
            } else {
                Logger.footln("无效的输入");
                aid = "";
            }
            if(!aid.isEmpty()) {
                Logger.clearFootln();
                Logger.debugln("返回 aid");
                return aid;
            } else {
                Logger.footln("无效的输入");
            }
        }
    }
    
    public static String verifyAid(String aid) {
        if(Long.parseLong(aid)>0) {
            return aid;
        } else {
            return "";
        }
    }
    
    public static String getDetail(String aid) {
        // 发送请求
        String rawJson = BiliAPIs.getViewDetail(aid);
        // 获取返回值
        int code = JsonLib.getInt(rawJson,"code");
        if(code==0){
            // 解析返回数据
            video = new Video(rawJson);
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
                    for(String t : video.tag) {
                        tags += t + ", ";
                    }
                    tags = tags.substring(0,tags.length()-2);
                } catch(StringIndexOutOfBoundsException e) {
                    // 看起来似乎没有TAG
                    tags = "无";
                }
            }
            // 格式化处理数据
            StringBuilder formatted = new StringBuilder();
            while(!video.ready) {}
            formatted.append(String.format("%s\n", video.title));
            formatted.append(String.format("%s-%s  %s   UP主 %s\n", video.mtname, video.tname, cprt, video.uploader));
            formatted.append(String.format("播放 %s   弹幕 %s   %s\n", view, danmaku, date));
            formatted.append(String.format("点赞 %s   投币 %s   收藏 %s   分享 %s\n", like, coin, fav, share));
            formatted.append(String.format("TAG: %s\n", tags));
            formatted.append(String.format("总时长 %s   评论 %s\n\n", alltime, reply));
            formatted.append(String.format("在线人数 %s\n", video.online));
            formatted.append(String.format("封面 %s\n", video.cover));
            // 处理视频流URL
            if(video.playURL!=null) {
                formatted.append("URL ");
                formatted.append(video.playURL);
            }
            // 返回数据
            return formatted.append("\n\n").toString();
        } else if(code==62002) {
            return "稿件不可见\n\n";
        } else if(code==62004) {
            return "稿件审核中\n\n";
        } else {
            return BiliAPIs.outCodeErr(rawJson)+"\n\n";
        }
    }
    
    public String bvid;
    public String aid;
    public String cid;
    public String tname; // 子分区名
    public String mtname; // 主分区名
    public boolean original; // 视频类型
    public String title;
    public String cover; // 封面
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
    public volatile String playURL = null;
    public volatile String online = null;
    public volatile boolean ready;
    
    public Video(long aid) {
        String detailJson = BiliAPIs.getViewDetail(String.valueOf(aid));
        try {
            videoVars(detailJson);
        } catch(Exception e) {
            throw new BiliException(BiliAPIs.outCodeErr(detailJson),e);
        }
    }
    
    public Video(String detailJson) {
        try {
            videoVars(detailJson);
        } catch(Exception e) {
            throw new BiliException(OutFormat.shorterString(detailJson),e);
        }
    }
    
    public void videoVars(String detailJson) {
        Thread videoStream;
        Thread videoOnline;
        // .data.View
        {
            // .
            bvid = JsonLib.getString(detailJson,"data","View","bvid");
            aid = JsonLib.getString(detailJson,"data","View","aid");
            cid = JsonLib.getString(detailJson,"data","View","cid");
            tname = JsonLib.getString(detailJson,"data","View","tname");
            mtname = tidSubToMain(JsonLib.getInt(detailJson,"data","View","tid"));
            original = JsonLib.getInt(detailJson,"data","View","copyright") == 1;
            title = JsonLib.getString(detailJson,"data","View","title");
            cover = JsonLib.getString(detailJson,"data","View","pic");
            pubdate = JsonLib.getLong(detailJson,"data","View","pubdate");
            desc = JsonLib.getString(detailJson,"data","View","desc");
            duration = JsonLib.getLong(detailJson,"data","View","duration");
            // get video stream url
            videoStream = new Thread(() -> {
                try {
                    // 发送请求
                    String rawJson = BiliAPIs.getViewPlayURL(this.aid,this.cid);
                    // 处理返回值
                    if(JsonLib.getInt(rawJson,"code")==0) {
                        // 处理返回数据
                        try {
                            video.playURL = JsonLib.getString(JsonLib.getArray(rawJson,"data","durl")[0],"url");
                        } catch(NullPointerException e) {}
                    }
                } catch(Exception e) {
                    OutFormat.outThrowable(e,0);
                }
            }, "VideoStream-"+this.aid);
            videoStream.start();
            // get online number
            videoOnline = new Thread(() -> {
                try {
                    // 发送请求
                    String rawJson = BiliAPIs.getViewOnline(this.aid,this.cid);
                    // 处理返回值
                    if(JsonLib.getInt(rawJson,"code")==0) {
                        // 处理返回数据
                        StringBuilder sb = new StringBuilder();
                        sb.append(JsonLib.getString(rawJson,"data","total"));
                        sb.append("(");
                        sb.append(JsonLib.getString(rawJson,"data","count"));
                        sb.append(")");
                        this.online = sb.toString();
                    }
                } catch(Exception e) {
                    OutFormat.outThrowable(e,0);
                }
            }, "VideoOnline-"+this.aid);
            videoOnline.start();
            // ..stat
            {
                view = JsonLib.getLong(detailJson,"data","View","stat","view");
                danmaku = JsonLib.getLong(detailJson,"data","View","stat","danmaku");
                reply = JsonLib.getLong(detailJson,"data","View","stat","reply");
                fav = JsonLib.getLong(detailJson,"data","View","stat","favorite");
                coin = JsonLib.getLong(detailJson,"data","View","stat","coin");
                share = JsonLib.getLong(detailJson,"data","View","stat","share");
                like = JsonLib.getLong(detailJson,"data","View","stat","like");
            }
        }
        // .data.Card
        {
            // ..card
            {
                mid = JsonLib.getLong(detailJson,"data","Card","card","mid");
                uploader = JsonLib.getString(detailJson,"data","Card","card","name");
            }
        }
        // .data.Tags
        {
            String[] tagJson = JsonLib.getArray(detailJson,"data","Tags");
            String[] tagName = new String[tagJson.length];
            for(int i = 0; i < tagJson.length; i++) {
                tagName[i] = JsonLib.getString(tagJson[i],"tag_name");
            }
            tag = tagName;
        }
        // 验证线程是否执行完毕
        new Thread(() -> {
            try {
                videoStream.join();
                videoOnline.join();
            } catch(InterruptedException e) {
            } finally {
                this.ready = true;
            }
        }, "VideoReadyReport").start();
    }
    
    @Override
    public String toString() {
        return this.title+"@av"+this.aid;
    }
    
    public static final Map<Integer,String> ZONE = new HashMap<>();
    static {
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
            {160,138,250,251,239,161,162,21,163,174,254}, // 生活
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
                ZONE.put(data[i][j], labels[i]);
            }
        }
    }
    
    public static String tidSubToMain(int tid) {
        // 根据情况返回结果
        return ZONE.getOrDefault(
                // 输入值
                tid,
                // 若输入值不符合前面的内容输出下面
                String.valueOf(tid)
                );
    }
    
    public static void downloadVideo() {
        if(video==null||video.playURL==null) {
            return;
        }
        // ask user if should download video
        Logger.println("是否下载视频[Y/N]");
        if(!OutFormat.getString("选项").toLowerCase().equals("y")) {
            return;
        }
        // download video
        String path = System.getProperty("user.home")+"/videos/";
        Logger.println("准备下载视频到 "+path);
        Downloader dl = new Downloader(video.playURL,path);
        Logger.println("开始下载");
        dl.download();
    }
    
}
