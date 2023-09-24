package tk.xhuoffice.sessbilinfo.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import tk.xhuoffice.sessbilinfo.Main;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Frame {

    public static int[] size = Size.get();
    
    public static void main(String[] args) {
        clear();
        printFullTitle();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future update = executor.submit(() -> update());
        Future process = executor.submit(() -> process());
        try {
            update.get();
            process.get();
        } catch(InterruptedException|java.util.concurrent.ExecutionException e) {
            OutFormat.outThrowable(e,3);
        }
        executor.shutdown();
    }
    
    public static void update() {
        while(true) {
            try {
                Thread.sleep(30000);
            } catch(InterruptedException e) {
                // do nothing...
            }
            size = Size.get();
            printFullTitle();
        }
    }
    
    public static void process() {
        Logger.ln();
        Main.main();
    }
    
    public static void clear() {
        for(int l = 0; l < size[1]; l++) {
            System.out.printf("\033[%d;0f",l);
            for(int c = 0; c < size[0]; c++) {
                System.out.print(" ");
            }
        }
    }
    
    public static void clearTitleLine() {
        System.out.printf("\033[0;0f");
        for(int c = 0; c < size[0]; c++) {
            System.out.print(" ");
        }
    }
    
    public static void printFullTitle() {
        clearTitleLine();
        printTitle();
        printTime();
        System.out.println();
        Pointer.set();
    }
    
    public static void printTitle() {
        System.out.print("\033[0;0f"+Main.SOFT_TITLE);
    }
    
    public static void printTime() {
        String time = getFormatDateTime();
        int length = time.length()-3;
        System.out.printf("\033[0;%df%s",size[0]-length,time.substring(0,length));
    }
    
    public static String getFormatDateTime() {
        return OutFormat.fullDateTime(System.currentTimeMillis()/1000);
    }
    
}
