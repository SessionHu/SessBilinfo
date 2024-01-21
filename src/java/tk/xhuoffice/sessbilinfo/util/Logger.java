package tk.xhuoffice.sessbilinfo.util;

import java.io.FileOutputStream;
import java.io.IOException;
import tk.xhuoffice.sessbilinfo.net.CookieFile;
import tk.xhuoffice.sessbilinfo.net.StringCoder;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.ui.Prompt;

/**
 * Simple Logger for SessBilinfo.
 */

public class Logger {

    /**
     * Enable debug log.
     * @see LEVELS
     */
    public static boolean debug;
    
    /**
     * Log levels.
     */
    public static final String[] LEVELS = {"DEBUG","INFO","WARN","ERROR","FATAL"};
    
    private static FileOutputStream out = null;
    
    /**
     * Initialize writing log to file.
     */
    public static void initWriter() {
        if(out==null) {
            try {
                String fpath = System.getProperty("user.home")+"/.openbili/logs/sess-"+OutFormat.currentLiteDateTime()+".log";
                CookieFile.checkParentDir(fpath,false);
                out = new FileOutputStream(fpath);
            } catch(IOException e) {}
        }
    }
    
    /**
     * Write string to log file. Line breaks will automatically added.
     * @param str  string to be written
     */
    protected static synchronized void writeln(String str) {
        // is null or empty?
        if(str==null || (str=str.trim()).isEmpty() || out==null) {
            return;
        }
        // write
        try {
            out.write(str.getBytes(StringCoder.UTF_8));
            out.write('\n');
        } catch(IOException e) {}
    }
    
    /**
     * Customizable log.
     * @param str   log
     * @param lv    level
     * @param desc  description before every line
     */
    public static void addLines(String str, int lv, String desc) {
        // log level
        if(lv<1||lv>4) {
            if(debug) {
                lv = 0;
            } else {
                return;
            }
        }
        // generate fullDesc
        String fullDesc;
        if(!desc.isEmpty()) {
            fullDesc = String.format("[%s][%s] ", LEVELS[lv], desc);
        } else {
            fullDesc = String.format("[%s] ", LEVELS[lv]);
        }
        // get lines
        String[] lines = lineSplitDesc(str,fullDesc);
        // print
        if(lv<3) {
            printLinesForEach(lines);
        } else {
            printLinesForEach(lines,true);
        }
    }
    
    private static synchronized void printLinesForEach(String[] lines) {
        printLinesForEach(lines,false);
    }
    
    private static synchronized void printLinesForEach(String[] lines, boolean usestderr) {
        // print
        for(String text : lines) {
            // print to screen
            if(usestderr) {
                System.err.println(text);
            } else {
                System.out.println(text);
            }
            // write to file
            writeln(text);
        }
        // title
        if(Frame.terminal!=null && Frame.size!=null) {
            Frame.printTitle();
        }
    }
    
    private static void printLines(String str, int lv) {
        String desc;
        if(debug) {
            // get StackTrace
            StackTraceElement st = Thread.currentThread().getStackTrace()[3];
            // 获取完整类名
            String fullClassName = st.getClassName();
            // 获取最后一个 '.' 位置
            int lastDotIndex = fullClassName.lastIndexOf('.');
            // 仅获取类名
            String cN = fullClassName.substring(lastDotIndex + 1);
            // 获取方法名
            String mN = st.getMethodName();
            // generate description
            desc = cN + "." + mN;
        } else {
            desc = "";
        }
        // 打印信息
        if(str!=null) {
            addLines(str,lv,desc);
        }
    }

    /**
     * Print & Log.
     * @param str  log
     * @param lv   level
     */
    public static void println(String str, int lv) {
        if((lv>=1&&lv<=4)||debug) {
            printLines(str,lv);
        }
    }

    /**
     * Print & Log (level 1).
     * @param str  log
     * @see println(String,int)
     */
    public static void println(String str) {
        printLines(str,1); // 信息
    }

    /**
     * Print & Log (level 1).
     * @param obj  log
     * @see println(String,int)
     * @see String#valueOf(Object)
     */
    public static void println(Object obj) {
        printLines(String.valueOf(obj),1); // 信息
    }

    /**
     * Print & Log (level 2).
     * @param str  log
     * @see println(String,int)
     */
    public static void warnln(String str) {
        printLines(str,2); // 警告
    }
    
    /**
     * Print & Log (level 3.
     * @param str  log
     * @see println(String,int)
     */
    public static void errln(String str) {
        printLines(str,3); // 错误
    }
    
    /**
     * Print & Log (level 4).
     * @param str  log
     * @see println(String,int)
     */
    public static void fataln(String str) {
        printLines(str,4); // 致命
    }

    /**
     * Print & Log (level 0).
     * @param str  log
     * @see println(String,int)
     * @see debug
     */
    public static void debugln(String str) {
        if(debug) {
            printLines(str,0); // 调试
        }
    }

    /**
     * Print & Log (level 0).
     * @param obj  log
     * @see println(String,int)
     * @see String#valueOf(Object)
     * @see debug
     */
    public static void debugln(Object obj) {
        if(debug) {
            printLines(String.valueOf(obj),0); // 调试
        }
    }
    
    /**
     * Print & Log without detailed description.
     * @param str  log
     * @param lv   level
     */
    public static void throwabln(String str, int lv) {
        // log level
        if(lv<1||lv>4) {
            if(debug) {
                lv = 0;
            } else {
                return;
            }
        }
        // get lines
        String[] lines = lineSplitDesc(str,"["+LEVELS[lv]+"] ");
        // print
        printLinesForEach(lines,true);
    }
    
    /**
     * Split string into lines and add description.
     * @param str       to be split
     * @param fullDesc  description
     */
    public static String[] lineSplitDesc(String str, String fullDesc) {
        String[] lines = str.replace("\r\n","\n").replace("\r","\n").split("\\n");
        for(int i = 0; i < lines.length; i++) {
            lines[i] = fullDesc + lines[i];
        }
        return lines;
    }
    
    /**
     * Log & print at foot of terminal. Only support <bold>ONE</bold> line.
     * @param text  text
     * @see Frame#size
     */
    public static void footln(String text) {
        // only support ONE line
        text = text.replace("\r","").replace("\n","");
        // clear foot line
        clearFootln();
        // print
        if(Frame.terminal!=null && Frame.size!=null) {
            // normal
            System.out.print("\033["+Frame.size.getColumns()+"f"+text);
        } else {
            // no terminal
            System.out.println(text);
        }
        // write to file
        writeln(text);
    }
    
    /**
     * Clear foot of terminal.
     * @see Frame#size
     */
    public static void clearFootln() {
        if(Frame.terminal!=null && Frame.size!=null) {
            System.out.printf("\033[%df\033[2K",Frame.size.getRows());
        }
    }
    
    /**
     * Press Enter key to continue.
     */
    public static void enter2continue() {
        footln("Press Enter key to continue ...");
        Prompt.getPasswordLine(Character.MIN_VALUE);
        clearFootln();
    }
    
}
