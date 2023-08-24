package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.CookieFile;
import tk.xhuoffice.sessbilinfo.Logger;
import tk.xhuoffice.sessbilinfo.Search;
import tk.xhuoffice.sessbilinfo.UserInfo;


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
                "2. 进行综合搜索\n"+
                "3. 检查昵称状态\n"+
                "4. 修改 Cookie\n"+
                "0. 退出"
                ,1);
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
            // 进行综合搜索
            Search.search();
        } else if(id==3) {
            // 检查昵称状态
            Account.checkNickname();
        } else if(id==4) {
            // 修改 Cookie
            CookieFile.edit();
        } else if(id==0) {
            // noting here...
        } else {
            // 输出错误
            Logger.println("无效的操作编号",2);
        }
    }

    public static void cmdArgs(String... args) {
        if(args.length==0) {
            return;
        }
        for(int i = 0; i < args.length; i++) {
            // DEBUG 输出是否启用
            try {
                if(args[i].equals("--debug")) {
                    Logger.debug = true;
                    Logger.println("DEBUG 输出已开启",0);
                }
                if(System.getenv("OPEN_BILI_DEBUG").trim().equals("true")) {
                    Logger.debug = true;
                    Logger.println("DEBUG 输出已开启",0);
                }
            } catch(NullPointerException e) {
                // nothing here...
            }
            // Cookie 处理
            try {
                if(args[i].equals("--nocookie")) {
                    CookieFile.rm();
                }
            } catch(Exception e) {
                Logger.println("文件删除异常: "+e.getMessage(),0);
            }
        }
    }
    
}
