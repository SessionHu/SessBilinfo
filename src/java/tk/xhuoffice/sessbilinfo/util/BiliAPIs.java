package tk.xhuoffice.sessbilinfo.util;

import java.util.Map;
import java.util.HashMap;

/**
 * API来源:
 *     https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/info.md
 *     https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/space.md
 *     https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/search/search_request.md
 *     https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/check_nickname.md
 *     https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/video/info.md
 *     https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/clientinfo/ip.md
 * 信息来源:
 *     https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/errcode.md
 */


public class BiliAPIs {
    
    // 基本API
    public static final String BASE_URL = "https://api.bilibili.com/x";
    // 用户名片信息
    public static final String USER_CARD = BASE_URL+"/web-interface/card";
    // 用户空间公告
    public static final String USER_SPACE_NOTICE = BASE_URL+"/space/notice";
    // 用户空间个人TAG
    public static final String USER_SPACE_TAG = BASE_URL+"/space/acc/tags";
    // 用户空间置顶视频
    public static final String USER_SPACE_TOP = BASE_URL+"/space/top/arc";
    // 用户空间代表作
    public static final String USER_SPACE_MASTERPIECE = BASE_URL+"/space/masterpiece";
    // 搜索综合
    public static final String SEARCH_ALL = BASE_URL+"/web-interface/search/all/v2";
    // 检查昵称是否可用
    public static final String ACCOUNT_CHECK_NICKNAME = "https://passport.bilibili.com/web/generic/check/nickname";
    // 视频超详细信息
    public static final String VIEW_DETAIL = BASE_URL+"/web-interface/view/detail";
    // 通过ip确定地理位置
    public static final String IP_LOCATION = BASE_URL+"/web-interface/zone";
    
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
    
    public static String codeErr(int code) {
        // 根据情况返回结果
        return ERRMSG.getOrDefault(
                // 输入值
                code,
                // 若输入值不符合前面的内容输出下面
                "\n未知的错误代码 "+code
                );
    }
    
}