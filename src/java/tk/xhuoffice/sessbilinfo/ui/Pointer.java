package tk.xhuoffice.sessbilinfo.ui;

import java.util.Arrays;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Pointer {
    
    public static int ln = 2;
    public static int col = 0;
    
    public static boolean prompt;
    
    public static int[] size = Size.get();
    
    public static void set() {
        if(prompt) {
            System.out.printf("\033[%d;%df",size[1]-1,col+1);
        } else {
            System.out.printf("\033[%d;0f",ln);
        }
    }
    
    public static void unset() {
        prompt = false;
        // 清空提示符所在行
        clearPromptLine();
        // 光标恢复原行
        System.out.printf("\033[%d;0f",ln);
    }
    
    public static void set(int l) {
        ln = l;
        System.out.printf("\033[%d;0f",l);
    }
    
    public static void update(String str) {
        // 更新指针位置
        int lns = str.length() - str.replace("\n", "").length();
        ln = ln + lns;
    }
    
    public static void prompt() {
        prompt = true;
        // 清空提示符所在行
        clearPromptLine();
        // 将光标临时移动至提示符行
        System.out.printf("\033[%d;0f",size[1]-1);
    }
    
    public static void clearPromptLine() {
        System.out.printf("\033[%d;0f",size[1]-1);
        char[] spaces = new char[size[0]-1];
        Arrays.fill(spaces,' ');
        System.out.println(new String(spaces));
    }
    
}