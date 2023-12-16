package tk.xhuoffice.sessbilinfo.ui;

import java.util.Arrays;
import org.fusesource.jansi.AnsiConsole;
import tk.xhuoffice.sessbilinfo.Main;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Frame {

    public static Screen screen = null;
    
    public static void main(String... args) {
        // load JANSI
        loadJansi();
        // create a new Screen
        screen = new Screen();
        // 重绘屏幕
        redraw();
    }
    
    public static void loadJansi() {
        if(System.getenv("TERM")==null||!System.getenv("TERM").contains("xterm")) {
            AnsiConsole.systemInstall();
        }
    }
    
    public static void unloadJansi() {
        AnsiConsole.systemUninstall();
    }
    
    public static void printTitle() {
        String titleSpace = null; {
            char[] spaces = new char[screen.cols-Main.SOFT_TITLE.length()];
            Arrays.fill(spaces,' ');
            titleSpace = new String(spaces);
        }
        // text for print
        String text = "\033[7m" + Main.SOFT_TITLE + titleSpace + "\033[0m";
        // set line
        screen.setLine(1,text);
    }
    
    public static void redraw() {
        screen.redraw();
        printTitle();
    }
    
    public static void reset() {
        screen.clear();
        printTitle();
    }
    
}
