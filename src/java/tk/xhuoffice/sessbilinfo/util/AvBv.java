package tk.xhuoffice.sessbilinfo.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 原始代码来自: https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/bvid_desc.md#Java
 * 原始算法来自: https://www.zhihu.com/question/381784377/answer/1099438784
 */

public class AvBv {

    public AvBv() {
        Logger.debugln("AvBv转换实用工具");
        for (int i = 0; i < 58; i++) {
            MAP.put(TABLE.charAt(i), i);
        }
    }
    
    private final String TABLE = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    private final int[] S = new int[]{11, 10, 3, 8, 4, 6};
    private final int XOR = 177451812;
    private final long ADD = 8728348608L;
    private final Map<Character, Integer> MAP = new HashMap<>();
    
    public String aidToBvid(int aid) {
        long x = (aid ^ XOR) + ADD;
        char[] chars = new char[]{'B', 'V', '1', ' ', ' ', '4', ' ', '1', ' ', '7', ' ', ' '};
        for (int i = 0; i < 6; i++) {
            int pow = (int) Math.pow(58, i);
            long i1 = x / pow;
            int index = (int) (i1 % 58);
            chars[S[i]] = TABLE.charAt(index);
        }
        String result = String.valueOf(chars);
        Logger.debugln("bvid "+result);
        return result;
    }
    
    public int bvidToAid(String bvid) {
        long r = 0;
        for (int i = 0; i < 6; i++) {
            r += MAP.get(bvid.charAt(S[i])) * Math.pow(58, i);
        }
        int result = (int) ((r - ADD) ^ XOR);
        Logger.debugln("aid "+result);
        return result;
    }
    
}