package tk.xhuoffice.sessbilinfo;

import sun.misc.Signal;
import tk.xhuoffice.sessbilinfo.net.Setting;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.Logger;

public class Lancher {
    
    static {
        Signal.handle(new Signal("INT"), signal -> {
            Logger.ln();
            Logger.debugln("SIGINT signal received, exit!");
            System.exit(0);
        });
    }
    
    public static void main(String[] args) {
        // 环境变量处理
        Main.env();
        // 命令行参数处理
        Main.cmdArgs(args);
        // load Terminal UI Frame
        Frame.main();
        // load Setting
        Setting.load();
        // 启动!
        Main.main();
    }
    
}