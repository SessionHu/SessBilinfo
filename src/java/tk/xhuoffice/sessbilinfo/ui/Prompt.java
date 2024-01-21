package tk.xhuoffice.sessbilinfo.ui;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Cursor;
import tk.xhuoffice.sessbilinfo.Lancher;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

/**
 * Get user input.
 */

public class Prompt {

    private static LineReader lineReader = LineReaderBuilder.builder().terminal(Frame.terminal).build();

    /**
     * Print prompt at foot of terminal and get next line from terminal input.
     * The prompt will be added {@code "> "} before it.
     * @param prompt  prompt
     * @return        Next line from terminal input.
     * @see getPasswordLine(String,Character)
     * @see Frame#size
     */
    public static String getNextLine(String prompt) {
        if(prompt!=null && !prompt.isEmpty()) {
            prompt += "> ";
        } else {
            prompt = "> ";
        }
        return getPasswordLine(prompt,null);
    }

    /**
     * Print prompt at foot of terminal and get next line from terminal input.
     * The prompt is {@code "> "}.
     * @return        Next line from terminal input.
     * @see getNextLine(String)
     */
    public static String getNextLine() {
        return getNextLine(null);
    }

    /**
     * Get next line from terminal input with specified echo and prompt.
     * @param prompt  prompt
     * @param mask    specified echo
     * @return        Next line from terminal input.
     * @see org.jline.reader.LineReader#readLine(String,Character)
     */
    public static String getPasswordLine(String prompt, Character mask) {
        String nextline = null;
        while(nextline==null) {
            try {
                System.out.printf("\033[%df\033[2K",Frame.size.getRows()-1);
                nextline = lineReader.readLine(prompt,mask);
            } catch(org.jline.reader.UserInterruptException e) {
                System.out.print("\033[A");
            } catch(org.jline.reader.EndOfFileException e) {
                System.out.print("\033[u");
                Logger.fataln("非法的输入");
                OutFormat.outThrowable(e,4);
                Lancher.exit(Lancher.ExitType.IO_FATAL);
            }
        }
        System.out.printf("\033[%df\033[2K",Frame.size.getRows()-1);
        return nextline;
    }

    /**
     * Get next line from terminal input with no prompt and specified echo.
     * @param mask    echo
     * @return        Next line from terminal input.
     * @see org.jline.reader.LineReader#readLine(String,Character)
     */
    public static String getPasswordLine(Character mask) {
        return getPasswordLine(null,mask);
    }

    private static Robot robot;
    static {
        try {
            robot = new Robot();
        } catch(java.awt.AWTException e) {
            Logger.fataln("无法获取光标位置");
            OutFormat.outThrowable(e,4);
        }
    }

    private static volatile Cursor cursor = null;

    /**
     * Query the terminal to report the cursor position.
     * @return {@code null} if failed.
     * @see org.jline.terminal#getCursorPosition(IntConsumer)
     */
    public synchronized static Cursor getCursorPosition() {
        if(robot==null) {
            return null;
        }
        cursor = null;
        // press Enter
        new Thread(() -> {
            while(cursor==null){
                // press enter
                robot.keyPress(KeyEvent.VK_ENTER);
            }
            // release enter
            robot.keyRelease(KeyEvent.VK_ENTER);
        }, "AutoPressEnterKey").start();
        // get pos
        cursor = Frame.terminal.getCursorPosition(discarded->{});
        if(cursor!=null) {
            // offset
            cursor = new Cursor(cursor.getX()+1,cursor.getY()+1);
            // recover
            Logger.enter2continue();
            System.out.printf("\033[%d;%df\033[K", cursor.getY(), cursor.getX());
            return cursor;
        } else {
            return null;
        }
    }

}
