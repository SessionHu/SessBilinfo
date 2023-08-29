package tk.xhuoffice.sessbilinfo.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class CookieFile {

    public static final long COOKIE_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000; // 14 days

    public static String CookieFilePath = getCookieFilePath();
    
    public static String getCookieFilePath(String... os) {
        // 获取系统类型
        String osName = null;
        if(os.length==0) {
            osName = System.getProperty("os.name").toLowerCase();
            Logger.debugln("系统 "+osName);
        } else {
            osName = os[0].toLowerCase();
        }
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
            if(os.length==0) {
                Logger.warnln("未知的操作系统 "+osName+", Cookie 将保存在当前工作目录下");
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
                        if(cookie[l]==null||cookie[l].trim().isEmpty()) {
                            cookie[l] = "";
                        }
                    }
                    // 写入文件
                    writeTimeAndLines(CookieFilePath,cookie);
                    // 离开循环
                    break;
                } catch(Exception e) {
                    if(i==0) {
                        OutFormat.outException(e,0);
                        Logger.warnln("Cookie 文件写入失败, 将在当前目录下保存");
                        CookieFilePath = getCookieFilePath("os");
                    } else {
                        OutFormat.outException(e,0);
                        Logger.errln("Cookie 文件写入失败");
                    }
                }
            }
        }
    }
    
    public static void writeTimeAndLines(String path, String[] line) throws IOException {
        // 检查父目录
        if(path.contains(".openbili")) {
            File parentDir = new File(path).getParentFile();
            if(!parentDir.exists()) { // 当父目录不存在时
                Logger.debugln("正在创建 Cookie 父目录");
                parentDir.mkdirs();
                if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                    // 仅在 Windows 下隐藏目录  (类Unix无隐藏属性)
                    hideWinDir(parentDir.getCanonicalPath());
                }
            }
        }
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
    
    public static void hideWinDir(String path) throws IOException {
        // 运行 attrib 命令来设置目录的隐藏属性
        Logger.debugln("正在设置隐藏属性");
        String[] cmd = {"attrib", "+H", path};
        Runtime.getRuntime().exec(cmd);
    }
    
    public static String[] load() {
        // 输入文件
        File file = new File(CookieFilePath);
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
                    Logger.debugln("文件过期");
                } else if(lines[1]==null||lines[1].trim().isEmpty()) {
                    // 正文第一行为空行
                    Logger.debugln("文件首行为空");
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
                            Logger.debugln("文件行 "+i+" 无效");
                        }
                    }
                    // 返回数据
                    return cookie;
                }
            } catch(NumberFormatException e) {
                Logger.debugln("NumberFormatException: "+e.getMessage());
                Logger.errln("Cookie 文件时间戳错误");
            } catch(ArrayIndexOutOfBoundsException e) {
                Logger.debugln("ArrayIndexOutOfBoundsException: "+e.getMessage());
                Logger.warnln("Cookie 文件为空");
            }
        } catch(java.io.FileNotFoundException e) {
            Logger.debugln("java.io.FileNotFoundException: "+e.getMessage());
            CookieFilePath = getCookieFilePath("os");
            return load();
        } catch(Exception e) {
            OutFormat.outException(e,0);
            Logger.warnln("Cookie 文件加载失败");
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
        Logger.println(tips);
        // 等待输入
        while(true) {
            // 获取输入
            current = OutFormat.getString("行",String.valueOf(lines.size()+1));
            // 验证行
            if(!current.trim().equals(":wq")) {
                // 输入有效性检测
                if(!current.trim().matches("^[^=]+=[^=]+$")) {
                    // 无效输入
                    Logger.warnln("无效的 Cookie "+current);
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

    public static void rm() {
        File file = new File(CookieFilePath);
        if(file.delete()) {
            Logger.println("Cookie 文件已删除");
        } else {
            Logger.warnln("Cookie 文件删除失败");
        }
    }
    
}
