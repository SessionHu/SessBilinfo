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
import tk.xhuoffice.sessbilinfo.util.OutFormat;

/**
 * UI frame for application.
 */

public class Frame {

    // NO <init>
    private Frame() {}

    /**
     * CLI mode.
     */
    public static boolean cli = false;

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
        if(terminal==null || size==null || size.getColumns()<8 || cli) {
            size = null;
            cli = true;
            return;
        }
        // get size
        Size newsize = null;
        exitable = false;
        while(!Lancher.exit) {
            // size
            try {
                newsize = terminal.getSize();
            } catch(java.io.IOError e) {
                Logger.debugln("获取终端大小失败: "+e.toString());
                if(Logger.debug) {
                    redraw();
                }
            }
            if(!size.equals(newsize)) {
                size = newsize;
                redraw();
            }
            // sleep
            try {
                Thread.sleep(48);
            } catch(InterruptedException e) {
                OutFormat.outThrowable(e,0);
            }
        }
        exitable = true;
    },"SizeGetter");

    /**
     * Print title.
     */
    public static void printTitle() {
        if(size==null) {
            return;
        }
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
        if(!Prompt.getLineReader().isReading()) {
            Prompt.setCursorPosition(1);
            System.out.print(text);
            Prompt.restoreCursorPosition();
        } else {
            Prompt.getLineReader().printAbove("\033[1f"+text+"\033["+(Frame.size.getRows()-2)+"f");
        }
    }
    
    /**
     * Reset temporary UI settings.
     */
    public static void reset() {
        if(terminal==null || size==null || cli) {
            return;
        }
        synchronized(size) { // 避免多线程冲突
            // clear
            System.out.print("\033[2J");
            Logger.history.clear();
            // reset cursor
            Prompt.setCursorPosition(2);
            Prompt.saveCursorPosition();
            // title
            printTitle();
        }
    }
    
    /**
     * Redraw UI. Automatically executed when {@link size} changes.
     */
    public static void redraw() {
        synchronized(Logger.history) {
            String[] history = Logger.history.toArray(new String[0]);
            reset();
            // print
            for(String text : history) {
                // print to screen
                if(!Prompt.getLineReader().isReading()) {
                    System.out.println(text);
                    // save cursor
                    Prompt.saveCursorPosition();
                } else {
                    Prompt.restoreCursorPosition();
                    Prompt.getLineReader().printAbove("\033[u"+text+"\n\033[s\033["+(Frame.size.getRows()-2)+"f");
                }
                // restore history
                Logger.history.add(text);
            }
            // title
            if(Frame.size!=null) {
                Frame.printTitle();
            }
        }
    }

}
