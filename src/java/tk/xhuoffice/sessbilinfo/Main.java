package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.Search;
import tk.xhuoffice.sessbilinfo.UserInfo;
import tk.xhuoffice.sessbilinfo.Video;
import tk.xhuoffice.sessbilinfo.util.CookieFile;
import tk.xhuoffice.sessbilinfo.util.Logger;



public class Main {
    
    public static Scanner scan = new Scanner(System.in);
    
    public static void main(String... args) {
        // 命令行参数处理
        cmdArgs(args);
        // 显示菜单
        int id = menu();
        // 执行操作
        task(id);
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
        Logger.inputHere();
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
                Logger.println("DEBUG 输出已开启",0);
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
                case "-v":
                case "--version":
                    // 输出版本信息
                    Logger.println(
                            "SessBilinfo v0.2.0-gh.main\n"+
                            "Copyright (C) 2023 SessionHu\n"+
                            "Cookie Path:  "+CookieFile.getCookieFilePath()+"\n"+
                            "Current Time: "+String.valueOf(System.currentTimeMillis()));
                    System.exit(0);
                    break;
                case "-d":
                case "--debug":
                    // DEBUG 输出是否启用
                    if(!Logger.debug) {
                        Logger.debug = true;
                        Logger.println("DEBUG 输出已开启",0);
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
