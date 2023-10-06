package tk.xhuoffice.sessbilinfo.util;

import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.ui.Prompt;


public class Logger {

    public static boolean debug;
    
    public static String[] levels = {"DEBUG","INFO","WARN","ERROR","FATAL"};
    
    public static synchronized void addLines(String str, int lv, String desc) {
        // log level
        if(lv<1||lv>4) {
            if(debug) {
                lv = 0;
            } else {
                return;
            }
        }
        // generate fullDesc
        String fullDesc = String.format("[%s][%s] ", levels[lv], desc);
        // get lines
        String[] lines = lineSplitDesc(str,fullDesc);
        // print
        try {
            for(String text : lines) {
                Frame.terminal.addLine(text);
            }
            // title
            if(Frame.terminal.screen[Frame.terminal.screen.length-1]!=null) {
                Frame.printTitle();
            }
        } catch(NullPointerException e) {
            // print when Frame.terminal is null
            for(String text : lines) {
                System.out.println(text);
            }
        }
    }
    
    private static synchronized void printLines(String str, int lv) {
        // 获取完整类名
        String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
        // 获取最后一个 '.' 位置
        int lastDotIndex = fullClassName.lastIndexOf('.');
        // 仅获取类名
        String cN = fullClassName.substring(lastDotIndex + 1);
        // 获取方法名
        String mN = Thread.currentThread().getStackTrace()[3].getMethodName();
        // generate description
        String desc = cN + "." + mN;
        // 打印信息
        addLines(str,lv,desc);
    }

    public static void println(String str, int lv) {
        if((lv>=1&&lv<=4)||debug) {
            printLines(str,lv);
        }
    }

    public static void println(String str) {
        printLines(str,1); // 信息
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
    
    public static void ln() {
        System.out.println();
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
        String[] lines = lineSplitDesc(str,"["+levels[lv]+"] ");
        // print
        for(String text : lines) {
            Frame.terminal.addLine(text);
        }
        // title
        if(Frame.terminal.screen[Frame.terminal.screen.length-1]!=null) {
            Frame.printTitle();
        }
    }
    
    public static String[] lineSplitDesc(String str, String fullDesc) {
        String[] lines = str.split("\\n");
        for(int i = 0; i < lines.length; i++) {
            lines[i] = fullDesc + lines[i];
        }
        return lines;
    } 
    
}
