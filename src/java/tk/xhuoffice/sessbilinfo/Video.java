package tk.xhuoffice.sessbilinfo;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import tk.xhuoffice.sessbilinfo.net.Downloader;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.AvBv;
import tk.xhuoffice.sessbilinfo.util.BiliAPIs;
import tk.xhuoffice.sessbilinfo.util.BiliException;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

/**
 * Get and download a video from Bilibili. Also can create an object of Video. <br>
 * 视频分区来源: <a href="https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/video/video_zone.md">bilibili-API-collect</a>
 */


public class Video implements Bilinfo {
    
    public static void simpleViewer() {
        Video video;
        video = getVideoInfo();
        downloadVideo(video);
    }
    
    public static Video getVideoInfo() {
        Frame.reset();
        // 提示信息
        Logger.println("请输入视频AV或BV号");
        // 获取AV号
        String aid = getAid();
        // 获取数据
        Logger.println("正在请求数据...");
        Map.Entry<Video,String> result = getDetail(aid);
        StringBuilder videoInfo = new StringBuilder();
        videoInfo.append("--------------------------------\n\n");
        videoInfo.append(result.getValue());
        videoInfo.append("--------------------------------");
        Logger.println("请求完毕");
        // 输出信息
        Frame.reset();
        Logger.println(videoInfo);
        return result.getKey();
    }

    public static String getAid() {
        String aid = "";
        AvBv avbv = new AvBv();
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
                aid = verifyAid(String.valueOf(avbv.bvidToAid(vid)));
            } else if(vid.length()==10) {
                // BV号(无bv头)
                aid = verifyAid(String.valueOf(avbv.bvidToAid("bv"+vid)));
            } else {
                aid = "";
            }
            if(!aid.isEmpty()) {
                Logger.clearFootln();
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
    
    public static Map.Entry<Video,String> getDetail(String aid) {
        Map.Entry<Video,String> result;
        Video video;
        try {
            video = new Video(Long.parseLong(aid));
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
            formatted.append(String.format("%s\n", video.title));
            formatted.append(String.format("%s-%s  %s   UP主 %s\n", video.mtname, video.tname, cprt, video.uploader));
            formatted.append(String.format("播放 %s   弹幕 %s   %s\n", view, danmaku, date));
            formatted.append(String.format("点赞 %s   投币 %s   收藏 %s   分享 %s\n", like, coin, fav, share));
            formatted.append(String.format("TAG: %s\n", tags));
            formatted.append(String.format("总时长 %s   评论 %s\n \n", alltime, reply));
            formatted.append(String.format("在线人数 %s\n", video.online));
            formatted.append(String.format("封面 %s\n", video.cover));
            // 处理视频流URL
            if(video.playURL!=null) {
                formatted.append("URL  ");
                formatted.append(OutFormat.shorterString(50,video.playURL));
            }
            // 返回数据
            result = new SimpleImmutableEntry<>(video,formatted.append("\n\n").toString());
        } catch(BiliException e) {
            result = new SimpleImmutableEntry<>(null,e.getDetailMessage()+"\n\n");
        }
        return result;
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
    private String json = null;
    
    public Video(long aid) {
        this(BiliAPIs.getViewDetail(String.valueOf(aid)));
    }
    
    public Video(String detailJson) {
        if(!detailJson.startsWith("{") || JsonLib.getInt(detailJson,"code")!=0) {
            throw BiliAPIs.codeErrExceptionBuilder(detailJson);
        }
        this.json = detailJson;
        // .data.View
        {
            // .
            this.bvid = JsonLib.getString(detailJson,"data","View","bvid");
            this.aid = JsonLib.getString(detailJson,"data","View","aid");
            this.cid = JsonLib.getString(detailJson,"data","View","cid");
            this.tname = JsonLib.getString(detailJson,"data","View","tname");
            this.mtname = tidSubToMain(JsonLib.getInt(detailJson,"data","View","tid"));
            this.original = JsonLib.getInt(detailJson,"data","View","copyright") == 1;
            this.title = JsonLib.getString(detailJson,"data","View","title");
            this.cover = JsonLib.getString(detailJson,"data","View","pic").replace("http","https");
            this.pubdate = JsonLib.getLong(detailJson,"data","View","pubdate");
            this.desc = JsonLib.getString(detailJson,"data","View","desc");
            this.duration = JsonLib.getLong(detailJson,"data","View","duration");
            // ..stat
            {
                this.view = JsonLib.getLong(detailJson,"data","View","stat","view");
                this.danmaku = JsonLib.getLong(detailJson,"data","View","stat","danmaku");
                this.reply = JsonLib.getLong(detailJson,"data","View","stat","reply");
                this.fav = JsonLib.getLong(detailJson,"data","View","stat","favorite");
                this.coin = JsonLib.getLong(detailJson,"data","View","stat","coin");
                this.share = JsonLib.getLong(detailJson,"data","View","stat","share");
                this.like = JsonLib.getLong(detailJson,"data","View","stat","like");
            }
            // play url & online number
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<String> playurl = executor.submit(() -> this.getPlayURL());
            Future<String> online = executor.submit(() -> this.getOnline());
            try {
                this.playURL = playurl.get();
                this.online = online.get();
            } catch(InterruptedException | java.util.concurrent.ExecutionException e) {
                OutFormat.outThrowable(e,3);
            }
            executor.shutdown();
        }
        try {
            // .data.Card.card
            this.mid = JsonLib.getLong(detailJson,"data","Card","card","mid");
            this.uploader = JsonLib.getString(detailJson,"data","Card","card","name");
            // .data.Tags
            List<String> tagName = new ArrayList<>();
            String[] arr = JsonLib.getArray(detailJson,"data","Tags");
            if(arr!=null){
                for(String tagjson : arr) {
                    tagName.add(JsonLib.getString(tagjson,"tag_name"));
                }
                this.tag = tagName.toArray(new String[0]);
            }
        } catch(NullPointerException e) {
            OutFormat.outThrowable(e,2);
        }
    }

    /**
     * Get video stream url.
     * @return video stream url
     */
    public String getPlayURL() {
        // 发送请求
        String rawJson = BiliAPIs.getViewPlayURL(this.aid,this.cid);
        // 处理返回值
        if(JsonLib.getInt(rawJson,"code")==0) {
            // 处理返回数据
            return JsonLib.getString(JsonLib.getArray(rawJson,"data","durl")[0],"url");
        }
        return null;
    }

    /**
     * Get video online number.
     * @return video online number
     */
    public String getOnline() {
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
            return sb.toString();
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.title+"@"+this.aid;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1454;
        result = prime * result + this.toJson().hashCode();
        result = prime * result + this.toString().hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj==this) {
            return true;
        }
        if(obj==null) {
            return false;
        }
        if(obj instanceof Video) {
            Video v = (Video)obj;
            return v.hashCode() == this.hashCode();
        }
        return false;
    }
    
    @Override
    public String toJson() {
        return this.json;
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
    
    public static void downloadVideo(Video video) {
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
        try {
            Downloader dl = new Downloader(video.playURL,path,video.toString()+".mp4");
            Logger.println("开始下载");
            dl.download();
        } catch(java.io.IOException e) {
            Logger.errln("从 "+(video.playURL.toString())+" 下载时发生异常");
            OutFormat.outThrowable(e,3);
        }
    }
    
}
