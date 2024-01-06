package tk.xhuoffice.sessbilinfo.util;

import java.io.FileOutputStream;
import java.io.IOException;
import tk.xhuoffice.sessbilinfo.net.CookieFile;
import tk.xhuoffice.sessbilinfo.net.StringCoder;
import tk.xhuoffice.sessbilinfo.ui.Frame;



public class Logger {

    public static boolean debug;
    
    public static final String[] LEVELS = {"DEBUG","INFO","WARN","ERROR","FATAL"};
    
    private static FileOutputStream out = null;
    
    public static void initWriter() {
        if(out==null) {
            try {
                String fpath = System.getProperty("user.home")+"/.openbili/logs/sess-"+OutFormat.currentLiteDateTime()+".log";
                CookieFile.checkParentDir(fpath,false);
                out = new FileOutputStream(fpath);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        out.close();
                    } catch(IOException e) {}
                }));
            } catch(IOException e) {}
        }
    }
    
    protected static void writeln(String str) {
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
        printLinesForEach(lines);
    }
    
    private static synchronized void printLinesForEach(String[] lines) {
        // print
        for(String text : lines) {
            // print to screen
            System.out.println(text);
            // write to file
            writeln(text);
        }
        // title
        Frame.printTitle();
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

    public static void println(String str, int lv) {
        if((lv>=1&&lv<=4)||debug) {
            printLines(str,lv);
        }
    }

    public static void println(String str) {
        printLines(str,1); // 信息
    }

    public static void println(Object obj) {
        printLines(String.valueOf(obj),1); // 信息
    }

    public static void warnln(String str) {
        printLines(str,2); // 警告
    }
    
    public static void errln(String str) {
        printLines(str,3); // 错误
    }
    
    public static void fataln(String str) {
        printLines(str,4); // 致命
    }

    public static void debugln(String str) {
        if(debug) {
            printLines(str,0); // 调试
        }
    }

    public static void debugln(Object obj) {
        if(debug) {
            printLines(String.valueOf(obj),0); // 调试
        }
    }
    
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
        printLinesForEach(lines);
    }
    
    public static String[] lineSplitDesc(String str, String fullDesc) {
        String[] lines = str.replace("\r\n","\n").replace("\n\n","\n").split("\\n");
        for(int i = 0; i < lines.length; i++) {
            lines[i] = fullDesc + lines[i];
        }
        return lines;
    }
    
    public static void footln(String text) {
        // only support ONE line
        text = text.replace("\n","");
        clearFootln();
        System.out.print("\033["+Frame.size.lns()+"f"+text);
        // write to file
        writeln(text);
    }
    
    public static void clearFootln() {
        System.out.printf("\033[%d;0f\033[2K",Frame.size.lns());
    }
    
    /**
     * Press Enter key to continue.
     * @return {@code false} if {@link java.util.NoSuchElementException} catched
     */
    public static boolean enter2continue() {
        try {
            footln("Press Enter key to continue ...");
            OutFormat.SCAN.nextLine();
            clearFootln();
            return true;
        } catch(java.util.NoSuchElementException e) {
            return false;
        }
    }
    
}
