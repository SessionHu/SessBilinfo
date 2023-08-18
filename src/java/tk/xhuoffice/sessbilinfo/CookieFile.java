package tk.xhuoffice.sessbilinfo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import tk.xhuoffice.sessbilinfo.Logger;
import tk.xhuoffice.sessbilinfo.OutFormat;

public class CookieFile {

    public static final long COOKIE_EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24 h
    
    public static String getCookieFilePath() {
        // 获取系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        // 根据系统选择路径
        if (osName.contains("windows")) {
            // Windows
            String usrdir = System.getenv("USERPROFILE");
            return usrdir + "\\.openbili\\cookie.txt";
        } else if (osName.contains("mac") || osName.contains("linux")) {
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
                // 获取路径
                String path = getCookieFilePath();
                // 检查父目录
                File parentDir = new File(path).getParentFile();
                if(!parentDir.exists()) { // 当父目录不存在时
                    parentDir.mkdirs();
                    hideWinDir(parentDir.getCanonicalPath());
                }
                // 写入文件
                try(FileWriter writer = new FileWriter(path)) {
                    String cookies = "";
                    for(int l = 0; l < cookie.length; l++) {
                        cookies += cookie[l]+"\n";
                    }
                    writer.write(System.currentTimeMillis() + "\n" + cookies);
                }
            } catch(Exception e) {
                Logger.println("Cookie 文件写入失败",2);
                OutFormat.outException(e,2);
            }
        }
    }
    
    public static void hideWinDir(String path) throws Exception {
        // 运行 attrib 命令来设置目录的隐藏属性
        Runtime.getRuntime().exec("attrib +H " + path);
    }
    
    public static String[] load() throws IOException {
        // 输入文件
        File file = new File(getCookieFilePath());
        // 文件是否存在
        if(!file.exists()) {
            return [];
        }
        // 读取文件
        FileReader reader = new FileReader(file);
        char[] buffer = new char[(int) file.length()];
        reader.read(buffer);
        reader.close();
        // 处理数据
        String[] lines = new String(buffer).split("\n");
        long timestamp = Long.parseLong(lines[0]);
        // 验证文件
        if (System.currentTimeMillis() - timestamp > COOKIE_EXPIRE_TIME) {
            // 文件过期
            return [];
        } else {
            // 读取数据
            for(int i = 1; i < lines.length; i++)
            String[] cookie;
            cookie[i-1] = lines[i];
            // 返回数据
            return cookie;
        }
    }
    
}
