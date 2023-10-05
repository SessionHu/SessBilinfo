package tk.xhuoffice.sessbilinfo.net;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Setting {
    
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    
    public static final String PROP_PATH = System.getProperty("user.home") + "/.openbili/settings.properties";
    
    public static Properties props = new Properties();
    
    public static void load() {
        // load from file
        try(FileReader reader = new FileReader(PROP_PATH)) {
            Logger.debugln("正在加载设置文件");
            props.load(reader);
        } catch(java.io.FileNotFoundException e) {
            Logger.debugln(e.toString());
            try {
                create();
            } catch(IOException ioe) {
                OutFormat.outThrowable(e,3);
            }
        } catch(IOException e) {
            OutFormat.outThrowable(e,3);
        }
        // get properties
        ProxySetting.useSys = !props.getProperty("proxy.sys","true").equals("false"); // default: true
        ProxySetting.useProxy = props.getProperty("proxy.use","false").equals("true"); // default: false
    }
    
    public static void create() throws IOException {
        CookieFile.checkParentDir(PROP_PATH);
        try(FileWriter writer = new FileWriter(PROP_PATH)) {
            Logger.debugln("正在创建设置文件");
            String dftprop = ""; {
                dftprop += "# =====================================================\n";
                dftprop += "# Proxy Settings\n";
                dftprop += "# proxy.sys > proxy.use (http) > proxy.use (socks)\n";
                dftprop += "# -----------------------------------------------------\n";
                dftprop += "# Use System Proxy (default: true)\n";
                dftprop += "proxy.sys=true\n";
                dftprop += "# Use Proxy settings below (default: false)\n";
                dftprop += "proxy.use=false\n";
                dftprop += "# Proxy Type (http/socks)\n";
                dftprop += "proxy.type=http\n";
                dftprop += "# Proxy Host (IP/Domain)\n";
                dftprop += "proxy.host=127.0.0.1\n";
                dftprop += "# Proxy Port (0-65535)\n";
                dftprop += "proxy.port=10809\n";
                dftprop += "# Proxy UserName and Password when proxy.type is http\n";
                dftprop += "proxy.username=\n";
                dftprop += "proxy.password=\n";
                dftprop += "# =====================================================\n";
            }
            writer.write(dftprop);
        }
    }
    
    public static String read(String key, String dft) {
        return props.getProperty(key, dft);
    }
    
    public static void set(String key, String val) {
        props.setProperty(key, val);
    }
    
}
