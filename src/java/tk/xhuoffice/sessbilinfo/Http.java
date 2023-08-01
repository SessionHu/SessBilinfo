package tk.xhuoffice.sessbilinfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {

    public static final String HTTP_USER_AGENT = "Dalvik/2.1.0 (Linux; U; Android 12; MLD-AL00 Build/HUAWEIMLD-AL00) 7.38.0 os/android model/MLD-AL00 mobi_app/Ai4cCreatorAndroid build/7380300 channel/master innerVer/7380310 osVer/12 network/2 grpc-java-cronet/1.36.1";
    
    public static String get(String inurl) {
        try {
            // 创建 URL 对象
            URL url = new URL(inurl);
            // 打开连接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            // 设置请求方法为 GET
            conn.setRequestMethod("GET");
            // 设置 User-Agent 请求头
            conn.setRequestProperty("User-Agent",HTTP_USER_AGENT);
            // 创建输入流并读取返回数据
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // 输出返回的 JSON 数据
            return response.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}