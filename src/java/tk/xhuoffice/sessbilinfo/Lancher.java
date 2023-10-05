package tk.xhuoffice.sessbilinfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import sun.misc.Signal;
import tk.xhuoffice.sessbilinfo.net.Setting;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Lancher {
    
    static {
        Signal.handle(new Signal("INT"), signal -> {
            Logger.ln();
            Logger.debugln("SIGINT signal received, exit!");
            System.exit(0);
        });
    }
    
    public static void main(String[] args) {
        // 运行前初始化
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            // 环境变量处理
            Future env = executor.submit(() -> Main.env());
            // 命令行参数处理
            Future cmd = executor.submit(() -> Main.cmdArgs(args));
            // load Setting
            Future set = executor.submit(() -> Setting.load());
            // load Terminal UI Frame
            Future tui = executor.submit(() -> Frame.main());
            // run
            env.get();
            cmd.get();
            set.get();
            tui.get();
        } catch(InterruptedException|java.util.concurrent.ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        // 启动!
        Main.main();
    }
    
}