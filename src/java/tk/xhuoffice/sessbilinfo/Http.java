package tk.xhuoffice.sessbilinfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import tk.xhuoffice.sessbilinfo.Logger;

public class Http {

    public static final String ANDROID_APP_UA = "Dalvik/2.1.0 (Linux; U; Android 12; MLD-AL00 Build/HUAWEIMLD-AL00) 7.38.0 os/android model/MLD-AL00 mobi_app/Ai4cCreatorAndroid build/7380300 channel/master innerVer/7380310 osVer/12 network/2 grpc-java-cronet/1.36.1";
    
    public static String get(String url) {
        for(int t = 2; t < 5; t++) {
            try {
                // 请求数据
                String data = getDataFromURL(url);
                // 输出返回的数据
                return data;
            } catch(java.net.MalformedURLException e) {
                // URL 不合法
                Logger.println("非法的 URL "+url,4);
                break;
            } catch(java.net.UnknownHostException e) {
                // 域名解析错误
                Logger.println("域名解析失败, 请检查网络连接与hosts文件配置",t);
            } catch(javax.net.ssl.SSLHandshakeException e) {
                // SSL 握手错误
                Logger.println("SSL 握手失败, 请检查网络连接是否稳定",t);
            } catch(Exception e) {
                // 异常报告
                Logger.println("HTTP 请求发生未知错误",4);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String stackTrace = sw.toString();
                Logger.println(stackTrace,4);
                break;
            }
        }
        System.exit(64);
        return ""; // 防止编译报错
    }
    
    public static String getDataFromURL(String inurl) throws Exception {
        // 创建 URL 对象
        URL url = new URL(inurl);
        // 打开连接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // 设置请求方法为 GET
        conn.setRequestMethod("GET");
        // 设置 User-Agent 请求头
        conn.setRequestProperty("User-Agent",ANDROID_APP_UA);
        // 创建输入流并读取返回数据
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // 返回数据
        return response.toString();
    }
    
}
