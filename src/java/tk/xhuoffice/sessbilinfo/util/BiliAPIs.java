package tk.xhuoffice.sessbilinfo.util;

import com.google.gson.JsonObject;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import tk.xhuoffice.sessbilinfo.net.Http;
import tk.xhuoffice.sessbilinfo.net.HttpConnectException;
import tk.xhuoffice.sessbilinfo.net.StringCoder;

/**
 * APIs about Bilibili. <br>
 * <br>
 * API来源: <a href="https://github.com/SocialSisterYi/bilibili-API-collect">SocialSisterYi/bilibili-API-collect</a>
 */


public class BiliAPIs {
    
    // NO <init>
    private BiliAPIs() {}

    /** 
     * Base HTTP GET request with string param.
     * @param apiurl  API URL
     * @param args    param string
     * @return Response from server <br> {@code null} if {@link tk.xhuoffice.sessbilinfo.net.HttpConnectException} caught
     */
    public static String get(String apiurl, String... args) {
        // string to map
        Map<String,String> map = new HashMap<>();
        for(String param : args) {
            // if param can be use
            String[] pair = param.split("=");
            if(pair.length>1) {
                // param value
                String val = "";
                for(int i = 1; i < pair.length; i++) {
                    val += pair[i];
                }
                // put param to map
                map.put(pair[0],val);
            }
        }
        // get & return
        return get(apiurl,map);
    }

    /**
     * Base HTTP GET request with map param.
     * @param apiurl  API URL
     * @param map     param map
     * @return Response from server <br> {@code null} if {@link tk.xhuoffice.sessbilinfo.net.HttpConnectException} caught
     */
    public static String get(String apiurl, Map<String,String> map) {
        // build request url
        StringBuilder url = new StringBuilder(apiurl);
        if(url.toString().contains("wbi")) {
            // wbi
            url.append("?");
            url.append(toWbiParams(map));
        } else if(map.size()>0) {
            // map to string
            url.append("?");
            for(Map.Entry<String,String> param : map.entrySet()) {
                url.append(param.getKey());
                url.append("=");
                url.append(Http.encode(param.getValue()));
                url.append("&");
            }
            url.deleteCharAt(url.length()-1);
        }
        // request
        String response;
        try {
            response = Http.get(url.toString());
        } catch(HttpConnectException e) {
            Logger.errln("请求异常: "+e.getCause().toString());
            // if Logger.debug == true
            if(Logger.debug) {
                OutFormat.outThrowable(e.getCause(),0);
            }
            // generate response json
            JsonObject json = new JsonObject();
            json.addProperty("code",-8888);
            json.addProperty("message",e.getMessage());
            response = JsonLib.GSON.toJson(json);
        }
        // return
        return response;
    }
    
    /**
     * 基本API */
    public static final String BASE_URL = "https://api.bilibili.com/x";
    
    /**
     * 用户名片信息 */
    public static final String USER_CARD = BASE_URL+"/web-interface/card";
    /**
     * GET 用户名片信息.
     * @param mid  user mid
     * @return     json from API */
    public static String getUserCard(String mid) {
        return get(USER_CARD,"mid="+mid);
    }
     
    /**
     * 用户空间公告 */
    public static final String USER_SPACE_NOTICE = BASE_URL+"/space/notice";
    /**
     * GET 用户空间公告.
     * @param mid  user mid
     * @return     json from API */
    public static String getUserSpaceNotice(String mid) {
        return get(USER_SPACE_NOTICE,"mid="+mid);
    }
    
    /**
     * 用户空间个人TAG */
    public static final String USER_SPACE_TAG = BASE_URL+"/space/acc/tags";
    /**
     * GET 用户空间个人TAG.
     * @param mid  user mid
     * @return     json from API */
    public static String getUserSpaceTag(String mid) {
        return get(USER_SPACE_TAG,"mid="+mid);
    }
    
    /**
     * 用户空间置顶视频 */
    public static final String USER_SPACE_TOP = BASE_URL+"/space/top/arc";
    /**
     * GET 用户空间置顶视频.
     * @param vmid  user mid
     * @return      json from API */
    public static String getUserSpaceTop(String vmid) {
        return get(USER_SPACE_TOP,"vmid="+vmid);
    }
    
    /**
     * 用户空间代表作 */
    public static final String USER_SPACE_MASTERPIECE = BASE_URL+"/space/masterpiece";
    /**
     * GET 用户空间代表作.
     * @param vmid  user mid
     * @return      json from API */
    public static String getUserSpaceMasterpiece(String vmid) {
        return get(USER_SPACE_MASTERPIECE,"vmid="+vmid);
    }
    
    /**
     * 搜索综合 */
    public static final String SEARCH_ALL = BASE_URL+"/web-interface/search/all/v2";
    /**
     * GET 搜索综合.
     * @param keyword  keyword
     * @return         json from API */
    public static String getSearchAll(String keyword) {
        return get(SEARCH_ALL,"keyword="+keyword);
    }
    
