package tk.xhuoffice.sessbilinfo.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Scanner;

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
            // 定义变量
            String str = null;
            // 输出提示
            if(tip.length!=0) {
                Logger.inputHere(tip[0]);
            } else {
                Logger.inputHere();
            }
            // 读取控制台输入
            try {
                str = scan.nextLine();
            } catch(java.util.NoSuchElementException e) {
                // 异常处理(退出)
                Logger.ln();
                Logger.fataln("非法的输入");
                outException(e,0);
                System.exit(1);
            }
            // 为空时的处理
            if(str==null||str.trim().isEmpty()) {
                Logger.warnln(typ+"不能为空");
            } else {
                return str;
            }
        }
    }

    public static String getPositiveLongAsString(String typ, String... num) {
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
            } catch(java.util.NoSuchElementException e) {
                // 异常处理
                Logger.ln();
                Logger.fataln("非法的输入");
                outException(e,0);
                System.exit(1);
            }
            try {
                // 将输入转换为 long
                long mid = Long.parseLong(input);
                // 检测输入是否大于0
                if(mid>0) {
                    // 提示并返回结果
                    Logger.println(typ+": "+mid);
                    return input;
                } else {
                    // 输出警告
                    Logger.warnln("无效的 "+typ+" "+input);
                }
            } catch(Exception e) {
                // 输出警告
                Logger.warnln("过大或非数字不能作为 "+typ);
            }
            if(num.length!=0) {
                Logger.debugln("抛出异常");
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

    private static boolean isSupportRichText() {
        // 获取系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        // Windows 环境
        if(osName.contains("windows")) {
            // 是否使用 Windows Terminal
            String wts = System.getenv("WT_SESSION");
            return wts != null && !wts.trim().isEmpty();
        } else {
            return true;
        }
    }

    public static String xmlToANSI(String text) {
        if(isSupportRichText()) {
            return xmlToRichText(text);
        } else {
            return xmlToPlainText(text,true);
        }
    }

    private static String xmlToRichText(String text) {
        // <em>CONTENT</em> -> \033[0;1mCONTENT\033[0m
        text = text.replaceAll("\u003cem.*?\u003e", "\033[0;1m");
        text = text.replaceAll("\u003c/em\u003e", "\033[0m");
        // &amp; -> &
        text = text.replaceAll("&amp;", "&");
        // &quot; -> "
        text = text.replaceAll("&quot;", "\"");
        // &lt; -> <
        text = text.replaceAll("&lt;", "<");
        // &gt; -> >
        text = text.replaceAll("&gt;", ">");
        // 返回结果
        return text;
    }

    private static String xmlToPlainText(String text, boolean tipSwitch) {
        // 输出提示
        if(tipSwitch) {
            // 打印文字提示
            Logger.warnln("当前终端似乎不支持 ANSI 转义序列");
            Logger.warnln("当前系统环境仅支持 Windows Terminal");
            // 等待以让用户注意到提示
            try {
                // 等待 1 秒
                Thread.sleep(1000);
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
        // &lt; -> <
        text = text.replaceAll("&lt;", "<");
        // &gt; -> >
        text = text.replaceAll("&gt;", ">");
        // 返回结果
        return text;
    }

    public static String[] getLang() {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        String country = locale.getCountry();
        return new String[] {lang, country};
    }
    
}
