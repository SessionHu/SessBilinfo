package tk.xhuoffice.sessbilinfo.ui;

import java.util.Arrays;
import org.fusesource.jansi.AnsiConsole;
import tk.xhuoffice.sessbilinfo.Main;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Frame {

    public static Terminal terminal = null;
    
    public static void main(String... args) {
        // load JANSI
        if((System.getenv("TERM")!=null)&&(!System.getenv("TERM").contains("xterm"))) {
            loadJansi();
        }
        // create a new Terminal
        terminal = new Terminal();
        // 重绘屏幕
        redraw();
    }
    
    public static void loadJansi() {
        AnsiConsole.systemInstall();
    }
    
    public static void unloadJansi() {
        AnsiConsole.systemUninstall();
    }
    
    public static void printTitle() {
        String titleSpace = null; {
            char[] spaces = new char[terminal.cols-Main.SOFT_TITLE.length()];
            Arrays.fill(spaces,' ');
            titleSpace = new String(spaces);
        }
        // text for print
        String text = "\033[7m" + Main.SOFT_TITLE + titleSpace + "\033[0m";
        // set line
        terminal.setLine(1,text);
    }
    
    public static void redraw() {
        terminal.redraw();
        printTitle();
    }
    
    public static void reset() {
        terminal.clear();
        printTitle();
    }
    
}
