package tk.xhuoffice.sessbilinfo.ui;

import java.util.Arrays;
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
        char[] spaces = new char[size[0]];
        Arrays.fill(spaces,' ');
        String line = new String(spaces);
        for(int l = 0; l < size[1]; l++) {
            System.out.println(line);
        }
    }       

    
    public static void printTitle() {
        // 在第0行打印白色背景
        System.out.print("\033[0;0f\033[47;30m");
        char[] spaces = new char[size[0]];
        Arrays.fill(spaces,' ');
        System.out.println(new String(spaces));
        // 在第0行白色背景黑色前景的标题
        System.out.print("\033[0;0f"+Main.SOFT_TITLE);
        // 恢复正常颜色
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
