package tk.xhuoffice.sessbilinfo.ui;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import tk.xhuoffice.sessbilinfo.Lancher;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Prompt {

    private static LineReader lineReader = LineReaderBuilder.builder().terminal(Frame.terminal).build();

    public static String getNextLine(String prompt) {
        if(prompt!=null && !prompt.isEmpty()) {
            prompt += "> ";
        } else {
            prompt = "> ";
        }
        String nextline = null;
        while(nextline==null) {
            try {
                System.out.printf("\033[%df\033[2K",Frame.size.getRows()-1);
                nextline = lineReader.readLine(prompt);
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

    public static String getNextLine() {
        return getNextLine(null);
    }

}
