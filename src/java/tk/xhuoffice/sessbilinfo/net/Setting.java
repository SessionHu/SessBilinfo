package tk.xhuoffice.sessbilinfo.net;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Setting {
    
    public static final String PROP_PATH = System.getProperty("user.home") + "/.openbili/settings.properties";
    
    private static Properties props = new Properties();
    
    public static void load() {
        // load from file
        try(InputStreamReader reader = new InputStreamReader(new FileInputStream(PROP_PATH),StringCoder.UTF_8)) {
            props.load(reader);
        } catch(java.io.FileNotFoundException e) {
            Logger.debugln(e.toString());
            create();
        } catch(IOException e) {
            OutFormat.outThrowable(e,3);
        }
    }

    
    public static void create() {
        try {
            CookieFile.checkParentDir(PROP_PATH);
        } catch(IOException e) {
            // do nothing
        }
        try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(PROP_PATH),StringCoder.UTF_8)) {
            StringBuilder dftprop = new StringBuilder(); 
            dftprop.append("# Settings\n");
            dftprop.append("# Comming soon...\n");
            writer.write(dftprop.toString());
        } catch(IOException e) {
            OutFormat.outThrowable(e,3);
        }
    }
    
    public static String read(String key, String dft) {
        return props.getProperty(key, dft);
    }
    
    public static void set(String key, String val) {
        props.setProperty(key, val);
    }
    
}
