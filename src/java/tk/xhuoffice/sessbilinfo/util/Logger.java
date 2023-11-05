package tk.xhuoffice.sessbilinfo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.net.CookieFile;
import tk.xhuoffice.sessbilinfo.net.StringCoder;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.ui.Prompt;


public class Logger {

    public static boolean debug;
    
    public static final String[] LEVELS = {"DEBUG","INFO","WARN","ERROR","FATAL"};
    
    private static final String FNAME = System.getProperty("user.home")+"/.openbili/logs/sess-"+OutFormat.currentLiteDateTime()+".log";
    private static FileOutputStream out = null;
    
    public static void initWriter() {
        if(out==null) {
            try {
                CookieFile.checkParentDir(FNAME,false);
                out = new FileOutputStream(FNAME);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        out.close();
                    } catch(IOException e) {}
                }));
            } catch(IOException e) {}
        }
    }
    
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
        String fullDesc = String.format("[%s][%s] ", LEVELS[lv], desc);
        // get lines
        String[] lines = lineSplitDesc(str,fullDesc);
        // print
        try {
            for(String text : lines) {
                // print to screen
                int cols = Frame.terminal.cols();
                if(text.length()>cols) {
                    for(int i = 0; i < text.length();) {
                        String chars = "";
                        if(i+cols>text.length()) {
                            chars = text.substring(i,text.length());
                        } else {
                            chars = text.substring(i,i+cols);
                        }
                        Frame.terminal.addLine(chars);
                        i = i + cols;
                    }
                } else {
                    Frame.terminal.addLine(text);
                }
                // write to file
                try {
                    out.write((text+"\n").getBytes(StringCoder.UTF_8));
                } catch(IOException|NullPointerException e) {
                }
            }
            // title
            String[] scr = Frame.terminal.getScreen();
            if(scr[scr.length-1]!=null) {
                Frame.printTitle();
            }
        } catch(NullPointerException e) {
            // print when Frame.terminal is null
            for(String text : lines) {
                System.out.println(text);
                // write to file
                try {
                    out.write((text+"\n").getBytes(StringCoder.UTF_8));
                } catch(IOException|NullPointerException ee) {}
            }
        }
    }
    
    private static void printLines(String str, int lv) {
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
        String[] lines = lineSplitDesc(str,"["+LEVELS[lv]+"] ");
        // print
        if(Frame.terminal!=null) {
            for(String text : lines) {
                // print to screen
                Frame.terminal.addLine(text);
                // write to file
                try {
                    out.write((text+"\n").getBytes(StringCoder.UTF_8));
                } catch(IOException|NullPointerException e) {}
            }
            // title
            String[] scr = Frame.terminal.getScreen();
            if(scr[scr.length-1]!=null) {
                Frame.printTitle();
            }
        } else {
            for(String text : lines) {
                System.out.println(text);
                // write to file
                try {
                    out.write((text+"\n").getBytes(StringCoder.UTF_8));
                } catch(IOException|NullPointerException e) {}
            }
        }
    }
    
    public static String[] lineSplitDesc(String str, String fullDesc) {
        String[] lines = str.split("\\n");
        for(int i = 0; i < lines.length; i++) {
            lines[i] = fullDesc + lines[i];
        }
        return lines;
    }
    
    public static void footln(String text) {
        // only support ONE line
        text = text.replaceAll("\\n","");
        if(Frame.terminal!=null) {
            clearFootln();
            Frame.terminal.setLine(Frame.terminal.lns()-1,text);
        } else {
            System.out.println(text);
        }
        // write to file
        try {
            out.write((text+"\n").getBytes(StringCoder.UTF_8));
        } catch(IOException|NullPointerException e) {}
    }
    
    public static void clearFootln() {
        if(Frame.terminal!=null) {
            Frame.terminal.clearLine(Frame.terminal.lns()-1);
        }
    }
    
    /**
     * Press Enter key to continue.
     * @return {@code false} if {@link java.util.NoSuchElementException} catched
     */
    public static boolean enter2continue() {
        try {
            footln("Press Enter key to continue ...");
            OutFormat.SCAN.nextLine();
            return true;
        } catch(java.util.NoSuchElementException e) {
            return false;
        }
    }
    
}
