package tk.xhuoffice.sessbilinfo;

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
    
}