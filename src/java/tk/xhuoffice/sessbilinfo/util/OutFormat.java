package tk.xhuoffice.sessbilinfo.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.ui.Prompt;

/**
 * Format message for output
 */

public class OutFormat {
    
    private static final ZoneId ZONE_HK = ZoneId.of("Asia/Hong_Kong");
    public static final Scanner SCAN = new Scanner(System.in);
    
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
    
    public static String currentLiteDateTime() {
        LocalDateTime dt = LocalDateTime.now(ZONE_HK);
        int y = dt.getYear(); // 年
        int m = dt.getMonthValue(); // 月
        int d = dt.getDayOfMonth(); // 日
        int h = dt.getHour(); // hour
        int min = dt.getMinute(); // minute
        int s = dt.getSecond(); // second
        return String.format("%04d-%02d-%02d-%02d-%02d-%02d",y,m,d,h,min,s); // yyyy-MM-dd-HH-mm-ss
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
            String log = "";
            // 输出提示
            if(tip.length!=0) {
                log = tip[0];
                Prompt.set(log);
            } else {
                Prompt.set();
            }
            log += "> ";
            // 读取控制台输入
            try {
                str = SCAN.nextLine();
                // write to log
                log += str;
                Logger.writeln(log);
            } catch(java.util.NoSuchElementException e) {
                Logger.fataln("非法的输入");
                System.exit(1);
            }
            Prompt.unset();
            // 为空时的处理
            str = str.trim();
            if(str.isEmpty()) {
                Logger.footln(typ+" 不能为空");
            } else {
                Logger.clearFootln();
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
                input = getString(typ);
            }
            try {
                // 将输入转换为 long
                long num = Long.parseLong(input);
                // 检测输入是否大于0
                if(num>0) {
                    // 提示并返回结果
                    Logger.println(typ+": "+num);
                    Logger.clearFootln();
                    return input;
                } else {
                    // 输出警告
                    Logger.footln("无效的 "+typ+" "+input);
                }
            } catch(NumberFormatException e) {
                // 输出警告
                Logger.footln("过大或非数字不能作为 "+typ);
            }
            if(strNum.length!=0) {
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

    public static String xmlToANSI(String text) {
        // <em>CONTENT</em> -> \033[0;1mCONTENT\033[22m
        text = text.replaceAll("\u003cem.*?\u003e", "\033[1m");
        text = text.replaceAll("\u003c/em\u003e", "\033[22m");
        // 转换实体字符
        text = xmlEntityToChar(text);
        // 返回结果
        return text;
    }

    private static String xmlEntityToChar(String text) {
        // &amp; -> &
        text = text.replace("&amp;", "&");
        // &quot; -> "
        text = text.replace("&quot;", "\"");
        // &lt; -> <
        text = text.replace("&lt;", "<");
        // &gt; -> >
        text = text.replace("&gt;", ">");
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
        if(length>40) {
            return str.substring(0,16)+"......"+str.substring(length-16);
        } else if(length>21) {
            return str.substring(0,8)+"..."+str.substring(length-8);
        } else {
            return str;
        }
    }
    
    /**
     * Break messages into many pages {@link Frame#screen} lines.
     * @param str  messages
     * @return     pages
     */
    public static String[] pageBreak(String str) {
        // prepare variables
        int lns = Frame.screen.lns()-2;
        String[] lines = str.split("\\n");
        List<String> pages = new ArrayList<>();
        // lines to pages
        for(int i = 0; i < lines.length;) {
            // lines to page
            StringBuilder page = new StringBuilder();
            for(int j = 0; j < lns && i < lines.length; j++) {
                page.append(lines[i++]);
                page.append("\n");
            }
            // add page to pages
            pages.add(page.deleteCharAt(page.length()-1).toString());
        }
        // return
        return pages.toArray(new String[0]);
    }
    
    /**
     * Split a line String into sub-lines according to {@link Frame#screen} columns.
     * @param text  A line without '\r' or(and) '\n'
     * @return      A {@link java.util.List} with String for printing
     */
    public static List<String> subLines(String text) {
        int cols = Frame.screen.cols();
        ArrayList<String> slines = new ArrayList<>(); // sub-lines
        for(int i = 0; i < text.length();) { // text with SUB-char to sub-lines
            StringBuilder cline = new StringBuilder(); // char-line
            boolean inAnsi = false;
            StringBuilder ansis = new StringBuilder();
            for(int j = 0; (j < cols) && (i < text.length()); j++) { // build char-line
                char c = text.charAt(i++);
                // ansi escape codes OR other invisible char
                if(c=='\033') { // ESC
                    inAnsi = true;
                    ansis.append(c);
                    j--;
                    continue;
                }
                if(c<9||(c>13&&c<32)) {} // ignore
                if(c==10||c==13) { // LF or CR
                    cline.append("\n");
                    j = -1;
                    continue;
                }
                if(inAnsi) { // in ansi escape codes
                    if(ansis.length()==1 && ansis.charAt(0)=='\033') { // CSI start
                        if(c=='[') { // CSI start
                            ansis.append(c);
                        } else if(c=='N'||c=='O'||c=='P'||c=='\\'||c==']'||c=='X'||c=='^'||c=='_'||c=='c') { // not CSI
                            inAnsi = false;
                        }
                        j--;
                        continue;
                    }
                    if(ansis.length()>1 && ansis.charAt(1)=='[') { // in CSI
                        ansis.append(c);
                        if(c>=0x40 && c<=0x7E) { // CSI end
                            inAnsi = false;
                        }
                        cline.append(ansis);
                        ansis = new StringBuilder();
                        j--;
                        continue;
                    }
                }
                // else
                cline.append(c);
                if(Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_COMPATIBILITY ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_STROKES ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_E ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.HIRAGANA ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.KATAKANA ||
                   Character.UnicodeBlock.of(c)==Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                    j++;
                }
            }
            slines.add(cline.toString()); // add char-line into sub-lines
        }
        return slines;
    }
    
}
