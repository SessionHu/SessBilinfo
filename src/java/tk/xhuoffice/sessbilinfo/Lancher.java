package tk.xhuoffice.sessbilinfo;

import tk.xhuoffice.sessbilinfo.net.Setting;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Lancher {

    public static void main(String[] args) {
        // 运行前初始化
        try {
            // 环境变量处理
            Main.env();
            // 命令行参数处理
            Main.cmdArgs(args);
            // log to file
            Logger.initWriter();
            // load Setting
            Setting.load();
            // load Terminal UI Frame
            Frame.main();
        } catch(Exception e) {
            OutFormat.outThrowable(e,4);
            exit(ExitType.LOAD_FATAL);
        }
        // 启动!
        while(true) {
            try {
                Main.main();
            } catch(Exception e) {
                Logger.fataln("发生未知异常");
                OutFormat.outThrowable(e,4);
                Lancher.exit(Lancher.ExitType.RUNTIME_EXCEPTION);
            } catch(StackOverflowError e) {
                Logger.fataln("堆栈...溢出了...");
                Logger.throwabln(OutFormat.shorterString(1024,OutFormat.getThrowableStackTrace(e)),4);
                System.gc();
            } catch(Error e) {
                Logger.fataln("发生未知错误");
                OutFormat.outThrowable(e,4);
                Lancher.exit(Lancher.ExitType.RUNTIME_ERROR);
            }
        }
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
        Logger.clearFootln();
        System.exit(code);
    }

    public static void exit(ExitType type) {
        exit(type.code);
    }

    public enum ExitType {
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
