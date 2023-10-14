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
        AnsiConsole.systemInstall();
        // create a new Terminal
        terminal = new Terminal();
        // 重绘屏幕
        redraw();
    }
    
    public static void printTitle() {
        // some colors
        String whiteBgWithBlackText = "\033[47;30m";
        String resetColor = "\033[0m";
        // title
        String titleText = Main.SOFT_TITLE;
        String titleSpace = null; {
            char[] spaces = new char[terminal.cols-titleText.length()];
            Arrays.fill(spaces,' ');
            titleSpace = new String(spaces);
        }
        // text for print
        String text = whiteBgWithBlackText + titleText + titleSpace + resetColor;
        // set line
        terminal.setLine(1,text);
    }
    
    public static void redraw() {
        terminal.redraw();
        printTitle();
    }
    
    public static void clear() {
        terminal.clear();
        printTitle();
    }
    
    public static void reset() {
        terminal.clear();
        printTitle();
    }
    
}
