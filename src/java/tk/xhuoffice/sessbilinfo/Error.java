package tk.xhuoffice.sessbilinfo;

// 信息来源: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/errcode.md

public class Error {
    
    public static void code(int code) {
        // 0 ~ -115 的 code 多半用不上
        switch(code) {
            case 0:
                // 这段文本正常不可能被输出
                System.out.println("info: 成功");
                break;
            // 权限类
            case -1:
                System.err.println("应用程序不存在或已被封禁");
                break;
            case -2:
                System.err.println("Access Key 错误");
                break;
            case -3:
                System.err.println("API 校验密匙错误");
                break;
            case -4:
                System.err.println("调用方对该 Method 没有权限");
                break;
            case -101:
                System.err.println("账号未登录");
                break;
            case -102:
                System.err.println("账号被封停");
                break;
            case -103:
                System.err.println("积分不足");
                break;
            case -104:
                System.err.println("硬币不足");
                break;
            case -105:
                System.err.println("验证码错误");
                break;
            case -106:
                System.err.println("账号非正式会员或在适应期");
                break;
            case -107:
                System.err.println("应用不存在或者被封禁");
                break;
            case -108:
            case -110:
                System.err.println("未绑定手机");
                break;
            case -111:
                System.err.println("csrf 校验失败");
                break;
            case -112:
                System.err.println("系统升级中");
                break;
            case -113:
                System.err.println("账号尚未实名认证");
                break;
            case -114:
                // 好臭的 code
                // 这和 -108 与 -110 没区别吧
                System.err.println("请先绑定手机");
                break;
            case -115:
                // 这和 -113 有区别吗
                System.err.println("请先完成实名认证");
                break;
            // 请求类
            case -304:
                System.err.println("木有改动");
                break;
            case -307:
                System.err.println("撞车跳转");
                break;
            case -400:
                System.err.println("请求错误");
                break;
            case -401:
                System.err.println("未认证 (或非法请求)");
                break;
            case -403:
                System.err.println("访问权限不足");
                break;
            case -404:
                System.err.println("啥都木有");
                break;
            case -405:
                System.err.println("不支持该方法");
                break;
            case -409:
                System.err.println("冲突");
                break;
            case -412:
                System.err.println("请求被拦截 (客户端 ip 被服务端风控)");
                break;
            case -500:
                System.err.println("服务器错误");
                break;
            case -503:                
                System.err.println("过载保护, 服务暂不可用");
                break;
            case -504:
                System.err.println("服务调用超时");
                break;
            case -509:
                System.err.println("超出限制");
                break;
            case -616:
                System.err.println("上传文件不存在");
                break;
            case -617:
                System.err.println("上传文件太大");
                break;
            case -625:
                System.err.println("登录失败次数太多");
            case -626:
                System.err.println("用户不存在");
                break;
            case -628:
                System.err.println("密码太弱");
                break;
            case -629:
                System.err.println("用户名或密码错误");
                break;
            case -632:
                System.err.println("操作对象数量限制");
                break;
            case -643:
                System.err.println("被锁定");
                break;
            case -650:
                System.err.println("用户等级太低");
                break;
            case -652:
                System.err.println("重复的用户");
                break;
            case -658:
                System.err.println("Token 过期");
                break;
            case -662:
                System.err.println("密码时间戳过期");
                break;
            case -688:
                System.err.println("地理区域限制 (请检查梯子)");
                break;
            case -689:
                System.err.println("版权限制");
                break;
            case -701:
                System.err.println("扣节操失败");
                break;
            case -799:
                System.err.println("请求过于频繁，请稍后再试");
                break;
            case -8888:
                System.err.println("对不起，服务器开小差了~ (ಥ﹏ಥ)");
                break;
            // 其她情况
            default:
                System.err.println("fatal: 无效的错误代码 "+code);
        }
    }
    
}