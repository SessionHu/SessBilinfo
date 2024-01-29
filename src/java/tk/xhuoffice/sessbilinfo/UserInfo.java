package tk.xhuoffice.sessbilinfo;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.BiliAPIs;
import tk.xhuoffice.sessbilinfo.util.BiliException;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

/**
 * Get User Information from Bilibili. <br>
 * 认证类型来源: <a href="https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/official_role.md">bilibili-API-collect</a>
 */


public class UserInfo implements Bilinfo {

    private static UserInfo usrcache = null;
    
    /**
     * Get formatted Bilibili User basic information.
     * Input from {@code Frame#screen}.
     * @return  Formatted Bilibili User basic information.
     */
    public static String getUserInfo() {
        Frame.reset();
        // 提示输入信息
        Logger.println(
                "请输入被查询用户的 Mid 信息\n"+
                "示例: 645769214");
        // 向用户获取 Mid
        long mid = Long.parseLong(OutFormat.getPositiveLongAsString("Mid"));
        // 获取数据
        UserInfo usr;
        if(usrcache!=null && usrcache.getMid()==mid) {
            usr = usrcache;
        } else {
            usr = new UserInfo(mid);
        }
        Logger.println("正在请求数据...");
        StringBuilder usrinfo = new StringBuilder("------------------------\n\n");
        try {
            usrinfo.append(usr.card());
            usrinfo.append(usr.space());
            usrinfo.append("------------------------");
            Logger.println("请求完毕");
            usrcache = usr;
        } catch(BiliException e) {
            usrinfo.append(e.getDetailMessage());
            usrinfo.append("\n\n------------------------");
        }
        // 输出数据
        Frame.reset();
        Logger.println(usrinfo);
        return usrinfo.toString();
    }

    /**
     * @return mid
     */
    public long getMid() {
        return this.mid;
    }
    private long mid;

    public UserInfo(long mid) {
        this.mid = mid;
    }

    @Override
    public String toJson() {
        JsonObject json = new JsonObject();
        // card
        json.add("card",JsonLib.GSON.fromJson(this.cardjson,JsonObject.class));
        // space
        JsonObject spacejson = new JsonObject();
        spacejson.add("notice",JsonLib.GSON.fromJson(this.spaceNoticeJson,JsonObject.class));
        spacejson.add("tag",JsonLib.GSON.fromJson(this.spaceTagJson,JsonObject.class));
        spacejson.add("top",JsonLib.GSON.fromJson(this.spaceTopJson,JsonObject.class));
        spacejson.add("masterpiece",JsonLib.GSON.fromJson(this.spaceMasterpieceJson,JsonObject.class));
        json.add("space",spacejson);
        return JsonLib.GSON.toJson(json);
    }

    public UserInfo(String rawjson) {
        JsonObject json = JsonLib.GSON.fromJson(rawjson,JsonObject.class);
        // card
        this.cardjson = json.get("card").getAsString();
        // space
        JsonObject space = json.getAsJsonObject("space");
        this.spaceNoticeJson = space.get("notice").getAsString();
        this.spaceTagJson = space.get("tag").getAsString();
        this.spaceTopJson = space.get("top").getAsString();
        this.spaceMasterpieceJson = space.get("masterpiece").getAsString();
    }

    private String cardinfo = null;
    private String cardjson = null;

    /**
     * @return 用户昵称
     */
    public String getNickname() {
        return this.nickname;
    }
    private String nickname;

    /**
     * @return 签名
     */
    public String getSign() {
        return this.sign;
    }
    private String sign;

    /**
     * @return 粉丝数
     */
    public int getFans() {
        return this.fans;
    }
    private int fans;

    /**
     * @return 当前等级
     */
    public short getLevel() {
        return this.level;
    }
    private short level;

    /**
     * @return 关注数
     */
    public int getFriend() {
        return this.friend;
    }
    private int friend;

    /**
     * @return 性别
     */
    public String getSex() {
        return this.sex;
    }
    private String sex;

    /**
     * @return 认证类型
     */
    public String getOfficalTag() {
        return this.officalTag;
    }
    private String officalTag = "";

