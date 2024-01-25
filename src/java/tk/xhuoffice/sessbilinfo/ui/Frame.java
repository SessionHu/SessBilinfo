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

/**
 * UI frame for application.
 */

public class Frame {

    // NO <init>
    private Frame() {}

    /**
     * Visible terminal size.
     */
    public static Size size;
    
    /**
     * Terminal to run the application.
     * @see org.jline.terminal.TerminalBuilder#system(boolean)
     */
    public static Terminal terminal;
    
    /**
     * Initialize UI.
     * @param args  unused
     * @throws IOException  from {@link TerminalBuilder#build()}
     */
    public static void main(String... args) throws IOException {
        // load JANSI
        if(System.getenv("TERM")==null||!System.getenv("TERM").contains("xterm")) {
            AnsiConsole.systemInstall();
        }
        // load JLINE
        terminal = TerminalBuilder.builder().system(true).build();
        terminal.handle(Terminal.Signal.INT, signal -> {
            System.out.println();
            Logger.debugln("SIGINT signal received, exit!");
            Lancher.exit(Lancher.ExitType.OK);
        });
        size = terminal.getSize();
        sizeGetter.start();
        // reset screen
        reset();
    }

    /**
     * Whether UI allows exit safely now.
     */
    public static volatile boolean exitable = true;

    private static Thread sizeGetter = new Thread(() -> {
        // if terminal get failed
        if(terminal==null || size==null || size.getColumns()<8) {
            terminal = null;
            size = null;
            return;
        }
        // get size
        Size newsize;
        exitable = false;
        while(!Lancher.exit) {
            // size
            newsize = terminal.getSize();
            if(!newsize.equals(size)) {
                size = newsize;
            }
            // sleep
            try {
                Thread.sleep(48);
            } catch(InterruptedException e) {}
        }
        exitable = true;
    },"SizeGetter");

    /**
     * Print title.
     */
    public static void printTitle() {
        int length = size.getColumns()-Main.SOFT_TITLE.length();
        if(length<0) {
            return;
        }
        String titleSpace = null; {
            char[] spaces = new char[length];
            Arrays.fill(spaces,' ');
            titleSpace = new String(spaces);
        }
        // text for print
        StringBuilder text = new StringBuilder();
        text.append("\033[7m");
        text.append(Main.SOFT_TITLE);
        text.append(titleSpace);
        text.append("\033[0m");
        // set line
        Prompt.setCursorPosition(1);
        System.out.print(text);
        Prompt.restoreCursorPosition();
    }
    
    /**
     * Reset temporary UI settings.
     */
    public static void reset() {
        if(terminal==null) {
            return;
        }
        // clear
        System.out.print("\033[2J");
        // reset cursor
        Prompt.setCursorPosition(2);
        Prompt.saveCursorPosition();
        // title
        printTitle();
    }

}
