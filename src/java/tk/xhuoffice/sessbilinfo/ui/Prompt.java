package tk.xhuoffice.sessbilinfo.ui;


public class Prompt {
    
    public static int lineNum = 22; // default value
    
    public static String originText = null;
    
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
    
    public static String getOriginText() {
        return Frame.terminal.getLine(lineNum);
    }
    
    public static void recoveryOriginText() {
        if(originText!=null) {
            Frame.terminal.setLine(lineNum,originText);
        } else {
            clearPromptLine();
        }
    }
    
    public static void clearPromptLine() {
        Frame.terminal.clearLine(lineNum);
    }
    
}