    /**
     * 检查昵称是否可用 */
    public static final String ACCOUNT_CHECK_NICKNAME = "https://passport.bilibili.com/web/generic/check/nickname";
    /**
     * GET 检查昵称是否可用.
     * @param nickName  nickName
     * @return          json from API */
    public static String getAccountCheckNickname(String nickName) {
        return get(ACCOUNT_CHECK_NICKNAME,"nickName="+nickName);
    }
    
    /**
     * 视频超详细信息 */
    public static final String VIEW_DETAIL = BASE_URL+"/web-interface/view/detail";
    /**
     * GET 视频超详细信息.
     * @param aid  aid
     * @return     json from API */
    public static String getViewDetail(String aid) {
        return get(VIEW_DETAIL,"aid="+aid);
    }
    
    /**
     * 获取视频在线人数 */
    public static final String VIEW_ONLINE = BASE_URL+"/player/online/total";
    /**
     * GET 获取视频在线人数.
     * @param aid  aid
     * @param cid  cid
     * @return     json from API */
    public static String getViewOnline(String aid, String cid) {
        return get(VIEW_ONLINE,"aid="+aid,"cid="+cid);
    }
    
    /**
     * 获取视频流URL */
    public static final String VIEW_PLAY_URL = BASE_URL+"/player/wbi/playurl";
    /**
     * GET 获取视频流URL. (wbi)
     * @param avid  aid
     * @param cid   cid
     * @return      json from API */
    public static String getViewPlayURL(String avid, String cid) {
        return get(VIEW_PLAY_URL,"avid="+avid,"cid="+cid,"platform=html5");
    }
    
    /**
     * 通过ip确定地理位置 */
    public static final String IP_LOCATION = BASE_URL+"/web-interface/zone";
    /**
     * GET 通过ip确定地理位置.
     * @return  json from API */
    public static String getIpLocation() {
        return get(IP_LOCATION);
    }

    /**
     * 导航栏用户信息. */
    public static final String NAV = BASE_URL + "/web-interface/nav";
    /**
     * GET 导航栏用户信息.
     * @return json from API */
    public static String getNav() {
        return get(NAV);
    }

    private static final int[] mixinKeyEncTab = new int[]{
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
        33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
        61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
        36, 20, 34, 44, 52
    };

    /**
     * Get MixinKey for Wbi sign from Nav.
     * @return {@code null} if any exceptions caught
     */
    public static String getMixinKey() {
        // get json from Nav
        String nav = BiliAPIs.getNav();
        // if server did not respose as usual
        int code = JsonLib.getInt(nav,"code");
        if(code==-8888 || !(code==-101 || code==0)) {
            BiliAPIs.codeErrExceptionBuilder(nav).outDetailMessage();
            return null;
        }
        // get name
        String imgName,subName;
        try {
            // get uri
            URI imgUrl,subUrl;
            imgUrl = new URI(JsonLib.getString(nav,"data","wbi_img","img_url"));
            subUrl = new URI(JsonLib.getString(nav,"data","wbi_img","sub_url"));
            // get name
            imgName = Paths.get(imgUrl.getPath()).getFileName().toString();
            subName = Paths.get(subUrl.getPath()).getFileName().toString();
        } catch(java.net.URISyntaxException e) {
            OutFormat.outThrowable(e,3);
            return null;
        }
        // get keys
        String s = imgName.substring(0,imgName.length()-5)
                 + subName.substring(0,subName.length()-5);
        StringBuilder key = new StringBuilder();
        for(int i = 0; i < 32; i++) {
            key.append(s.charAt(mixinKeyEncTab[i]));
        }
        return key.toString();
    }

    private static String mixinKeyCache = null;

