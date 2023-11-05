package tk.xhuoffice.sessbilinfo;

import java.util.regex.Pattern;
import tk.xhuoffice.sessbilinfo.net.Http;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.BiliAPIs;
import tk.xhuoffice.sessbilinfo.util.JsonLib;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Account {
    
    public static void checkNickname() {
        Frame.reset();
        // 定义变量
        String name = "";
        // 提示输入
        Frame.reset();
        Logger.println("请输入要检查的昵称");
        while(true) {
            // 获取输入
            name = OutFormat.getString("昵称");
            // 检查输入是否合法
            if(iStandardNickname(name)) {
                Logger.clearFootln();
                // 提示信息
                Logger.println("正在检查...");
                // 输出信息
                outNicknameStatus(name);
                // 返回
                return;
            }
        }
    }
    
    private static boolean iStandardNickname(String name) {
        Logger.debugln("本地处理昵称");
        // 检查字符串长度
        int length = name.length();
        if(length<2||length>16) {
            Logger.footln("昵称长度应在 2 ~ 16 之间");
            return false;
        }
        // 检查是否包含特殊字符
        Pattern pattern = Pattern.compile("[^\\p{L}\\p{N}_\\-\\p{IsHan}\\p{IsHangul}\\p{IsKatakana}\\p{IsHiragana}]");
        if(pattern.matcher(name).find()) {
            Logger.footln("昵称包含除字母、数字、下划线(_)、连字符(-)、中日韩统一表意文字以外的字符");
            return false;
        }
        // 返回 true
        return true;
    }
    
    public static void outNicknameStatus(String name) {
        // 请求数据
        Logger.debugln("API处理昵称");
        String json = Http.get(BiliAPIs.ACCOUNT_CHECK_NICKNAME+"?nickName="+Http.encode(name));
        // 获取返回值
        int code = JsonLib.getInt(json,"code");
        // 输出信息
        Frame.reset();
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
            BiliAPIs.outCodeErr(json);
        }
    }
    
    public static void ipLocation() {
        Frame.reset();
        // 发送请求
        Logger.println("正在请求数据...");
        String json = Http.get(BiliAPIs.IP_LOCATION);
        // 解析返回数据
        if(JsonLib.getInt(json,"code")==0) {
            // 提取信息
            String ip = JsonLib.getString(json,"data","addr"); // 公网IP地址
            String country = JsonLib.getString(json,"data","country"); // 国家/地区
            String province = JsonLib.getString(json,"data","province"); // 省/州
            String city = JsonLib.getString(json,"data","city"); // 城市
            String isp = JsonLib.getString(json,"data","isp"); // 运营商
            float latitude = JsonLib.getFloat(json,"data","latitude"); // 纬度
            float longitude = JsonLib.getFloat(json,"data","longitude"); // 经度
            int countryCode = JsonLib.getInt(json,"data","country_code"); // 国家/地区代码
            // 处理信息
            String ns = ""; // 纬度
            if(latitude>=0) {
                ns = latitude+"°N";
            } else {
                ns = latitude+"°S";
            }
            String ew = ""; // 经度
            if(longitude>=0) {
                ew = longitude+"°E";
            } else {
                ew = longitude+"°W";
            }
            // 准备信息
            String info = "--------------------------------\n";
            info += "公网 IP:       "+ip+"\n";
            info += "国家/地区:     "+country+"\n";
            if(province!=null) {
                info += "省/州:         "+province+"\n";
                if(city!=null) {
                    info += "城市:          "+city+"\n";
                }
            }
            info += "经纬度:        "+ew+" "+ns+"\n";
            info += "运营商:        "+isp+"\n";
            info += "国家/地区代码: "+countryCode+"\n";
            info += "--------------------------------\n";
            // 打印信息
            Logger.println("请求完毕");
            Frame.reset();
            Logger.println(info);
        } else {
            BiliAPIs.outCodeErr(json);
        }
    }
    
}
