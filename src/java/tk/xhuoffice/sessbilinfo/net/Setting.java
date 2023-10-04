package tk.xhuoffice.sessbilinfo.net;

import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Setting {
    
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    
    public static String propPath = getPropsPath();
    
    public static Properties props = new Properties();
    
    public static String getPropsPath() {
        String path = System.getProperty("user.home") + "/.openbili/settings.properties";
        CookieFile.checkParentDir(path);
        return path;
    }
    
    public static void load() {
        // load from file
        props.load(new FileReader(propPath));
        // get properties
        ProxySetting.useSys = !props.getProperty("proxy.sys","true").equals("false"); // default: true
        ProxySetting.useProxy = props.getProperty("proxy.use","false").equals("true"); // default: false
    }
    
    public static String read(String key, String default) {
        return props.getProperty(key, default);
    }
    
    public static void set(String key, String val) {
        props.setProperty(key, val);
    }
    
}
