package tk.xhuoffice.sessbilinfo.net;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import tk.xhuoffice.sessbilinfo.ui.Frame;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class CookieFile {

    public static final long COOKIE_EXPIRE_TIME = 15552000000L; // 6 months

    public static String CookieFilePath = getCookieFilePath();
    
    public static String getCookieFilePath() {
        // 获取系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        Logger.debugln("系统 "+osName);
        // get & return
        return getCookieFilePath(osName);
    }
    
    public static String getCookieFilePath(String os) {
        // 根据系统选择路径
        if(os.contains("windows")) {
            // Windows
            String usrdir = System.getenv("USERPROFILE");
            return usrdir + "\\.openbili\\cookie.txt";
        } else if(os.contains("mac") || os.contains("linux")) {
            // 类 Unix
            String usrHome = System.getProperty("user.home");
            return usrHome + "/.openbili/cookie.txt";
        } else {
            // 其她
            if(os.length()!=0) {
                Logger.warnln("未知的操作系统 "+os+", Cookie 将保存在当前工作目录下");
            }
            return "cookie.txt";
        }
    }
    
    public static void save(String[] cookie) {
        // 验证获取的 Cookie
        if(cookie.length==0) {
            // Cookie 为空
            Logger.warnln("Cookie 为空");
        } else {
            for(int i = 0; i < 2; i++) {
                try {
                    // 修改 Cookie
                    for(int l = 0; l < cookie.length; l++) {
                        // 避免 null
                        if(cookie[l]==null) {
                            cookie[l] = "";
                        }
                    }
                    // 写入文件
                    writeTimeAndLines(CookieFilePath,cookie);
                    // 离开循环
                    break;
                } catch(java.io.FileNotFoundException e) {
                    Logger.debugln(e.toString());
                } catch(Exception e) {
                    OutFormat.outThrowable(e,0);
                }
                // 发生异常时的提示
                if(i==0) {
                    Logger.warnln("Cookie 文件写入失败, 将在当前目录下保存");
                    CookieFilePath = getCookieFilePath("");
                } else {
                    Logger.errln("Cookie 文件写入失败");
                    // 恢复原路径
                    CookieFilePath = getCookieFilePath();
                }
            }
        }
    }
    
    public static void writeTimeAndLines(String path, String[] line) throws IOException {
        // 检查父目录
        checkParentDir(path);
        // 写入文件
        try(FileWriter writer = new FileWriter(path)) {
            Logger.debugln("正在写入 Cookie");
            String lines = "";
            for(int l = 0; l < line.length; l++) {
                lines += line[l]+"\n";
            }
            writer.write(System.currentTimeMillis() + "\n" + lines);
        }
    }
    
    public static void checkParentDir(String path) throws IOException {
        if(path.contains(".openbili")) {
            File parentDir = new File(path).getParentFile();
            if(!parentDir.exists()) { // 当父目录不存在时
                Logger.debugln("正在创建 .openbili 目录");
                parentDir.mkdirs();
                if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                    // 仅在 Windows 下隐藏目录  (类Unix无隐藏属性)
                    hideWinDir(parentDir.getCanonicalPath());
                }
            }
        }
    }
    
    public static void hideWinDir(String path) throws IOException {
        // 运行 attrib 命令来设置目录的隐藏属性
        Logger.debugln("正在设置隐藏属性");
        String[] cmd = {"attrib", "+H", path};
        Runtime.getRuntime().exec(cmd);
    }
    
    public static String[] load() {
        // 加载文件
        for(int t = 0; t < 2; t++) {
            try {
                return readCookie();
            } catch(java.io.FileNotFoundException e) {
                // 重新更换路径加载
                Logger.debugln(e.toString());
                CookieFilePath = getCookieFilePath("");
            } catch(Exception e) {
                OutFormat.outThrowable(e,0);
                Logger.warnln("Cookie 文件加载失败");
            }
        }
        // 恢复原路径
        CookieFilePath = getCookieFilePath();
        // 返回空数据
        return new String[0];
    }
    
    private static String[] readCookie() throws IOException {
        // 输入文件
        File file = new File(CookieFilePath);
        // 读取文件
        String[] line;
        try(FileReader reader = new FileReader(file)) {
            char[] buffer = new char[(int)file.length()];
            reader.read(buffer);
            line = new String(buffer).split("\n");
        }
        // 处理数据
        try {
            // 验证文件
            if(System.currentTimeMillis()-Long.parseLong(line[0]) > COOKIE_EXPIRE_TIME) {
                // 文件过期
                Logger.debugln("文件过期");
            } else if(line[1]==null||line[1].trim().isEmpty()) {
                // 正文第一行为空行
                Logger.debugln("文件首行为空");
            } else {
                // 读取数据
                ArrayList<String> cookie = new ArrayList<>();
                for(int i = 1; i < line.length; i++) {
                    // 读取行
                    String current = line[i]; // 你好,中国.国庆快乐.-2023.10.1.
                    // 判断行是否有效
                    if(current.contains("=")) {
                        // 有效载入
                        cookie.add(current);
                    } else {
                        // 无效
                        Logger.debugln("文件行 "+i+" 无效");
                    }
                }
                // 返回数据
                return cookie.toArray(new String[0]);
            }
        } catch(NumberFormatException e) {
            Logger.debugln(e.toString());
            Logger.errln("Cookie 文件时间戳错误");
        } catch(ArrayIndexOutOfBoundsException e) {
            Logger.debugln(e.toString());
            Logger.warnln("Cookie 文件为空");
        }
        // 返回空数据
        return new String[0];
    }
    
    public static void edit() {
        Frame.reset();
        // 初始化变量
        ArrayList<String> lines = new ArrayList<>();
        String current = "";
        // 输出提示
        String tips = "";
        tips += "请输入新的 Cookie 内容\n";
        tips += "一行一条 Cookie\n";
        tips += "输入 :wq 并回车以退出\n";
        tips += "示例内容 SESSDATA=xxxx";
        Logger.println(tips);
        // 等待输入
        while(true) {
            // 获取输入
            current = OutFormat.getString("行",String.valueOf(lines.size()+1));
            // 验证行
            if(!current.equals(":wq")) {
                // 输入有效性检测
                if(!current.matches("^[^=]+=[^=]+$")) {
                    // 无效输入
                    Logger.warnln("无效的 Cookie "+current);
                } else {
                    // 有效输入
                    Logger.println((lines.size()+1)+": "+current);
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

    public static void rm() {
        if(new File(CookieFilePath).delete()) {
            Logger.println("Cookie 文件已删除");
        } else {
            Logger.warnln("Cookie 文件删除失败");
        }
    }
    
}
