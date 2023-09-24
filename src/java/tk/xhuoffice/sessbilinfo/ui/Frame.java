package tk.xhuoffice.sessbilinfo.ui;

import tk.xhuoffice.sessbilinfo.Main;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Frame {

    public static int[] size = Size.get();
    
    public static void main(String[] args) {
        // 重绘屏幕
        redraw();
        // 环境变量处理
        Main.env();
        // 命令行参数处理
        Main.cmdArgs(args);
        // 启动!
        Main.main();
    }
    
    public static void clear() {
        System.out.print("\033[0;0f");
        for(int l = 0; l < size[1]; l++) {
            for(int c = 0; c < size[0]; c++) {
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    public static void printTitle() {
        System.out.print("\033[0;0f\033[47;30m");
        for(int i = 0; i < size[0]; i++) {
            System.out.print(" ");
        }
        System.out.print("\033[0;0f"+Main.SOFT_TITLE);
        System.out.println("\033[0m");
    }
    
    public static void redraw() {
        clear();
        printTitle();
    }
    
    public static void reset() {
        Pointer.ln = 2;
        Pointer.col = 0;
        redraw();
    }
    
}
