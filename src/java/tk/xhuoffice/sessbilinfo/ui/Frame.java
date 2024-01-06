package tk.xhuoffice.sessbilinfo.ui;

import java.io.IOException;
import java.util.Arrays;
import org.fusesource.jansi.AnsiConsole;
import tk.xhuoffice.sessbilinfo.Main;


public class Frame {

    public static Size size;
    static {
        try {
            size = Size.get();
        } catch(IOException e) {
            size = new Size(80,24);
        }
    }
    
    public static void main(String... args) {
        // load JANSI
        loadJansi();
        // reset screen
        reset();
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
            char[] spaces = new char[size.cols()-Main.SOFT_TITLE.length()];
            Arrays.fill(spaces,' ');
            titleSpace = new String(spaces);
        }
        // text for print
        String text = "\033[7m" + Main.SOFT_TITLE + titleSpace + "\033[0m";
        // set line
        System.out.print("\033[s");
        System.out.print("\033[1f"+text);
        System.out.print("\033[u");
    }
    
    public static void reset() {
        // clear
        System.out.print("\033[2J");
        // cursor position
        System.out.print("\033[2f");
        // title
        printTitle();
    }
    
}
