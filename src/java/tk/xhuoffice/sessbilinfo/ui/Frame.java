package tk.xhuoffice.sessbilinfo.ui;

import java.io.IOException;
import java.util.Arrays;
import org.fusesource.jansi.AnsiConsole;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import tk.xhuoffice.sessbilinfo.Lancher;
import tk.xhuoffice.sessbilinfo.Main;
import tk.xhuoffice.sessbilinfo.util.Logger;


public class Frame {

    public static Size size;
    public static Terminal terminal;
    
    public static void main(String... args) throws IOException {
        // load JANSI
        if(System.getenv("TERM")==null||!System.getenv("TERM").contains("xterm")) {
            AnsiConsole.systemInstall();
        }
        // load JLINE
        terminal = TerminalBuilder.builder().system(true).signalHandler(signal -> {
            if(signal.equals(Terminal.Signal.INT)) {
                System.out.println();
                Logger.debugln("SIGINT signal received, exit!");
                Lancher.exit(Lancher.ExitType.OK);
            }
        }).build();
        size = terminal.getSize();
        sizeGetter.start();
        // reset screen
        reset();
    }

    public static volatile boolean exitable = false;

    private static Thread sizeGetter = new Thread(() -> {
        Size newsize;
        while(!Lancher.exit) {
            // size
            newsize = terminal.getSize();
            if(!newsize.equals(size)) {
                size = newsize;
            }
            // sleep
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {}
        }
        exitable = true;
    },"SizeGetter");

    public static void printTitle() {
        String titleSpace = null; {
            char[] spaces = new char[size.getColumns()-Main.SOFT_TITLE.length()];
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
