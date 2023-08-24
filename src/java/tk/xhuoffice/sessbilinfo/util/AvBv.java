package tk.xhuoffice.sessbilinfo.util;

import java.util.Map;
import java.util.HashMap;

/**
 * 代码来自: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/bvid_desc.md#Java
 * 算法来自: https://www.zhihu.com/question/381784377/answer/1099438784
 */

public class AvBv {
    
    private static final String TABLE = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    private static final int[] S = new int[]{11, 10, 3, 8, 4, 6};
    private static final int XOR = 177451812;
    private static final long ADD = 8728348608L;
    private static final Map<Character, Integer> MAP = new HashMap<>();
    
    static {
        for (int i = 0; i < 58; i++) {
            MAP.put(TABLE.charAt(i), i);
        }
    }
    
    public static String aidToBvid(int aid) {
        long x = (aid ^ XOR) + ADD;
        char[] chars = new char[]{'B', 'V', '1', ' ', ' ', '4', ' ', '1', ' ', '7', ' ', ' '};
        for (int i = 0; i < 6; i++) {
            int pow = (int) Math.pow(58, i);
            long i1 = x / pow;
            int index = (int) (i1 % 58);
            chars[S[i]] = TABLE.charAt(index);
        }
        return String.valueOf(chars);
    }
    
    public static int bvidToAid(String bvid) {
        long r = 0;
        for (int i = 0; i < 6; i++) {
            r += MAP.get(bvid.charAt(S[i])) * Math.pow(58, i);
        }
        return (int) ((r - ADD) ^ XOR);
    }
    
}