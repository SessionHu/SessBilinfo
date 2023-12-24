package tk.xhuoffice.sessbilinfo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Convert Bilibili video Bvid into Aid and back.
 * @see <a href="https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/bvid_desc.md#Java">bvid说明
</a>
 * @see <a href="https://www.zhihu.com/question/381784377/answer/1099438784">如何看待 2020 年 3 月 23 日哔哩哔哩将稿件的「av 号」变更为「BV 号」？</a>
 */

public class AvBv {
    
    private int aid;
    private String bvid;
    
    public int getAid() {
        return this.aid;
    }
    
    public String getAvid() {
        return "av"+this.aid;
    }
    
    public String getBvid() {
        return this.bvid;
    }
    
    /**
     * Basic constructor.
     */
    public AvBv() {
        Logger.debugln("AvBv转换实用工具");
    }
    
    /**
     * Construct with {@code aid}.
     * @param aid Aid
     */
    public AvBv(int aid) {
        this();
        this.aid = aid;
        this.bvid = aidToBvid(aid);
    }
    
    /**
     * Construct with {@code bvid}.
     * @param bvid Bvid with {@code .toLowerCase().startsWith("bv")==true}
     */
    public AvBv(String bvid) {
        this();
        this.bvid = bvid;
        this.aid = bvidToAid(bvid);
    }
    
    // 几个常量
    private static final String TABLE = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    private static final int[] S = new int[]{11, 10, 3, 8, 4, 6};
    private static final int XOR = 177451812;
    private static final long ADD = 8728348608L;
    private static final Map<Character, Integer> MAP = new HashMap<>();
    static {
        for(int i = 0; i < 58; i++) {
            MAP.put(TABLE.charAt(i), i);
        }
    }
    
    // 转换方法
    public String aidToBvid(int aid) {
        long x = (aid ^ XOR) + ADD;
        char[] chars = new char[]{'B', 'V', '1', ' ', ' ', '4', ' ', '1', ' ', '7', ' ', ' '};
        for(int i = 0; i < 6; i++) {
            int pow = (int) Math.pow(58, i);
            long i1 = x / pow;
            int index = (int) (i1 % 58);
            chars[S[i]] = TABLE.charAt(index);
        }
        String result = String.valueOf(chars);
        // 输出结果
        Logger.debugln("bvid "+result);
        this.aid = aid;
        this.bvid = result;
        return result;
    }
    
    public int bvidToAid(String bvid) {
        long r = 0;
        for(int i = 0; i < 6; i++) {
            r += MAP.get(bvid.charAt(S[i])) * Math.pow(58, i);
        }
        int result = (int)((r - ADD) ^ XOR);
        // 输出结果
        Logger.debugln("aid "+result);
        this.aid = result;
        this.bvid = bvid;
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("av%d/%s",this.aid,this.bvid);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj==this) {
            return true;
        }
        if(obj==null) {
            return false;
        }
        if(obj instanceof AvBv) {
            AvBv avbv = (AvBv)obj;
            return (avbv.aid==this.aid)&&Objects.equals(avbv.bvid,this.bvid);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1454;
        result = prime * result + this.aid;
        result = prime * result + this.bvid.hashCode();
        result = prime * result + this.toString().hashCode();
        return result;
    }
    
}
