package tk.xhuoffice.sessbilinfo.ui;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Cursor;
import tk.xhuoffice.sessbilinfo.util.Logger;

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
        boolean clearline = (mask==null || (mask!=null && !mask.equals(Character.MIN_VALUE) && prompt!=null));
        synchronized(lineReader) {
            while(nextline==null) {
                try {
                    // set cursor position
                    if(Frame.size!=null) {
                        setCursorPosition(Frame.size.getRows()-1);
                    }
                    // clear line
                    if(clearline && Frame.size!=null) {
                        System.out.print("\033[2K");
                    }
                    // read line
                    nextline = lineReader.readLine(prompt,mask);
                    // auto end the line
                    System.out.println();
                } catch(org.jline.reader.UserInterruptException e) {
                    if(clearline){
                        Logger.footln("输入被取消: "+e.toString());
                    }
                } catch(org.jline.reader.EndOfFileException e) {
                    if(clearline){
                        Logger.footln("非法的输入: "+e.toString());
                    }
                }
            }
            // cursor up & clear line
            System.out.print("\033[A");
            if(clearline && Frame.size!=null) {
                System.out.print("\033[2K");
                Logger.clearFootln();
            }
            // restore cursor position
            Prompt.restoreCursorPosition();
        }
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

    /**
     * Set cursor position.
     * @param x X
     * @param y Y
     * @return cursor position
     */
    public static Cursor setCursorPosition(int y, int x) {
        // cannot set
        if(Frame.size==null) {
            return new Cursor(0,0);
        }
        // param
        if(x<-1) {
            throw new IllegalArgumentException("x");
        } else if(y<0) {
            throw new IllegalArgumentException("y");
        }
        // set
        if(x==-1) {
            System.out.printf("\033[%df",y);
            return new Cursor(1,y);
        } else {
            System.out.printf("\033[%d;%df",y,x);
            return new Cursor(x,y);
        }
    }

    /**
     * Set cursor Y position.
     * @param y Y
     * @return cursor position
     */
    public static Cursor setCursorPosition(int y) {
        return setCursorPosition(y,-1);
    }

    /**
     * Set cursor postion with {@link org.jline.terminal.Cursor}.
     * @param cursor cursor
     * @return cursor position
     */
    public static Cursor setCursorPosition(Cursor cursor) {
        return setCursorPosition(cursor.getY(),cursor.getX());
    }

    public static void saveCursorPosition() {
        if(Frame.size!=null) {
            System.out.print("\033[s");
        }
    }

    public static void restoreCursorPosition() {
        if(Frame.size!=null) {
            System.out.print("\033[u");
        }
    }

    /**
     * Get {@link LineReader} of this class.
     * @return {@code lineReader}
     */
    public static LineReader getLineReader() {
        return lineReader;
    }

}
