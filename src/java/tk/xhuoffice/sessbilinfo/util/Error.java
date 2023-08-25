package tk.xhuoffice.sessbilinfo.util;

import java.util.Map;
import java.util.HashMap;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.JsonLib;

// 信息来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/errcode.md

public class Error {
    
    public static void out(String rawJson) {
        // 获取错误
        int code = JsonLib.getInt(rawJson,"code");
        String msg = JsonLib.getString(rawJson,"message");
        // 输出错误信息
        Logger.println("返回值: "+code+" "+code(code),3);
        Logger.println("错误信息: "+msg,3);
    }
    
    public static String code(int code) {
        Map<Integer,String> errMsg = new HashMap<>();
        // -1 ~ -115 的 code 多半用不上
        // 权限类
        errMsg.put(-1,  "应用程序不存在或已被封禁");
        errMsg.put(-2,  "Access Key 错误");
        errMsg.put(-3,  "API 校验密匙错误");
        errMsg.put(-4,  "调用方对该 Method 没有权限");
        errMsg.put(-101,"账号未登录");
        errMsg.put(-102,"账号被封停");
        errMsg.put(-103,"积分不足");
        errMsg.put(-104,"硬币不足");
        errMsg.put(-105,"验证码错误");
        errMsg.put(-106,"账号非正式会员或在适应期");
        errMsg.put(-107,"应用不存在或者被封禁");
        errMsg.put(-108,"未绑定手机");
        errMsg.put(-110,"未绑定手机");
        errMsg.put(-111,"csrf 校验失败");
        errMsg.put(-112,"系统升级中");
        errMsg.put(-113,"账号尚未实名认证");
        errMsg.put(-114,"请先绑定手机");
                // 好臭的 code, 这和 -108 与 -110 没区别吧
        errMsg.put(-115,"请先完成实名认证");
                // 这和 -113 有区别吗
        // 请求类
        errMsg.put(-304,"木有改动");
        errMsg.put(-307,"撞车跳转");
        errMsg.put(-400,"请求错误");
        errMsg.put(-401,"未认证 (或非法请求)");
        errMsg.put(-403,"访问权限不足");
        errMsg.put(-404,"啥都木有");
        errMsg.put(-405,"不支持该方法");
        errMsg.put(-409,"冲突");
        errMsg.put(-412,"请求被拦截 (客户端 IP 被服务端风控)");
        errMsg.put(-500,"服务器错误");
        errMsg.put(-503,"过载保护, 服务暂不可用");
        errMsg.put(-504,"服务调用超时");
        errMsg.put(-509,"超出限制");
        errMsg.put(-616,"上传文件不存在");
        errMsg.put(-617,"上传文件太大");
        errMsg.put(-625,"登录失败次数太多");
        errMsg.put(-626,"用户不存在");
        errMsg.put(-628,"密码太弱");
        errMsg.put(-629,"用户名或密码错误");
        errMsg.put(-632,"操作对象数量限制");
        errMsg.put(-643,"被锁定");
        errMsg.put(-650,"用户等级太低");
        errMsg.put(-652,"重复的用户");
        errMsg.put(-658,"Token 过期");
        errMsg.put(-662,"密码时间戳过期");
        errMsg.put(-688,"地理区域限制 (请检查梯子)");
        errMsg.put(-689,"版权限制");
        errMsg.put(-701,"扣节操失败");
        errMsg.put(-799,"请求过于频繁，请稍后再试");
        errMsg.put(-8888,"对不起，服务器开小差了~ (ಥ﹏ಥ)");
        // 根据情况返回结果
        return errMsg.getOrDefault(
                // 输入值
                code,
                // 若输入值不符合前面的内容输出下面
                "\n未知的错误代码 "+code
                );
    }
    
}