package tk.xhuoffice.sessbilinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fusesource.jansi.AnsiConsole;
import tk.xhuoffice.sessbilinfo.Lancher;
import tk.xhuoffice.sessbilinfo.net.CookieFile;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.ui.Prompt;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

/**
 * User entrance.
 */

public class Main {
    
    /**
     * Application name */
    public static final String SOFT_NAME = "SessBilinfo";
    /**
     * Application version */
    public static final String SOFT_VERSION = "1.1.1";
    /**
     * Application title */
    public static final String SOFT_TITLE = SOFT_NAME+" "+SOFT_VERSION;
    
    /**
     * User entrance.
     * @param args unused
     */
    public static void main(String... args) {
        while(true) {
            // 显示菜单
            int id = menu();
            // 执行操作
            task(id);
            // Press Enter key to continue ...
            Logger.enter2continue();
            // reset screen
            Frame.reset();
        }
    }
    
    /**
     * Display menu.
     * @return task id
     */
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
        try {
            id = Integer.parseInt(Prompt.getNextLine());
        } catch(NumberFormatException e) {
            // print warning
            Logger.warnln("无效的操作编号");
            OutFormat.outThrowable(e,2);
            // 退出
            Lancher.exit(Lancher.ExitType.OK);}
        return id;
    }
    
    /**
     * Run task.
     * @param task id
     */
    public static void task(int id) {
        if(id==1) {
            // 获取用户信息
            UserInfo.getUserInfo();
        } else if(id==2) {
            // 获取视频信息
            Video.simpleViewer();
        } else if(id==3) {
            // 进行综合搜索
            Search.searchAll();
        } else if(id==4) {
            // 检查昵称状态
            Account.checkNickname();
        } else if(id==5) {
            //  获取IP地理位置
            Account.ipLocation();
        } else if(id==6) {
            // 修改 Cookie
            CookieFile.edit();
        //} else if(id==114514) {
        //    // for test
        //    FileWriter w = new FileWriter("unicode.txt");
        //    for(char c = '\u0000'; c < '\uFFFF'; c++) {
        //        w.write(c);
        //    }
        //    w.close();
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

    /**
     * Parse environment variables.
     */
    public static void env() {
        // 是否启用 DEBUG 输出
        {
            String d = System.getenv("OPEN_BILI_DEBUG");
            if((d!=null) && (d.equals("true"))) {
                Logger.debug = true;
            }
        }
    }

    /**
     * Parse command arguments.
     * @param ags  command arguments
     */
    public static void cmdArgs(String... ags) {
        // 判断命令行参数
        if(ags.length==0) {
            return;
        }
        // 短参数转换
        List<String> args = new ArrayList<>();
        Map<Character,String> conv = new HashMap<>();
        conv.put('d',"--debug");
        conv.put('n',"--nocookie");
        conv.put('a',"--force-ansi");
        conv.put('c',"--clear-log");
        conv.put('l',"--cli");
        for(String arg : ags) {
            if(arg.startsWith("-") && !arg.startsWith("--")) {
                // if help or version
                if(arg.contains("?") || arg.contains("h")) {
                    // print help information directly and exit
                    printHelpInfo();
                    Lancher.exit(Lancher.ExitType.OK);
                } else if(arg.contains("v")) {
                    // print version information directly and exit
                    Logger.debug = false;
                    printVersion();
                    Lancher.exit(Lancher.ExitType.OK);
                }
                // convert
                for(int i = 1; i < arg.length(); i++) {
                    String fu = conv.get(arg.charAt(i));
                    if(fu!=null) {
                        args.add(fu);
                    }
                }
            } else if(arg.startsWith("--")) {
                args.add(arg);
            }
        }
        for(String arg : args) {
            if(arg.equals("--help")) {
                // print help information
                printHelpInfo();
                Lancher.exit(Lancher.ExitType.OK);
            } else if(arg.equals("--version")) {
                // print version information
                Logger.debug = false;
                printVersion();
                Lancher.exit(Lancher.ExitType.OK);
            } else if(arg.equals("--debug")) {
                // DEBUG 输出是否启用
                Logger.debug = true;
            } else if(arg.equals("--nocookie")) {
                // Cookie 处理
                CookieFile.rm();
            } else if(arg.equals("--force-ansi")) {
                // Force use of JANSI library
                AnsiConsole.systemInstall();
            } else if(arg.equals("--clear-log")) {
                // clear log
                Logger.clearLogs();
            } else if(arg.equals("--cli")) {
                // CLI mode
                Frame.cli = true;
            }
        }
    }

    /**
     * Print version information of SessBilinfo.
     */
    public static void printVersion() {
        System.out.println(
                        SOFT_TITLE+"\n"+
                        "Copyright (C) 2023 SessionHu\n"+
                        "Cookie Path:  "+CookieFile.getCookieFilePath()+"\n"+
                        "Current Time: "+System.currentTimeMillis()/1000L);
    }
    
    /**
     * Print help information of SessBilinfo.
     */
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
                "    -c, --clear-log  Clear log files\n"+
                "    -d, --debug      Enable DEBUG output\n"+
                "    -l, --cli        Enable CLI mode\n"+
                "    -n, --nocookie   Run the program after deleting the Cookie file\n"+
                "Environment variables:\n"+
                "    OPEN_BILI_DEBUG  The same as '-d' or '--debug' when the value is \"true\"\n"+
                "Help information:\n"+
                "    -h, --help       Output this help information\n"+
                "    -v, --version    Output version and other information";
        String helpMsgZhCn = "用法:\n"+
                "    java -jar \""+jarFileName+"\"\n"+
                "命令参数:\n"+
                "    -a, --force-ansi 强制使用 JANSI 库\n"+
                "    -c, --clear-log  清空日志文件\n"+
                "    -d, --debug      启用 DEBUG 输出\n"+
                "    -l, --cli        启用 CLI 模式\n"+
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
