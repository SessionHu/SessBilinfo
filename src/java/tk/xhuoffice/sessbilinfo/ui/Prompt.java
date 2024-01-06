package tk.xhuoffice.sessbilinfo.ui;


public class Prompt {
    
    private static int lineNum = 23; // default value
    
    protected static boolean status;
    
    public static void set() {
        lineNum = Frame.size.lns() - 1;
        System.out.printf("\033[%df\033[2K> ",lineNum);
        status = true;
    }
    
    public static void set(String str) {
        lineNum = Frame.size.lns() - 1;
        System.out.printf("\033[%df\033[2K%s > ", lineNum, str);
        status = true;
    }
    
    public static void unset() {
        lineNum = Frame.size.lns() - 1;
        System.out.printf("\033[%df\033[2K", lineNum);
        status = false;
    }

}
