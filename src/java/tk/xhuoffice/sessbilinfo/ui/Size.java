package tk.xhuoffice.sessbilinfo.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Size {
    
    public static Size get() throws IOException {
        return new Size();
    }
    
    private int cols;
    private int lns;
    
    public int cols() {
        return this.cols;
    }
    
    public int lns() {
        return this.lns;
    }
    
    protected Size() throws IOException {
        // stty size
        Size stty = sttySize();
        this.cols = stty.cols;
        this.lns = stty.lns;
    }
    
    protected Size(int c, int l) {
        this.cols = c;
        this.lns = l;
    }
    
    private static Size sttySize() throws IOException {
        // run process
        String line = null;
        String[] ttys = {"/dev/tty","/dev/pty0"};
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
            return new Size(c,l);
        }
    }
    
}
