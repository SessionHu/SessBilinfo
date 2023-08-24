package tk.xhuoffice.sessbilinfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.Logger;

public class OutFormat {
    
    public static Scanner scan = new Scanner(System.in);
    
    public static final ZoneId ZONE_HK = ZoneId.of("Asia/Hong_Kong");
    
    public static String time(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        String formattedTime;
        if (duration.toMinutes() >= 60) {
            formattedTime = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() % 60);
        } else {
            formattedTime = String.format("%02d:%02d", duration.toMinutes(), duration.getSeconds() % 60);
        }
        return formattedTime;
    }
    
    public static String num(long num) {
        return NumberFormat.getInstance(Locale.US).format(num);
    }
    
    public static String date(long timestamp) {
        // 读取时间戳
        Instant instant = Instant.ofEpochSecond(timestamp);
        // 转换为日期
        LocalDateTime date = LocalDateTime.ofInstant(instant,ZONE_HK);
        int y = date.getYear(); // 年
        int m = date.getMonthValue(); // 月
        int d = date.getDayOfMonth(); // 日
        int cY = LocalDateTime.now(ZONE_HK).getYear(); // 当前年份
        // 返回数据
        if(y==cY) {
            return m+"-"+d;
        } else {
            return y+"-"+m+"-"+d;
        }
    }
    
    public static String formatString(String origin, String add) {
        return origin.replaceAll("\\n","\n"+add);
    }
    
    public static String getString(String typ, String... tip) {
        // 获取输入
        while(true) {
            try {
                // 输出提示
                if(tip.length!=0) {
                    Logger.inputHere(tip[0]);
                } else {
                    Logger.inputHere();
                }
                // 读取控制台输入
                String str = scan.nextLine();
                // 为空时的处理
                if(str==null||str.trim().isEmpty()) {
                    Logger.println(typ+"不能为空",2);
                } else {
                    return str;
                }
            } catch(Exception e) {
                // 异常处理(退出)
                Logger.ln();
                Logger.println("非法的输入",4);
                System.exit(1);
            }
        }
    }

    public static String getPositiveLongAsString(String typ, String... num) throws NumberFormatException {
        // 定义并初始化变量
        String input = "";
        while(true) {
            try {
                if(num.length!=0) {
                    // 读取参数
                    input = num[0];
                } else {
                    // 提示输入
                    Logger.inputHere();
                    // 获取输入
                    input = scan.nextLine().trim();
                }
            } catch(Exception e) {
                // 异常处理
                Logger.ln();
                Logger.println("无效的 "+typ,4);
                System.exit(1);
            }
            try {
                // 将输入转换为 long
                long mid = Long.parseLong(input);
                // 检测输入是否大于0
                if(mid>0) {
                    // 提示并返回结果
                    Logger.println(typ+": "+mid,1);
                    return input;
                } else {
                    // 输出警告
                    Logger.println("无效的 "+typ+" "+input,2);
                }
            } catch(Exception e) {
                // 输出警告
                Logger.println("过大或非数字不能作为 "+typ,2);
            }
            if(num.length!=0) {
                Logger.println("抛出异常",0);
                throw new NumberFormatException("无效的 "+typ+" "+input);
            }
        }
    }
    
    public static void outException(Exception e, int l) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        Logger.println(stackTrace,l);
    }
    
    public static String xmlToANSI(String text) {
        // 获取系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        // Windows 环境
        if (osName.contains("windows")) {
            // 是否使用 Windows Terminal
            String wts = System.getenv("WT_SESSION");
            if (wts != null && !wts.trim().isEmpty()) {
                // 转换为富文本
                text = xmlToRichText(text);
            } else {
                // 转换为纯文本
                text = xmlToPlainText(text, true);
            }
        } else {
            // 其他系统直接转换
            text = xmlToRichText(text);
        }
        return text;
    }    

    private static String xmlToRichText(String text) {
        // <em>CONTENT</em> -> \033[0;1mCONTENT\033[0m
        text = text.replaceAll("\u003cem.*?\u003e", "\033[0;1m");
        text = text.replaceAll("\u003c/em\u003e", "\033[0m");
        // &amp; -> &
        text = text.replaceAll("&amp;", "&");
        // &quot; -> "
        text = text.replaceAll("&quot;", "\"");
        // 返回结果
        return text;
    }

    private static String xmlToPlainText(String text, boolean tipSwitch) {
        // 输出提示
        if(tipSwitch) {
            // 打印文字提示
            Logger.println("当前终端似乎不支持 ANSI 转义序列",2);
            Logger.println("当前系统环境仅支持 Windows Terminal",2);
            // 等待以让用户注意到提示
            try {
                // 等待 0.667 秒
                Thread.sleep(667);
            } catch (InterruptedException e) {
                // 处理中断异常
                // nothing here...
            }
            
        }
        // <em>CONTENT</em> -> \033[0;1mCONTENT\033[0m
        text = text.replaceAll("\u003cem.*?\u003e", "");
        text = text.replaceAll("\u003c/em\u003e", "");
        // &amp; -> &
        text = text.replaceAll("&amp;", "&");
        // &quot; -> "
        text = text.replaceAll("&quot;", "\"");
        // 返回结果
        return text;
    }
    
}
