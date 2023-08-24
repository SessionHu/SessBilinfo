package tk.xhuoffice.sessbilinfo;

public class Logger {

    public static boolean debug = false;
    
    public static void print(String str, int lv) {
        if(lv==1) {
            // 信息
            str = str.replaceAll("\\n","\n[INFO] ");
            System.out.print("[INFO] "+str);
        } else if(lv==2) {
            // 警告
            str = str.replaceAll("\\n","\n[WARN] ");
            System.out.print("[WARN] "+str);
        } else if(lv==3) {
            // 错误
            str = str.replaceAll("\\n","\n[ERROR] ");
            System.err.print("[ERROR] "+str);
        } else if(lv==4) {
            // 致命
            str = str.replaceAll("\\n","\n[FATAL] ");
            System.err.print("[FATAL] "+str);
        } else {
            // 调试(默认)
            if(debug) {
                str = str.replaceAll("\\n","\n[DEBUG] ");
                System.err.print("[DEBUG] "+str);
            }
        }
    }
    
    public static void println(String str, int lv) {
        // 打印信息
        print(str,lv);
        // 根据情况确定换行符
        if((lv>0&&lv<5)||(!(lv>0&&lv<5)&&debug)) {
            System.out.println();
        }
    }
    
    public static void inputHere(String... tip) {
        if(tip.length!=0) {
            System.out.print(tip[0]+" > ");
        } else {
            System.out.print("> ");
        }
    }
    
    public static void ln() {
        System.out.println();
    }
}
