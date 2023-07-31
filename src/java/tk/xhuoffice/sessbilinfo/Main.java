package tk.xhuoffice.sessbilinfo;

import java.util.Scanner;
import tk.xhuoffice.sessbilinfo.*;


public class Main {
    
    public static void main(String[] args) {
        // 运行
        outUserInfo();
        // 退出
        System.exit(0);
    }
    
    public static void outUserInfo() {
        String mid;
        // 提示输入信息
        System.out.println(
                "请输入被查询用户的 Mid 信息\n"+
                "示例: 645769214"
                );
        System.out.print("> ");
        // 获取输入信息
        try(Scanner scan = new Scanner(System.in)) {
            mid = scan.nextLine();
        }
        // 提示输入完成
        System.out.println("Mid: "+mid);
        // 输出用户信息
        UserInfo.card(mid);
    }
    
}