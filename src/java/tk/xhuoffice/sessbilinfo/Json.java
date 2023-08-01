package tk.xhuoffice.sessbilinfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Json {

    public static String formatJson(String json, int indent) {
        StringBuilder result = new StringBuilder();
        int level = 0; // 当前缩进级别
        boolean inQuote = false; // 是否在引号内
        for(int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if(c == '\"') {
                inQuote = !inQuote;
            }
            if(!inQuote) {
                if(c == '{' || c == '[') { // 遇到左括号，增加缩进级别
                    result.append(c);
                    result.append('\n');
                    level++;
                    for(int j = 0; j < level * indent; j++) {
                        result.append(' ');
                    }
                } else if(c == '}' || c == ']') { // 遇到右括号，减少缩进级别
                    result.append('\n');
                    level--;
                    for(int j = 0; j < level * indent; j++) {
                        result.append(' ');
                    }
                    result.append(c);
                } else if(c == ',') { // 遇到逗号，换行并缩进
                    result.append(c);
                    result.append('\n');
                    for(int j = 0; j < level * indent; j++) {
                        result.append(' ');
                    }
                } else if(c == ':') { // 遇到冒号，添加空格
                    result.append(c);
                    result.append(' ');
                } else {
                result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    public static int getIntAfterStr(String inputJson, String str) {
        // 获取查找内容
        Pattern pattern = Pattern.compile("\""+str+"\":(-?\\d+),");
        // 检测查找内容
        Matcher matcher = pattern.matcher(inputJson);
        int num = -8888;
        if(matcher.find()) {
            // 正常赋值
            num = Integer.parseInt(matcher.group(1));
        } else {
            // 异常处理
            System.err.println("fatal: couldn't find any number after "+str);
            System.exit(127);
        }
        return num;
    }
    
}