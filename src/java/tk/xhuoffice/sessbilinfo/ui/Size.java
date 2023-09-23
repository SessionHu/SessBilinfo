package tk.xhuoffice.sessbilinfo.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Size {
    
    public static void main(String[] args) {
        Logger.debug = true;
        try {
            int[] size = getSize();
            Logger.println(size[0]+"x"+size[1]);
        } catch(Exception e) {
            OutFormat.outThrowable(e,2);
        }
    }

    public static int[] getSize() throws IOException {
        // stty size
        int[] stty = sttySize();
        // return
        return stty;
    }

    public static int[] sttySize() throws IOException {
        // run process
        String line = null;
        String ty = "/dev/tty";
        for(int i = 0; i < 20; i++) {
            if(i>0&&i<11) {
                ty = "/dev/pty"+(i-1);
            } else if(i>10) {
                ty = "/dev/cons"+(i-10);
            }
            Process process = Runtime.getRuntime().exec(new String[]{"sh","-c","stty size <"+ty});
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                line = reader.readLine();
                if(line!=null) {
                    Logger.debugln("stty size: "+line);
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