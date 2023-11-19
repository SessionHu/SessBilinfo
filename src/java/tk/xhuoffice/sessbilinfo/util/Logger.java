package tk.xhuoffice.sessbilinfo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.net.CookieFile;
import tk.xhuoffice.sessbilinfo.net.StringCoder;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.ui.Prompt;



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
           out.write((str+"\n").getBytes(StringCoder.UTF_8));
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
        String fullDesc = String.format("[%s][%s] ", LEVELS[lv], desc);
        // get lines
        String[] lines = lineSplitDesc(str,fullDesc);
        // print
        printLinesForEach(lines);
    }
    
    private static synchronized void printLinesForEach(String[] lines) {
        // print
        if(Frame.terminal!=null) {
            for(String text : lines) {
                // print to screen
                int cols = Frame.terminal.cols();
                ArrayList<String> slines = new ArrayList<>(); // sub-lines
                for(int i = 0; i < text.length();) { // text with SUB-char to sub-lines
                    StringBuilder cline = new StringBuilder(); // char-line
                    for(int j = 0; (j < cols) && (i < text.length()); j++) { // build char-line
                        char c = text.charAt(i++);
                        cline.append(c);
                        if(Character.UnicodeBlock.of(c)==Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                            j++;
                        }
                    }
                    slines.add(cline.toString()); // add char-line into sub-lines
                }
                for(String sline : slines) { // add every sub-line to terminal
                    Frame.terminal.addLine(sline);
                }
                // write to file
                writeln(text);
            }
            // title
            String[] scr = Frame.terminal.getScreen();
            if(scr[scr.length-1]!=null) {
                Frame.printTitle();
            }
        } else {
            // print when Frame.terminal is null
            for(String text : lines) {
                System.out.println(text);
                // write to file
                writeln(text);
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
        String[] lines = str.split("\\n");
        for(int i = 0; i < lines.length; i++) {
            lines[i] = fullDesc + lines[i];
        }
        return lines;
    }
    
    public static void footln(String text) {
        // only support ONE line
        text = text.replace("\n","");
        if(Frame.terminal!=null) {
            clearFootln();
            Frame.terminal.setLine(Frame.terminal.lns(),text);
        } else {
            System.out.println(text);
        }
        // write to file
        writeln(text);
    }
    
    public static void clearFootln() {
        if(Frame.terminal!=null) {
            Frame.terminal.clearLine(Frame.terminal.lns());
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
            clearFootln();
            return true;
        } catch(java.util.NoSuchElementException e) {
            return false;
        }
    }
    
}
