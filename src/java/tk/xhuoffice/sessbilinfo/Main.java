package tk.xhuoffice.sessbilinfo;

import java.io.File;
import org.fusesource.jansi.AnsiConsole;
import tk.xhuoffice.sessbilinfo.net.CookieFile;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.ui.Prompt;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Main {
    
    public static final String SOFT_NAME = "SessBilinfo";
    public static final String SOFT_VERSION = "1.1.0-beta.2";
    public static final String SOFT_TITLE  = SOFT_NAME+" "+SOFT_VERSION;
    
    public static void main(String... args) {
        try {
            while(true) {
                // 显示菜单
                int id = menu();
                // 执行操作
                task(id);
                if(!Logger.enter2continue()) {
                    Lancher.exit(Lancher.ExitType.OK);
                }
                // reset screen
                Frame.reset();
            }
        } catch(Exception e) {
            Logger.fataln("发生未知异常");
            OutFormat.outThrowable(e,4);
            Lancher.exit(16);
        } catch(Error e) {
            Logger.fataln("发生未知错误");
            OutFormat.outThrowable(e,4);
            Lancher.exit(17);
        }
    }
    
    public static int menu() {
        int id = -1;
        // 提示输入信息
        Logger.println(
                "请输入操作编号\n"+
                "1. 获取用户信息\n"+
                "2. 获取视频信息\n"+
                "3. 进行综合搜索\n"+
                "4. 检查昵称状态\n"+
                "5. 获取IP地理位置\n"+
                "6. 修改 Cookie\n"+
                "0. 退出");
        // 获取输入信息
        System.out.print("\033[s");
        try {
            id = Integer.parseInt(Prompt.getNextLine());
        } catch(NumberFormatException e) {}
        System.out.print("\033[u");
        return id;
    }
    
    public static void task(int id) {
        if(id==1) {
            // 获取用户信息
            UserInfo.getUserInfo();
        } else if(id==2) {
            // 获取视频信息
            Video.simpleViewer();
        } else if(id==3) {
            // 进行综合搜索
            Search.search();
        } else if(id==4) {
            // 检查昵称状态
            Account.checkNickname();
        } else if(id==5) {
            //  获取IP地理位置
            Account.ipLocation();
        } else if(id==6) {
            // 修改 Cookie
            CookieFile.edit();
        } else if(id==0) {
            // 退出
            Lancher.exit(Lancher.ExitType.OK);
        } else {
            // print warning
            Logger.warnln("无效的操作编号");
            // 退出
            Lancher.exit(Lancher.ExitType.OK);
        }
    }

    public static void env() {
        // 是否启用 DEBUG 输出
        {
            String d = System.getenv("OPEN_BILI_DEBUG");
            if((d!=null) && (d.equals("true"))) {
                Logger.debug = true;
            }
        }
    }

    public static void cmdArgs(String... args) {
        // 判断命令行参数
        if(args.length==0) {
            return;
        }
        for(String arg : args) {
            switch(arg) {
                case "-?":
                case "-h":
                case "--help":
                    printHelpInfo();
                    Lancher.exit(Lancher.ExitType.OK);
                    break;
                case "-v":
                case "--version":
                    // 输出版本信息
                    Logger.debug = false;
                    System.out.println(
                            SOFT_TITLE+"\n"+
                            "Copyright (C) 2023 SessionHu\n"+
                            "Cookie Path:  "+CookieFile.getCookieFilePath()+"\n"+
                            "Current Time: "+System.currentTimeMillis()/1000L);
                    Lancher.exit(Lancher.ExitType.OK);
                    break;
                case "-d":
                case "--debug":
                    // DEBUG 输出是否启用
                    Logger.debug = true;
                    break;
                case "-n":
                case "--nocookie":
                    // Cookie 处理
                    CookieFile.rm();
                    break;
                case "-a":
                case "--force-ansi":
                    // Force use of JANSI library
                    AnsiConsole.systemInstall();
                    break;
                default:
                    // nothing here...
            }
        }
    }
    
    public static void printHelpInfo() {
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
                "    -a, --force-ansi Force use of JANSI library\n"+
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
                "    -a, --force-ansi 强制使用 JANSI 库\n"+
                "    -d, --debug      启用 DEBUG 输出\n"+
                "    -n, --nocookie   删除 Cookie 文件后运行程序\n"+
                "环境变量:\n"+
                "    OPEN_BILI_DEBUG  当值为 \"true\" 时, 与 '-d' 或 '-debug' 相同\n"+
                "帮助及信息:\n"+
                "    -h, --help       输出本帮助信息\n"+
                "    -v, --version    输出版本和其她信息";
        // 根据语言输出帮助信息
        if(OutFormat.getLang()[0].equals("zh")) {
            helpMsg += helpMsgZhCn;
        } else {
            helpMsg += helpMsgEnUs;
        }
        System.out.println(helpMsg);
    }
    
}
