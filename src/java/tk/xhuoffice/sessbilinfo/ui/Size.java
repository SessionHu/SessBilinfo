package tk.xhuoffice.sessbilinfo.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Size {
    
    public static int[] get() {
        try {
            return getSize();
        } catch(Exception e) {
            return new int[]{80,24};
        }
    }
    
    public static int[] getSize() throws IOException {
        // stty size
        return sttySize();
    }
    
    public static int[] sttySize() throws IOException {
        // run process
        String line = null;
        String[] ttys = {System.getenv("tty"),"/dev/tty","/dev/pty0"};
        for(String tty : ttys) {
            Process process = Runtime.getRuntime().exec(new String[]{"sh","-c","stty size <"+tty});
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                if((line=reader.readLine())!=null) {
                    break;
                }
            }
        }
        if(line==null) {
            // throw exception
            throw new IOException("could not get Terminal size");
        } else {
            // get size
            String[] size = line.split(" ");
            int l = Integer.parseInt(size[0]);
            int c = Integer.parseInt(size[1]);
            // return
            return new int[]{c,l};
        }
    }
    
}
