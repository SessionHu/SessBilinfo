package tk.xhuoffice.sessbilinfo;

import java.io.File;
import java.util.Scanner;
import sun.misc.Signal;
import tk.xhuoffice.sessbilinfo.util.CookieFile;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Main {
    
    public static final String SOFT_NAME = "SessBilinfo";
    public static final String SOFT_VERSION = "1.0.0-rc";
    public static final String SOFT_TITLE  = SOFT_NAME+" "+SOFT_VERSION;
    
    public static Scanner scan = new Scanner(System.in);
    
    static {
        Signal.handle(new Signal("INT"), signal -> {
            Logger.ln();
            Logger.debugln("SIGINT signal received, exit!");
            System.exit(0);
        });
    }
    
    public static void main(String... args) {
        // 命令行参数处理
        cmdArgs(args);
        // 显示菜单
        int id = menu();
        // 执行操作
        try {
            task(id);
        } catch(Exception e) {
            Logger.fataln("发生未知异常");
            OutFormat.outThrowable(e,4);
            System.exit(127);
        } catch(Error e) {
            Logger.fataln("发生未知错误");
            OutFormat.outThrowable(e,4);
            System.exit(127);
        }
        // 退出
        System.exit(0);
    }
    
    public static int menu() {
        int id = 0;
        // 提示输入信息
        Logger.println(
                "请输入操作编号\n"+
                "1. 获取用户信息\n"+
                "2. 获取视频信息\n"+
                "3. 进行综合搜索\n"+
                "4. 检查昵称状态\n"+
                "5. 修改 Cookie\n"+
                "0. 退出");
        Logger.prompt();
        // 获取输入信息
        try {
            id = scan.nextInt();
            scan.nextLine(); // 消耗掉换行符
        } catch(Exception e) {
            // 送给不按套路出牌的用户
            Logger.ln();
            return -1;
        }
        return id;
    }
    
    public static void task(int id) {
        if(id==1) {
            // 获取用户信息
            UserInfo.getUserInfo();
        } else if(id==2) {
            // 获取视频信息
            Video.getVideoInfo();
        } else if(id==3) {
            // 进行综合搜索
            Search.search();
        } else if(id==4) {
            // 检查昵称状态
            Account.checkNickname();
        } else if(id==5) {
            // 修改 Cookie
            CookieFile.edit();
        } else if(id==0) {
            // noting here...
        } else {
            // 输出错误
            Logger.warnln("无效的操作编号");
        }
    }

    public static void cmdArgs(String... args) {
        // 检测环境变量中是否应该启用 DEBUG 输出
        try {
            if(System.getenv("OPEN_BILI_DEBUG").trim().equals("true")) {
                Logger.debug = true;
            }
        } catch(NullPointerException e) {
            // nothing here...
        }
        // 判断命令行参数
        if(args.length==0) {
            return;
        }
        for(int i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "-h":
                case "--help":
                    // 帮助信息
                    {
                        // 获取 JAR 包 文件名
                        String jarFileName;
                        try {
                            File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                            if(!file.isDirectory()) {
                                jarFileName = file.getName();
                            } else {
                                throw new java.io.IOException(file.getCanonicalPath()+" is a directory");
                            }
                        } catch(java.net.URISyntaxException|java.io.IOException e) {
                            jarFileName = "SessBilinfo.jar";
                        }
                        // 帮助信息内容
                        String helpMsg = SOFT_TITLE+"\n";
                        String helpMsgEnUs = "Usage:\n"+
                                "    java -jar \""+jarFileName+"\"\n"+
                                "Command arguments:\n"+
                                "    -d, --debug      Enable DEBUG output\n"+
                                "    -n, --nocookie   Run the program after deleting the Cookie file\n"+
                                "Environment variables:\n"+
                                "    OPEN_BILI_DEBUG  The same as '-d' or '--debug' when the value is \"true\"\n"+
                                "Help information:\n"+
                                "    -h, --help       Output this help information\n"+
                                "    -v, --version    Output version and other information";
                        String helpMsgZhCn = "用法:\n"+
                                "	java -jar \""+jarFileName+"\"\n"+
                                "命令参数:\n"+
                                "	-d, --debug     启用 DEBUG 输出\n"+
                                "	-n, --nocookie  删除 Cookie 文件后运行程序\n"+
                                "环境变量:\n"+
                                "	OPEN_BILI_DEBUG 当值为 \"true\" 时, 与 '-d' 或 '-debug' 相同\n"+
                                "帮助及信息:\n"+
                                "	-h, --help      输出本帮助信息\n"+
                                "	-v, --version   输出版本和其她信息";
                        // 根据语言输出帮助信息
                        if(OutFormat.getLang()[0].equals("zh")) {
                            helpMsg += helpMsgZhCn;
                        } else {
                            helpMsg += helpMsgEnUs;
                        }
                        Logger.println(helpMsg);
                        // 退出
                        System.exit(0);
                    }
                    break;
                case "-v":
                case "--version":
                    // 输出版本信息
                    Logger.debug = false;
                    Logger.println(
                            SOFT_TITLE+"\n"+
                            "Copyright (C) 2023 SessionHu\n"+
                            "Cookie Path:  "+CookieFile.getCookieFilePath()+"\n"+
                            "Current Time: "+System.currentTimeMillis());
                    System.exit(0);
                    break;
                case "-d":
                case "--debug":
                    // DEBUG 输出是否启用
                    if(!Logger.debug) {
                        Logger.debug = true;
                        Logger.debugln("DEBUG 输出已开启");
                    }
                    break;
                case "-n":
                case "--nocookie":
                    // Cookie 处理
                    try {
                        CookieFile.rm();
                    } catch(Exception e) {
                        Logger.errln("文件删除异常: "+e.getMessage());
                    }
                    break;
                default:
                    // nothing here...
            }
        }
    }
    
}
