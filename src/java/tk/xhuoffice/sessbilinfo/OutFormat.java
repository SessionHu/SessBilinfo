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
    
    public static void outException(Exception e, int l) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        Logger.println(stackTrace,l);
    }
    
    public static String xmlTagToANSI(String text) {
        // <em>CONTENT</em> -> \033[0;1mCONTENT\033[0m
        text = text.replaceAll("\u003cem.*?\u003e", "\033[0;1m");
        text = text.replaceAll("\u003c/em\u003e", "\033[0m");
        return text;
    }
    
}
