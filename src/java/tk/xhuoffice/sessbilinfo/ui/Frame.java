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
                Thread.sleep(50);
            } catch(InterruptedException e) {}
        }
        exitable = true;
    },"SizeGetter");

    /**
     * Print title.
     */
    public static void printTitle() {
        String titleSpace = null; {
            char[] spaces = new char[size.getColumns()-Main.SOFT_TITLE.length()];
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
        System.out.print("\033[s");
        System.out.print("\033[1f");
        System.out.print(text);
        System.out.print("\033[u");
    }
    
    /**
     * Reset temporary UI settings.
     */
    public static void reset() {
        // clear
        System.out.print("\033[2J");
        // cursor position
        System.out.print("\033[2f");
        // title
        printTitle();
    }

}