    /**
     * Convent param map to param string and add Wbi sign.
     * @param map  param map
     * @return     param string with Wbi sign
     * @see <a href="https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/sign/wbi.md">WBI 签名</a>
     */
    public static String toWbiParams(Map<String,String> map) {
        // get mixin key
        final String mixinKey;
        if(mixinKeyCache==null) {
            mixinKey = mixinKeyCache = getMixinKey();
        } else {
            mixinKey = mixinKeyCache;
        }
        // wts
        map.put("wts",String.valueOf(System.currentTimeMillis()/1000L));
        // 排序 + 拼接字符串
        StringJoiner params = new StringJoiner("&");
        map.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> params.add(entry.getKey()+"="+Http.encode(entry.getValue().toString().toUpperCase())));
        // return
        return params.add("w_rid="+StringCoder.md5(params+mixinKey)).toString();
   }

    /**
     * Print summary with code from Bilibili API.
     * @deprecated Poor performance and complex to use.
     * @param      rawJson json from Bilibili API
     * @return     Summary printed on screen
     * @see        #codeErrExceptionBuilder(String)
     * @see        BiliException#outDetailMessage()
     */
    @Deprecated
    public static String outCodeErr(String rawJson) {
        // 获取错误
        int code = JsonLib.getInt(rawJson,"code");
        String msg = JsonLib.getString(rawJson,"message");
        // 输出错误信息
        String summary = code+" "+codeErr(code);
        Logger.errln("返回值: "+summary);
        Logger.errln("错误信息: "+msg);
        // 返回code
        return summary;
    }
    
    /**
     * Build {@code BiliException} with JSON from Bilibili API.
     * @param rawJson  json from Bilibili API
     * @return         {@code BiliException} with detailed message.
     */
    public static BiliException codeErrExceptionBuilder(String rawJson) {
        // 获取错误
        int code = JsonLib.getInt(rawJson,"code");
        String msg = JsonLib.getString(rawJson,"message");
        // 构建错误信息
        String message = codeErrMsgBuilder(code);
        StringBuilder detailMessage = new StringBuilder();
        detailMessage.append("返回值: ");
        detailMessage.append(message);
        detailMessage.append("\n");
        detailMessage.append("错误信息: ");
        detailMessage.append(msg);
        // 返回
        return new BiliException(message,detailMessage.toString());
    }

    private static String codeErrMsgBuilder(int code) {
        StringBuilder message = new StringBuilder();
        message.append(code);
        message.append(" ");
        message.append(BiliAPIs.codeErr(code));
        return message.toString();
    }
    
    /**
     * A Map for convert code from Bilibili API to message. */
    public static final Map<Integer,String> ERRMSG = new HashMap<>();
    static {
        // -1 ~ -115 的 code 多半用不上
        // 权限类
        ERRMSG.put(-1,  "应用程序不存在或已被封禁");
        ERRMSG.put(-2,  "Access Key 错误");
        ERRMSG.put(-3,  "API 校验密匙错误");
        ERRMSG.put(-4,  "调用方对该 Method 没有权限");
        ERRMSG.put(-101,"账号未登录");
        ERRMSG.put(-102,"账号被封停");
        ERRMSG.put(-103,"积分不足");
        ERRMSG.put(-104,"硬币不足");
        ERRMSG.put(-105,"验证码错误");
        ERRMSG.put(-106,"账号非正式会员或在适应期");
        ERRMSG.put(-107,"应用不存在或者被封禁");
        ERRMSG.put(-108,"未绑定手机");
        ERRMSG.put(-110,"未绑定手机");
        ERRMSG.put(-111,"csrf 校验失败");
        ERRMSG.put(-112,"系统升级中");
        ERRMSG.put(-113,"账号尚未实名认证");
        ERRMSG.put(-114,"请先绑定手机");
                // 好臭的 code, 这和 -108 与 -110 没区别吧
        ERRMSG.put(-115,"请先完成实名认证");
                // 这和 -113 有区别吗
        // 请求类
        ERRMSG.put(-304,"木有改动");
        ERRMSG.put(-307,"撞车跳转");
        ERRMSG.put(-400,"请求错误");
        ERRMSG.put(-401,"未认证 (或非法请求)");
        ERRMSG.put(-403,"访问权限不足");
        ERRMSG.put(-404,"啥都木有");
        ERRMSG.put(-405,"不支持该方法");
        ERRMSG.put(-409,"冲突");
        ERRMSG.put(-412,"请求被拦截 (客户端 IP 被服务端风控)");
        ERRMSG.put(-500,"服务器错误");
        ERRMSG.put(-503,"过载保护, 服务暂不可用");
        ERRMSG.put(-504,"服务调用超时");
        ERRMSG.put(-509,"超出限制");
        ERRMSG.put(-616,"上传文件不存在");
        ERRMSG.put(-617,"上传文件太大");
        ERRMSG.put(-625,"登录失败次数太多");
        ERRMSG.put(-626,"用户不存在");
        ERRMSG.put(-628,"密码太弱");
        ERRMSG.put(-629,"用户名或密码错误");
        ERRMSG.put(-632,"操作对象数量限制");
        ERRMSG.put(-643,"被锁定");
        ERRMSG.put(-650,"用户等级太低");
        ERRMSG.put(-652,"重复的用户");
        ERRMSG.put(-658,"Token 过期");
        ERRMSG.put(-662,"密码时间戳过期");
        ERRMSG.put(-688,"地理区域限制 (请检查梯子)");
        ERRMSG.put(-689,"版权限制");
        ERRMSG.put(-701,"扣节操失败");
        ERRMSG.put(-799,"请求过于频繁，请稍后再试");
        ERRMSG.put(-8888,"对不起，服务器开小差了~ (ಥ﹏ಥ)");
    }
    
    
    /**
     * Convert code from Bilibili API to message.
     * @param  code  code from Bilibili API
     * @return       Error message about code if code can be found in {@code ERRMSG}
     */
    public static String codeErr(int code) {
        // 根据情况返回结果
        return ERRMSG.getOrDefault(code,"");
    }
    
}
