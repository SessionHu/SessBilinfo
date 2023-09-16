package tk.xhuoffice.sessbilinfo.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;


public class OutFormat {
    
    public static Scanner scan = new Scanner(System.in);
    
    public static final ZoneId ZONE_HK = ZoneId.of("Asia/Hong_Kong");
    
    public static String time(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        String formattedTime;
        if(duration.toMinutes()>=60) {
            formattedTime = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes()%60, duration.getSeconds()%60);
        } else {
            formattedTime = String.format("%02d:%02d", duration.toMinutes(), duration.getSeconds()%60);
        }
        return formattedTime;
    }
    
    public static String num(long num) {
        return java.text.NumberFormat.getInstance(Locale.US).format(num);
    }
    
    public static String date(long timestamp) {
        // 转换为日期
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp),ZONE_HK);
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
    
    public static String fullDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp),ZONE_HK).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
                Logger.prompt(tip[0]);
            } else {
                Logger.prompt();
            }
            // 读取控制台输入
            try {
                str = scan.nextLine();
            } catch(java.util.NoSuchElementException e) {
                // 异常处理(退出)
                Logger.ln();
                outThrowable(e,0);
                Logger.fataln("非法的输入");
                System.exit(1);
            }
            // 为空时的处理
            if(str==null||str.trim().isEmpty()) {
                Logger.warnln(typ+" 不能为空");
            } else {
                return str;
            }
        }
    }

    public static String getPositiveLongAsString(String typ, String... strNum) {
        // 定义并初始化变量
        String input = "";
        while(true) {
            if(strNum.length!=0) {
                // 读取参数
                input = strNum[0];
            } else {
                // 获取输入
                input = getString(typ).trim();
            }
            try {
                // 将输入转换为 long
                long num = Long.parseLong(input);
                // 检测输入是否大于0
                if(num>0) {
                    // 提示并返回结果
                    Logger.println(typ+": "+num);
                    return input;
                } else {
                    // 输出警告
                    Logger.warnln("无效的 "+typ+" "+input);
                }
            } catch(Exception e) {
                // 输出警告
                Logger.warnln("过大或非数字不能作为 "+typ);
            }
            if(strNum.length!=0) {
                Logger.debugln("抛出异常");
                throw new NumberFormatException("无效的 "+typ+" "+input);
            }
        }
    }
    
    public static void outThrowable(Throwable e, int l) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        Logger.throwabln(stackTrace,l);
    }
    
    public static boolean forceANSI;
    
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
        if(isSupportRichText()||forceANSI) {
            return xmlToRichText(text);
        } else {
            return xmlToPlainText(text,true);
        }
    }

    private static String xmlToRichText(String text) {
        // <em>CONTENT</em> -> \033[0;1mCONTENT\033[0m
        text = text.replaceAll("\u003cem.*?\u003e", "\033[0;1m");
        text = text.replaceAll("\u003c/em\u003e", "\033[0m");
        // 转换实体字符
        text = xmlEntityToChar(text);
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
                // 等待 1.111 秒
                Thread.sleep(1111);
            } catch (InterruptedException e) {
                // 处理中断异常
                // nothing here...
            }
        }
        // <em>CONTENT</em> -> \033[0;1mCONTENT\033[0m
        text = text.replaceAll("\u003cem.*?\u003e", "");
        text = text.replaceAll("\u003c/em\u003e", "");
        // 转换实体字符
        text = xmlEntityToChar(text);
        // 返回结果
        return text;
    }

    private static String xmlEntityToChar(String text) {
        // &amp; -> &
        text = text.replaceAll("&amp;", "&");
        // &quot; -> "
        text = text.replaceAll("&quot;", "\"");
        // &lt; -> <
        text = text.replaceAll("&lt;", "<");
        // &gt; -> >
        text = text.replaceAll("&gt;", ">");
        // &apos; &#39; -> '
        text = text.replaceAll("(&quot;|&#39;)", "'");
        // 返回结果
        return text;
    }
    
    public static String[] getLang() {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        String country = locale.getCountry();
        return new String[] {lang, country};
    }

    public static String shorterString(String str) {
        int length = str.length();
        if(length>55) {
            return str.substring(0,24)+"......"+str.substring(length-24);
        } else if(length>39) {
            return str.substring(0,16)+"......"+str.substring(length-16);
        } else if(length>28) {
            return str.substring(0,12)+"..."+str.substring(length-12);
        } else if(length>20) {
            return str.substring(0,8)+"..."+str.substring(length-8);
        } else {
            Logger.debugln("无法缩短字符串 "+str);
            return str;
        }
    }
    
}