    /**
     * @return 认证信息
     */
    public String getOfficalInfo() {
        return this.officalInfo;
    }
    private String officalInfo = "";
    
    public String card() {
        if(this.cardjson==null) {
            // 向 API 发送 GET 请求
            Logger.debugln("获取用户"+this.mid+"名片信息");
            this.cardjson = BiliAPIs.getUserCard(String.valueOf(this.mid));
        }
        // 获取返回值
        if(JsonLib.getInt(this.cardjson,"code")==0) {
            // 解析返回内容
            this.nickname = JsonLib.getString(this.cardjson,"data","card","name");
            this.sign = JsonLib.getString(this.cardjson,"data","card","sign");
            this.fans = JsonLib.getInt(this.cardjson,"data","card","fans");
            this.level = JsonLib.get(this.cardjson,short.class,"data","card","level_info","current_level");
            this.friend = JsonLib.getInt(this.cardjson,"data","card","friend");
            this.sex = JsonLib.getString(this.cardjson,"data","card","sex");
            if(JsonLib.getInt(this.cardjson,"data","card","Official","type")==0) { // 认证信息
                // 处理认证类型
                officalTag = offical(JsonLib.getInt(this.cardjson,"data","card","Official","role"),String.valueOf(this.mid));
                // 处理认证信息
                officalInfo = JsonLib.getString(this.cardjson,"data","card","Official","title"); // 认证内容
            }
            // 处理返回内容
            if(this.sign.trim().isEmpty()) { // 签名
                this.sign = "(这个人很神秘,什么都没有写)";
            }
            String strFans = OutFormat.num(this.fans); // 粉丝数
            String strFriend = OutFormat.num(this.friend); // 关注数
            if(this.sex.equals("保密")) { // 性别
                this.sex = "";
            }
            // 输出解析结果
            StringBuilder cardinfo = new StringBuilder();
            cardinfo.append("Lv"+this.level+"  "+this.nickname+"  "+this.sex+"\n");
            cardinfo.append("粉丝 "+strFans+"   关注 "+strFriend+"\n");
            if(!officalTag.isEmpty()) { // 有认证信息时打印
                cardinfo.append(this.officalTag+": "+this.officalInfo+"\n");
            }
            cardinfo.append("签名 "+this.sign+"\n");
            cardinfo.append("\n");
            return (this.cardinfo=cardinfo.toString());
        } else {
            throw BiliAPIs.codeErrExceptionBuilder(this.cardjson);
        }
    }

    private String spaceinfo = null;
    
    /**
     * Get formatted Bilibili User Space basic information.
     * @return Formatted Bilibili User Space basic information.
     */
    public String space() {
        if(spaceinfo!=null) {
            return spaceinfo;
        }
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Future<String> spaceTag = executor.submit(() -> this.spaceTag());
        Future<String> spaceNotice = executor.submit(() -> this.spaceNotice());
        Future<String> spaceTop = executor.submit(() -> this.spaceTop());
        Future<String> spaceMasterpiece = executor.submit(() -> this.spaceMasterpiece());
        StringBuilder result = new StringBuilder();
        try {
            result.append(spaceTag.get());
            result.append(spaceNotice.get());
            result.append(spaceTop.get());
            result.append(spaceMasterpiece.get());
        } catch(InterruptedException | java.util.concurrent.ExecutionException e) {
            OutFormat.outThrowable(e,3);
        }
        executor.shutdown();
        return this.spaceinfo=result.toString();
    }

    /**
     * @return 空间公告
     */
    public String getSpaceNotice() {
        return this.spaceNotice;
    }
    private String spaceNotice = "";
    private String spaceNoticeJson = null;
    
