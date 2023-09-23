package tk.xhuoffice.sessbilinfo.ui;

import tk.xhuoffice.sessbilinfo.Main;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Frame {

    public static int[] size = Size.get();
    
    public static void main(String[] args) {
        clear();
        printTitle();
    }
    
    public static void clear() {
        for(int l = 0; l < size[1]; l++) {
            System.out.printf("\033[%d;0f",l);
            for(int c = 0; c < size[0]; c++) {
                System.out.print(" ");
            }
        }
    } 
    
    public static void printTitle() {
        System.out.print("\033[0;0f");
        System.out.println(Main.SOFT_TITLE);
    }
    
}
