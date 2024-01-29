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
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.ui.Prompt;

/**
 * Format message for output.
 */

public class OutFormat {
    
    // NO <init>
    private OutFormat() {}

    private static final ZoneId ZONE_HK = ZoneId.of("Asia/Hong_Kong");
    
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

    /**
     * Reads a line of input string from {@code Frame#terminal}.
     * @param typ  Input text type
     * @param tip  Text before prompt
     * @param scp  Save cursor postion
     * @return     String read from {@code Frame#terminal}.
     */
    public static String getString(String typ, String tip) {
        // var
        if(typ==null || typ.isEmpty()) {
            typ = "输入";
        }
        String line;
        String logprompt; {
            StringBuilder sb = new StringBuilder();
            // log prompt
            if(tip!=null) {
                sb.append(tip);
            }
            sb.append("> ");
            logprompt = sb.toString();
        }
        // loop
        while(true) {
            // get next line
            line = Prompt.getNextLine(tip);
            // log
            Logger.writeln(logprompt+line);
            // if is empty
            if(line.trim().isEmpty()) {
                Logger.footln(typ+" 不能为空");
            } else {
                Logger.clearFootln();
                return line;
            }
        }
    }

    public static String getString(String typ) {
        return getString(typ,null);
    }

    public static String getString() {
        return getString(null,null);
    }
    
    /**
     * Reads a line of input long positive number as String from {@code Frame#terminal}.
     * @param typ     Input text type
     * @return        String read from {@code Frame#terminal}.
     */
    public static String getPositiveLongAsString(String typ) {
        return getPositiveLongAsString(typ,null);
    }
    
    /**
     * Reads a line of input long positive number as String from {@code Frame#terminal}.
     * @param typ     Input text type
     * @param strNum  Input string instead of {@code Frame#terminal}.
     * @return        String read from {@code Frame#terminal}.
     */
    public static String getPositiveLongAsString(String typ, String strNum) {
        // 定义并初始化变量
        String input = "";
        while(true) {
            if(strNum!=null&&!strNum.isEmpty()) {
                // 读取参数
                input = strNum;
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
            if(strNum!=null && !strNum.isEmpty()) {
                throw new NumberFormatException("无效的 "+typ+" "+input);
            }
        }
    }
    
    public static void outThrowable(Throwable e, int l) {
        Logger.throwabln(getThrowableStackTrace(e),l);
    }
    
    public static String getThrowableStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
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
    
    /**
     * @deprecated Sometimes it could not work well.
     * @param str  input
     * @return     Maybe like {@code "xxxxxxxx...xxxxxxxx"} or {@code "xxxxxxxxxxxxxxxx......xxxxxxxxxxxxxxxx"} or {@code str} itself
     * @see        #shorterString(int,String,int)
     */
    @Deprecated
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
     * Shorter String like {@code "xxxx..."}.
     * @param str     input
     * @param firsts  First characters counts.
     * @return        First {@code firsts} characters of {@code str} and {@code "..."}.
     */
    public static String shorterString(int firsts, String str) {
        if(str.length()<=firsts+3) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(str.substring(0,firsts));
            sb.append("...");
            return sb.toString();
        }
    }
    
    /**
     * Shorter String like {@code "...xxxx"}.
     * @param str    input
     * @param lasts  Last characters counts.
     * @return       {@code "..."} and last {@code lasts} characters of {@code str}.
     */
    public static String shorterString(String str, int lasts) {
        if(str.length()<=lasts+3) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("...");
            sb.append(str.substring(str.length()-lasts,str.length()));
            return sb.toString();
        }
    }
    
    /**
     * Shorter String like {@code "xxxx...xxxx"}.
     * @param str     input
     * @param firsts  First characters counts.
     * @param lasts   Last characters counts.
     * @return        First {@code firsts} characters and {@code "..."} and last {@code lasts} characters of {@code str}.
     */
    public static String shorterString(int firsts, String str, int lasts) {
        if(str.length()<=firsts+lasts+3) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(str.substring(0,firsts));
            sb.append("...");
            sb.append(str.substring(str.length()-lasts,str.length()));
            return sb.toString();
        }
    }
    
    /**
     * Break messages into many pages {@link Frame#size} lines.
     * @param str  messages
     * @return     pages
     */
    public static String[] pageBreak(String str) {
        if(Frame.size==null) {
            return new String[]{str};
        }
        // prepare variables
        int lns = Frame.size.getRows()-4;
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
            page.append(String.format("第%d页 共%d页", pages.size()+1, lines.length/lns+1));
            // add page to pages
            pages.add(page.toString());
        }
        // return
        return pages.toArray(new String[0]);
    }
    
    /**
     * Checks whether a character is a full-width character.
     * @param c  Character to check
     * @return   Check result
     */
    public static boolean checkFullWidth(char c) {
        return Character.UnicodeBlock.of(c)==Character.UnicodeBlock.CJK_COMPATIBILITY ||
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
               Character.UnicodeBlock.of(c)==Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }
    
}
