package tk.xhuoffice.sessbilinfo;

import sun.misc.Signal;
import tk.xhuoffice.sessbilinfo.net.Setting;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Lancher {
    
    static {
        Signal.handle(new Signal("INT"), signal -> {
            System.out.println();
            Logger.debugln("SIGINT signal received, exit!");
            exit(ExitType.OK);
        });
    }

    public static void main(String[] args) {
        // 运行前初始化
        try {
            // 环境变量处理
            Main.env();
            // 命令行参数处理
            Main.cmdArgs(args);
            // load Setting
            Setting.load();
            // log to file
            Logger.initWriter();
            // load Terminal UI Frame
            Frame.main();
        } catch(Exception e) {
            OutFormat.outThrowable(e,4);
            exit(ExitType.LOAD_FATAL);
        }
        // 启动!
        Main.main();
    }

    /**
     * Exit with code.
     * 0 正常退出
     * 1 加载时异常错误
     * 2 不可恢复的IO异常错误
     * 15 运行时未知异常
     * 16 运行时未知错误
     */
    public static void exit(int code) {
        exit = true;
        while(!Frame.exitable) {
            //System.out.print(Frame.exitable);
        }
        System.exit(code);
    }

    public static void exit(ExitType type) {
        exit(type.code);
    }

    public static enum ExitType {
        OK(0),
        LOAD_FATAL(1),
        IO_FATAL(2),
        RUNTIME_EXCEPTION(15),
        RUNTIME_ERROR(16);
        ExitType(int code) {
            this.code = code;
        }
        private int code;
    }

    public static volatile boolean exit = false;
    
}
