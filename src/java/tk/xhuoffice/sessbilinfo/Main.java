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
            // 正常退出
            System.exit(0);
        } else {
            // 输出错误并退出
            System.err.println("error: 无效的操作编号");
            System.exit(1);
        }
    }
    
    public static int menu() {
        int task = 0;
        // 提示输入信息
        System.out.println(
                "请输入操作编号\n"+
                "1. 获取用户信息\n"+
                "0. 退出");
        System.out.print("> ");
        // 获取输入信息
        task = scan.nextInt();
        scan.nextLine(); // 消耗掉换行符
        return task;
    }
    
}