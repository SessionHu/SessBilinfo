package tk.xhuoffice.sessbilinfo.util;

public class Logger {

    public static boolean debug;
    
    private static void print(String str, int lv, String cN, String mN) {
        String[] levels = {"DEBUG","INFO","WARN","ERROR","FATAL"};
        if(lv>=1&&lv<=4) {
            String formatted = String.format("[%s] [%s] [%s] ", levels[lv], cN, mN);
            str = str.replaceAll("\\n", "\n"+formatted);
            if(lv>=3) {
                System.err.print(formatted+str);
            } else {
                System.out.print(formatted+str);
            }
        } else if(debug) {
            str = str.replaceAll("\\n", "\n[DEBUG] ["+cN+"] ["+mN+"] ");
            System.out.format("[DEBUG] [%s] [%s] %s", cN, mN, str);
        }
    }
    
    private static void println(String str, int lv) {
        synchronized(Logger.class) {
            // 获取完整类名
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            // 获取最后一个 '.' 位置
            int lastDotIndex = fullClassName.lastIndexOf('.');
            // 仅获取类名
            String cN = fullClassName.substring(lastDotIndex + 1);
            // 获取方法名
            String mN = Thread.currentThread().getStackTrace()[3].getMethodName();
            // 打印信息
            print(str,lv,cN,mN);
            // 换行
            System.out.println();
        }
    }

    public static void println(String str, long lv) {
        println(str,(int)lv);
    }

    public static void println(String str) {
        println(str,1); // 信息
    }

    public static void warnln(String str) {
        println(str,2); // 警告
    }
    
    public static void errln(String str) {
        println(str,3); // 错误
    }
    
    public static void fataln(String str) {
        println(str,4); // 致命
    }

    public static void debugln(String str) {
        if(debug) {
            println(str,0); // 调试
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
