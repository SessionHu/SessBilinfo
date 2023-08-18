import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import tk.xhuoffice.sessbilinfo.Logger;

public class CookieFile {

    public static final long COOKIE_EXPIRE_TIME = 30 * 60 * 1000; // 30 min
    
    public static String getCookieFilePath() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            String usrdir = System.getenv("USERPROFILE");
            return usrdir + "\\.openbili\\cookie.txt";
        } else if (osName.contains("mac") || osName.contains("linux")) {
            String usrHome = System.getProperty("user.home");
            return usrHome + "/.openbili/cookie.txt";
        } else {
            // 其他操作系统
            Logger.println("未知的操作系统, Cookie 将保存在当前工作目录下",2);
            return "cookie.txt";
        }
    }
    
    public static void save(String cookie) throws IOException {
        // 准备文件
        FileWriter writer = new FileWriter(getCookieFilePath());
        // 写入文件
        writer.write(System.currentTimeMillis() + "\n" + cookie);
        writer.close();
    }

    public static String load() throws IOException {
        // 输入文件
        File file = new File(getCookieFilePath());
        // 文件是否存在
        if(!file.exists()) {
            return null;
        }
        // 读取文件
        FileReader reader = new FileReader(file);
        char[] buffer = new char[(int) file.length()];
        reader.read(buffer);
        reader.close();
        // 处理文件
        String[] lines = new String(buffer).split("\n");
        long timestamp = Long.parseLong(lines[0]);
        String cookie = lines[1];
        // 验证文件
        if (System.currentTimeMillis() - timestamp > COOKIE_EXPIRE_TIME) {
            // 文件过期
            return null;
        } else {
            // 返回数据
            return cookie;
        }
    }
    
}