    /**
     * Get formatted Bilibili User Space Notice.
     * @return Formatted Bilibili User Space Notice.
     */
    public String spaceNotice() {
        if(!this.spaceNotice.isEmpty()) {
            return "空间公告\n"+this.spaceNotice;
        }
        if(this.spaceNoticeJson==null) {
            // 发送请求
            Logger.debugln("获取用户"+this.mid+"空间公告");
            this.spaceNoticeJson = BiliAPIs.getUserSpaceNotice(String.valueOf(this.mid));
        }
        // 解析返回信息
        if(JsonLib.getInt(this.spaceNoticeJson,"code")==0) {
            String data = JsonLib.getString(this.spaceNoticeJson,"data");
            // 处理返回结果
            if(data!=null && !data.trim().isEmpty()) {
                return "空间公告\n"+(this.spaceNotice=data);
            }
        } else {
            // 输出错误信息
            BiliAPIs.codeErrExceptionBuilder(this.spaceNoticeJson).outDetailMessage(2);
        }
        return "";
    }

    /**
     * @return 空间TAG
     */
    public String getSpaceTag() {
        return this.spaceTag;
    }
    private String spaceTag = "";
    private String spaceTagJson = null;
    
    /**
     * Get formatted Bilibili User Space Tags.
     * @return Formatted Bilibili User Space Tags.
     */
    public String spaceTag() {
        if(spaceTagJson==null) {
            // 发送请求
            Logger.debugln("获取用户"+this.mid+"空间TAG");
            spaceTagJson = BiliAPIs.getUserSpaceTag(String.valueOf(this.mid));
        }
        if(JsonLib.getInt(spaceTagJson,"code")==0) {
            try {
                // 提取返回信息
                String data = JsonLib.getArray(spaceTagJson,"data")[0];
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
                    return (this.spaceTag=listag);
                }
            } catch(NullPointerException e) {
                // 空指针异常
                // 一般是 tags 为空导致的
                // 不需要处理
            }
        } else {
            // 输出错误
            BiliAPIs.codeErrExceptionBuilder(spaceTagJson).outDetailMessage(2);
        }
        return "";
    }

    /**
     * @return 空间置顶视频
     */
    public Video getSpaceTop() {
        return this.spaceTop;
    }
    private Video spaceTop = null;
    private String spaceTopJson = null;
    
    /**
     * Get formatted Bilibili User Space Top Video basic imfomation.
     * @return Formatted Bilibili User Space Top Video basic imfomation.
     */
    public String spaceTop() {
        if(this.spaceTop==null) {
            if(spaceTopJson==null) {
                // 向 API 发送 GET 请求
                Logger.debugln("获取用户"+this.mid+"置顶视频");
                this.spaceTopJson = BiliAPIs.getUserSpaceTop(String.valueOf(this.mid));
            }
            // 获取返回值
            int code = JsonLib.getInt(this.spaceTopJson,"code");
            if(code==0) {
                // 构建Video信息
                JsonObject data = JsonLib.get(this.spaceTopJson,JsonObject.class,"data");
                String json = "{\"data\":{\"View\":"+JsonLib.GSON.toJson(data)+"}}";
                this.spaceTop = new Video(json);
            } else if(code==53016) {
                // 无置顶视频
                return "";
            } else {
                BiliAPIs.codeErrExceptionBuilder(this.spaceTopJson).outDetailMessage(2);
                return "";
            }
        }
        // 处理返回信息
        String playtime = OutFormat.time(this.spaceTop.duration); // 总时长((hh:m)m:ss)
        String strView = OutFormat.num(this.spaceTop.view); // 播放
        String strDanmaku = OutFormat.num(this.spaceTop.danmaku); // 弹幕
        String date = OutFormat.date(this.spaceTop.pubdate); // 发布时间
        String desc = OutFormat.formatString(this.spaceTop.desc,"     ");
        // 输出处理结果
        StringBuilder topinfo = new StringBuilder();
        topinfo.append("置顶视频\n");
        topinfo.append("标题 "+this.spaceTop.title+"\n");
        topinfo.append("AV号 "+this.spaceTop.aid+"   "+date+"   时长 "+playtime+"\n");
        topinfo.append("播放 "+strView+"   弹幕 "+strDanmaku+"\n");
        topinfo.append("简介 "+desc+"\n\n");
        return topinfo.toString();
    }
    
    /**
     * @return 空间置顶视频
     */
    public Video[] getSpaceMasterpiece() {
        return this.spaceMasterpiece.toArray(new Video[0]);
    }
    private List<Video> spaceMasterpiece = new ArrayList<>();
    private String spaceMasterpieceJson = null;
    
    /**
     * Get formatted Bilibili User Space Top Video basic imfomation.
     * @return Formatted Bilibili User Space Top Video basic imfomation.
     */
    public String spaceMasterpiece() {
        if(this.spaceMasterpiece.isEmpty()) {
            if(spaceMasterpieceJson==null) {
                // 向 API 发送 GET 请求
                Logger.debugln("获取用户代表作");
                this.spaceMasterpieceJson = BiliAPIs.getUserSpaceMasterpiece(String.valueOf(this.mid));
            }
            // 获取返回值
            int code = JsonLib.getInt(this.spaceMasterpieceJson,"code");
            if(code==0) {
                // 提取返回信息
                String[] jsons = JsonLib.getArray(this.spaceMasterpieceJson,"data");
                // 处理返回信息
                if(jsons.length>0) {
                    this.spaceMasterpiece.clear();
                    // 依次处理信息
                    for(int i = 0; i < jsons.length; i++) {
                        // 构建Video信息
                        String json = "{\"data\":{\"View\":"+jsons[i]+"}}";
                        this.spaceMasterpiece.add(new Video(json));
                    }
                } else {
                    return "";
                }
            } else {
                BiliAPIs.codeErrExceptionBuilder(this.spaceMasterpieceJson).outDetailMessage(2);
                return "";
            }
        }
        StringBuilder result = new StringBuilder("代表作\n");
        for(int i = 0; i < this.spaceMasterpiece.size(); i++) {
            Video video = this.spaceMasterpiece.get(i);
            // 处理信息
            String playtime = OutFormat.time(video.duration); // 总时长((hh:)mm:ss)
            String strView = OutFormat.num(video.view); // 播放
            String strDanmaku = OutFormat.num(video.danmaku); // 弹幕
            // 输出信息
            result.append((i+1) + ". " + video.title + "\n");
            result.append("   " + playtime + "   播放 " + strView + "   弹幕 " + strDanmaku + "\n");
        }
        // 返回信息
        result.append("\n");
        return result.toString();
    }
    
    /**
     * A Map can convert Bilibili offical type number id to text description. */
    public static final Map<Integer, String> OFFICAL_TYPE = new HashMap<>();
    static {
        // 个人认证
        OFFICAL_TYPE.put(1,"UP主(知名UP主)认证");
        OFFICAL_TYPE.put(2,"UP主(大V达人)认证");
        OFFICAL_TYPE.put(7,"UP主(高能主播)认证");
        OFFICAL_TYPE.put(9,"UP主(社会知名人士)认证");
        // 机构认证
        OFFICAL_TYPE.put(3,"机构(企业)认证");
        OFFICAL_TYPE.put(4,"机构(组织)认证");
        OFFICAL_TYPE.put(5,"机构(媒体)认证");
        OFFICAL_TYPE.put(6,"机构(政府)认证");
    }
    
    public static String offical(int typ, String... mid) {
        Logger.debugln("处理用户认证信息");
        String officalTag = OFFICAL_TYPE.getOrDefault(typ,"unknown");
        if(officalTag.equals("unknown")) {
            // 我也不知道 role 为 8 时是什么
            Logger.errln("喜报");
            Logger.errln("未知的认证类型 "+typ);
            Logger.errln("请向 SocialSisterYi/bilibili-API-collect 与 SessionHu/SessBilinfo 提交 Issue, 提交前请确认使用最新版本且本项目最新修改在2个月内");
            Logger.errln("可以复制本信息: Bilibili 用户 Mid "+mid+" 的 认证类型(role) 为 "+typ+", 在[此页](https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/official_role.md)中似乎并没有找到, 希望尽早解决该问题");
            try {
                Thread.sleep(8888);
            } catch(InterruptedException e) {
                OutFormat.outThrowable(e,2);
            }
        }
        return officalTag;
    }

}
