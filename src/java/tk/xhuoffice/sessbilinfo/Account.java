package tk.xhuoffice.sessbilinfo;

import java.util.regex.Pattern;
import tk.xhuoffice.sessbilinfo.util.Error;
import tk.xhuoffice.sessbilinfo.util.Http;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

// API来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/user/check_nickname.md


public class Account {
    
    // 检查昵称是否可用
    public static final String CHECK_NICKNAME = "https://passport.bilibili.com/web/generic/check/nickname";
    
    public static void checkNickname() {
        // 定义变量
        String name = "";
        // 提示输入
        Logger.println("请输入要检查的昵称");
        while(true) {
            // 获取输入
            name = OutFormat.getString("昵称");
            // 检查输入是否合法
            if(iStandardNickname(name)) {
                // 提示信息
                Logger.println("正在检查...");
                // 输出信息
                outNicknameStatus(name);
                // 返回
                return;
            }
        }
    }
    
    public static boolean iStandardNickname(String name) {
        Logger.println("本地处理昵称");
        // 检查字符串长度
        int length = name.length();
        if(length<2||length>16) {
            Logger.warnln("昵称长度应在 2 ~ 16 之间");
            return false;
        }
        // 检查是否包含特殊字符
        Pattern pattern = Pattern.compile("[^\\p{L}\\p{N}_\\-\\p{IsHan}\\p{IsHangul}\\p{IsKatakana}\\p{IsHiragana}]");
        boolean containsOtherChars = pattern.matcher(name).find();
        if(containsOtherChars) {
            Logger.warnln("昵称包含除字母、数字、下划线(_)、连字符(-)、中日韩统一表意文字以外的字符");
            return false;
        }
        // 返回 true
        return true;
    }
    
    public static void outNicknameStatus(String name) {
        // 请求数据
        Logger.debugln("API处理昵称");
        String json = Http.get(CHECK_NICKNAME+"?nickName="+Http.encode(name));
        // 获取返回值
        int code = JsonLib.getInt(json,"code");
        // 输出信息
        if(code==0) {
            Logger.println("昵称未被注册");
        } else if(code==2001||code==40014) {
            Logger.println("昵称已存在");
        } else if(code==40002) {
            Logger.println("昵称包含敏感信息");
        } else if(code==40004) {
            Logger.println("昵称不可包含除-和_以外的特殊字符");
        } else if(code==40005) {
            Logger.println("昵称过长");
        } else if(code==40006) {
            Logger.println("昵称过短");
        } else {
            Error.out(json);
        }
    }
    
}
