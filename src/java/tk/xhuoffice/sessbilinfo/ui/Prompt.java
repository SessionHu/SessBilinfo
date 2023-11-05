package tk.xhuoffice.sessbilinfo.ui;


public class Prompt {
    
    private static int lineNum = 22; // default value
    
    private static String originText = null;
    
    public static void set() {
        lineNum = Frame.terminal.lns - 2;
        originText = getOriginText();
        Frame.terminal.setLine(lineNum,"> ");
        System.out.printf("\033[%d;3f",lineNum);
    }
    
    public static void set(String str) {
        lineNum = Frame.terminal.lns - 2;
        originText = getOriginText();
        Frame.terminal.setLine(lineNum,str+" > ");
        System.out.printf("\033[%d;%df",lineNum,str.length()+4);
    }
    
    public static void unset() {
        recoveryOriginText();
    }
    
    private static String getOriginText() {
        return Frame.terminal.getLine(lineNum);
    }
    
    private static void recoveryOriginText() {
        if(originText!=null) {
            Frame.terminal.setLine(lineNum,originText);
        } else {
            clearPromptLine();
        }
    }
    
    private static void clearPromptLine() {
        Frame.terminal.clearLine(lineNum);
    }
    
}
