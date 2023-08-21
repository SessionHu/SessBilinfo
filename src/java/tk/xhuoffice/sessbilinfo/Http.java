package tk.xhuoffice.sessbilinfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import tk.xhuoffice.sessbilinfo.CookieFile;
import tk.xhuoffice.sessbilinfo.Logger;
import tk.xhuoffice.sessbilinfo.OutFormat;

public class Http {

    public static final String ANDROID_APP_UA = "Dalvik/2.1.0 (Linux; U; Android 12; MLD-AL00 Build/HUAWEIMLD-AL00) 7.38.0 os/android model/MLD-AL00 mobi_app/Ai4cCreatorAndroid build/7380300 channel/master innerVer/7380310 osVer/12 network/2 grpc-java-cronet/1.36.1";
    public static final String WIN10_EDGE_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/115.0.1901.203";
    
    public static String get(String url) {
        for(int t = 0; t < 3; t++) {
            // 确定重试提示 log 级别
            int l;
            if(t<2) {
                l = 2;
            } else {
                l = 4;
            }
            // 进行请求
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
                Logger.println("域名解析失败, 请检查网络连接与hosts文件配置",l);
            } catch(javax.net.ssl.SSLHandshakeException e) {
                // SSL 握手错误
                Logger.println("SSL 握手失败, 请检查网络连接是否稳定",l);
            } catch(Exception e) {
                // 异常报告
                Logger.println("HTTP 请求发生未知错误",4);
                OutFormat.outException(e,4);
                break;
            }
        }
        System.exit(64);
        return ""; // 防止编译报错
    }
    
    public static String getDataFromURL(String inurl) throws Exception {
        // 使用本地 Cookie (有效期 24 h)
        String[] cookie = CookieFile.load();
        // 当本地 Cookie 不可用时重新获取
        if(cookie.length==0) {
            cookie = getDefaultCookie();
        }
        // 创建请求
        HttpURLConnection conn = setGetConnURL(inurl,ANDROID_APP_UA,cookie);
        // 读取数据
        String data = readResponseData(conn);
        // 返回结果
        return data;
    }
    
    public static HttpURLConnection setGetConnURL(String inurl, String ua, String[] cookie) throws Exception {
        // 创建 URL 对象
        URL url = new URL(inurl);
        // 打开连接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // 设置请求方法为 GET
        conn.setRequestMethod("GET");
        // 设置 User-Agent 请求头
        conn.setRequestProperty("User-Agent",ua);
        // 设置 Cookie
        if(cookie.length==0) {
            // do nothing...
        } else {
            String cookies = "";
            for(int i = 0; i < cookie.length; i++) {
                String[] parts = cookie[i].split(";");
                String cookieVal = parts[0];
                cookies += cookieVal + "; ";
            }
            cookies = cookies.substring(0, cookies.length() - 2);
            conn.setRequestProperty("Cookie", cookies);
        }
        return conn;
    }
    
    public static String readResponseData(HttpURLConnection conn) throws Exception {
        // 创建输入流并读取返回数据
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // 返回返回数据
        return response.toString();
    }
    
    public static String[] getDefaultCookie() throws Exception {
        // 设置请求到 https://www.bilibili.com/
        String url = "https://www.bilibili.com/";
        HttpURLConnection conn = setGetConnURL(url, WIN10_EDGE_UA, new String[0]);
        conn.connect();
        // 从响应头中获取 Cookie
        Map<String, List<String>> headers = conn.getHeaderFields();
        List<String> cookies = headers.get("Set-Cookie");
        String[] cookie = new String[cookies.size()];
        for(int i = 0; i < cookies.size(); i++) {
            cookie[i] = cookies.get(i);
        }
        // 保存 Cookie
        CookieFile.save(cookie);
        // 返回 SESSDATA
        return cookie;
    }
    
}
