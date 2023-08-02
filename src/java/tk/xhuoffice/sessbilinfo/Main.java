package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.UserInfo;


public class Main {
    
    public static Scanner scan = new Scanner(System.in);
    
    public static void main(String[] args) {
        // 显示菜单
        int task = menu();
        // 执行操作
        if(task==1) {
            // 获取用户信息
            UserInfo.getUserInfo();
        } else if(task==0) {
            // noting here...
        } else {
            // 输出错误并退出
            System.err.println("[WARN] 无效的操作编号");
        }
        System.exit(0);
    }
    
    public static int menu() {
        int task = 0;
        // 提示输入信息
        System.out.println(
                "[INFO] 请输入操作编号\n"+
                "[INFO] 1. 获取用户信息\n"+
                "[INFO] 0. 退出");
        System.out.print("> ");
        // 获取输入信息
        try {
            task = scan.nextInt();
            scan.nextLine(); // 消耗掉换行符
        } catch(Exception e) {
            // 送给不按套路出牌的用户
            return -1;
        }
        return task;
    }
    
}