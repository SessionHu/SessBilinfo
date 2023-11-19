package tk.xhuoffice.sessbilinfo.ui;


public class Prompt {
    
    private static int lineNum = 23; // default value
    
    private static String originText = null;
    
    protected static boolean status;
    
    public static void set() {
        lineNum = Frame.screen.lns - 1;
        getOriginText();
        Frame.screen.setLine(lineNum,"> ");
        status = true;
    }
    
    public static void set(String str) {
        lineNum = Frame.screen.lns - 1;
        getOriginText();
        Frame.screen.setLine(lineNum,str+" > ");
        status = true;
    }
    
    public static void unset() {
        recoveryOriginText();
        status = false;
    }
    
    private static String getOriginText() {
        return originText = Frame.screen.getLine(lineNum);
    }
    
    private static void recoveryOriginText() {
        if(originText!=null) {
            Frame.screen.setLine(lineNum,originText);
        } else {
            Frame.screen.clearLine(lineNum);
        }
    }
    
}
