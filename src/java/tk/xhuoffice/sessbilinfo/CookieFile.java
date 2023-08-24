package tk.xhuoffice.sessbilinfo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import tk.xhuoffice.sessbilinfo.Logger;
import tk.xhuoffice.sessbilinfo.OutFormat;


public class CookieFile {

    public static final long COOKIE_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000; // 24 h
    
    public static String getCookieFilePath() {
        // 获取系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        Logger.println("系统 "+osName,0);
        // 根据系统选择路径
        if(osName.contains("windows")) {
            // Windows
            String usrdir = System.getenv("USERPROFILE");
            return usrdir + "\\.openbili\\cookie.txt";
        } else if(osName.contains("mac") || osName.contains("linux")) {
            // 类 Unix
            String usrHome = System.getProperty("user.home");
            return usrHome + "/.openbili/cookie.txt";
        } else {
            // 其她
            Logger.println("未知的操作系统 "+osName+", Cookie 将保存在当前工作目录下",2);
            return "cookie.txt";
        }
    }
    
    public static void save(String[] cookie) {
        // 验证获取的 Cookie
        if(cookie.length==0) {
            // Cookie 为空
            Logger.println("Cookie 为空",2);
        } else {
            try {
                // 修改 Cookie
                for(int l = 0; l < cookie.length; l++) {
                    // 避免 null
                    if(cookie[l]==null||cookie[l].trim().isEmpty()) {
                        cookie[l] = "";
                    }
                }
                // 写入文件
                writeTimeAndLines(getCookieFilePath(),cookie);
            } catch(Exception e) {
                Logger.println("Cookie 文件写入失败",2);
                OutFormat.outException(e,2);
            }
        }
    }
    
    public static void writeTimeAndLines(String path, String[] line) throws IOException {
        // 检查父目录
        File parentDir = new File(path).getParentFile();
        if(!parentDir.exists()) { // 当父目录不存在时
            Logger.println("正在创建 Cookie 父目录",0);
            parentDir.mkdirs();
            if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                // 仅在 Windows 下隐藏目录  (类Unix无隐藏属性)
                hideWinDir(parentDir.getCanonicalPath());
            }
        }
        // 写入文件
        try(FileWriter writer = new FileWriter(path)) {
            Logger.println("正在写入 Cookie",0);
            String lines = "";
            for(int l = 0; l < line.length; l++) {
                lines += line[l]+"\n";
            }
            writer.write(System.currentTimeMillis() + "\n" + lines);
        }
    }
    
    public static void hideWinDir(String path) throws IOException {
        // 运行 attrib 命令来设置目录的隐藏属性
        Logger.println("正在设置隐藏属性",0);
        String[] cmd = {"attrib", "+H", path};
        Runtime.getRuntime().exec(cmd);
    }
    
    public static String[] load() {
        // 输入文件
        File file = new File(getCookieFilePath());
        // 文件是否存在
        if(!file.exists()) {
            return new String[0];
        }
        try {
            // 读取文件
            String[] lines;
            try(FileReader reader = new FileReader(file)) {
                char[] buffer = new char[(int) file.length()];
                reader.read(buffer);
                lines = new String(buffer).split("\n");
            }
            // 处理数据
            try {
                long timestamp = Long.parseLong(lines[0]);
                // 验证文件
                if(System.currentTimeMillis() - timestamp > COOKIE_EXPIRE_TIME) {
                    // 文件过期
                    Logger.println("文件过期",0);
                } else if(lines[1]==null||lines[1].trim().isEmpty()) {
                    // 正文第一行为空行
                    Logger.println("文件首行为空",0);
                } else {
                    // 读取数据
                    String[] cookie = new String[lines.length-1];
                    int cookieIndex = 0;
                    for(int i = 1; i < lines.length; i++) {
                        // 读取行
                        String line = lines[i];
                        // 判断行是否有效
                        if(line.contains("=")) {
                            // 有效载入
                            cookie[cookieIndex] = line;
                            cookieIndex++;
                        } else {
                            // 无效留空
                            Logger.println("行 "+1+" 无效",0);
                        }
                    }
                    // 返回数据
                    return cookie;
                }
            } catch(NumberFormatException e) {
                Logger.println("Cookie 文件时间戳错误",3);
            } catch(ArrayIndexOutOfBoundsException e) {
                Logger.println("Cookie 文件为空",2);
            }
        } catch(Exception e) {
            Logger.println("Cookie 文件加载失败",2);
        }
        // 返回空数据
        return new String[0];
    }
    
    public static void edit() {
        // 初始化变量
        ArrayList<String> lines = new ArrayList<>();
        String current = "";
        // 输出提示
        String tips = "";
        tips += "请输入新的 Cookie 内容\n";
        tips += "一行一条 Cookie\n";
        tips += "输入 :wq 并回车以退出\n";
        tips += "示例内容 SESSDATA=xxxx";
        Logger.println(tips,1);
        // 等待输入
        while(true) {
            // 获取输入
            current = OutFormat.getString("行",String.valueOf(lines.size()+1));
            // 验证行
            if(!current.trim().equals(":wq")) {
                // 输入有效性检测
                if(!current.trim().matches("^[^=]+=[^=]+$")) {
                    // 无效输入
                    Logger.println("无效的 Cookie "+current,2);
                } else {
                    // 有效输入
                    lines.add(current);
                }
            } else {
                // 保存并退出
                break;
            }
        }
        // 写入文件
        save(lines.toArray(new String[0]));
    }

    public static void rm() throws IOException {
        File file = new File(getCookieFilePath());
        if(file.delete()) {
            Logger.println("Cookie 文件已删除",0);
        } else {
            Logger.println("Cookie 文件删除失败",2);
        }
    }
    
}
