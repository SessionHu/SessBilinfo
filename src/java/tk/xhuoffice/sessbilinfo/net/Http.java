package tk.xhuoffice.sessbilinfo.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Http {

    public static final String ANDROID_APP_UA = "Dalvik/2.1.0 (Linux; U; Android 12; MLD-AL00 Build/HUAWEIMLD-AL00) 7.38.0 os/android model/MLD-AL00 mobi_app/Ai4cCreatorAndroid build/7380300 channel/master innerVer/7380310 osVer/12 network/2 grpc-java-cronet/1.36.1";
    public static final String WIN10_EDGE_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/115.0.1901.203";
    public static final String DEFAULT_COOKIE = "b_ut=7; i-wanna-go-back=-1; b_nut=1693285885; buvid3=88FDE25E-30BA-1D47-62B8-FAA9D96069D785506infoc; innersign=0";
    
    public static String[] cookieCache = null;
    
    public static boolean useCookie = true;
    public static int timeoutc = 5000;
    public static int timeoutr = 12000;

    public static String encode(String str) {
        return StringCoder.urlEncode(str);
    }
    
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
                Logger.fataln("非法的 URL "+url);
                break;
            } catch(java.net.UnknownHostException e) {
                // 域名解析错误
                Logger.println("域名解析失败, 请检查网络连接与hosts文件配置",l);
            } catch(javax.net.ssl.SSLHandshakeException e) {
                // SSL 握手错误
                Logger.println("SSL 握手失败, 请检查网络连接是否稳定",l);
            } catch(java.net.ConnectException e) {
                String msg = e.getMessage();
                if(msg.contains("timed out")) {
                    // 连接超时
                    Logger.println("连接超时, 请检查网络连接是否稳定",l);
                } else {
                    handleUnknownException(e);
                    break;
                }
            } catch(java.net.SocketTimeoutException|javax.net.ssl.SSLException e) {
                String msg = e.getMessage();
                if(msg.equals("Read timed out")) {
                    // 读取超时
                    Logger.println("读取超时, 请检查网络连接是否稳定",l);
                } else {
                    handleUnknownException(e);
                    break;
                }
            } catch(Exception e) {
                // 异常报告
                handleUnknownException(e);
                break;
            }
            Logger.debugln("第 "+(t+1)+" 次重试");
        }
        System.exit(64);
        return ""; // 防止编译报错
    }
    
    private static void handleUnknownException(Exception e) {
        Logger.fataln("HTTP 请求发生未知异常");
        OutFormat.outThrowable(e,4);
    }
    
    public static String getDataFromURL(String inurl) throws IOException {
        // 缓存不可用时加载本地 Cookie
        if(cookieCache==null) {
            cookieCache = CookieFile.load();
        }
        // 本地不可用时重新获取
        if(cookieCache.length==0) {
            cookieCache = getDefaultCookie();
        }
        // 处理 Cookie 缓存
        for(int i = 0; i < cookieCache.length; i++) {
            cookieCache[i] = cookieCache[i].split(";")[0];
        }
        // 创建请求
        HttpURLConnection conn = setGetConnURL(inurl);
        // 读取数据
        String data = readResponseData(conn);
        // 返回结果
        return data;
    }
    
    public static HttpURLConnection setGetConnURL(String inurl) throws IOException {
        // print debug log
        Logger.debugln("设置请求到 "+OutFormat.shorterString(inurl));
        // 创建 URL 对象
        URL url = new URL(inurl);
        // 打开连接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // 设置请求方法为 GET
        conn.setRequestMethod("GET");
        // 设置连接超时时间
        conn.setConnectTimeout(timeoutc);
        // 设置读取超时时间
        conn.setReadTimeout(timeoutr);
        // 设置 User-Agent 请求头
        {
            // choose userAgent
            String userAgent = null;
            if(inurl.startsWith("https://api")) {
                userAgent = ANDROID_APP_UA;
            } else {
                userAgent = WIN10_EDGE_UA;
            }
            // set User-Agent
            conn.setRequestProperty("User-Agent",userAgent);
            // print User-Agent
            Logger.debugln("User-Agent: "+userAgent);
        }
        // 设置 Cookie
        if(useCookie) {
            String cookies = "";
            // 提取 Cookie
            try {
                for(String cookieVal : cookieCache) {
                    cookies += cookieVal + "; ";
                }
                cookies = cookies.substring(0, cookies.length() - 2);
            } catch(NullPointerException e) {
                Logger.debugln("Cookie 为空, 使用内置 Cookie");
                cookies = DEFAULT_COOKIE;
            }
            // 设置 Cookie 内容
            conn.setRequestProperty("Cookie", cookies);
            // 打印 Cookie (加密)
            cookies = cookies.replaceAll("(?<=\\=)[^;]+", "xxx");
            Logger.debugln("Cookie: "+cookies);
        }
        // connect & return
        conn.connect();
        return conn;
    }
    
    public static String readResponseData(HttpURLConnection conn) throws IOException {
        // 打印调试日志
        String url = conn.getURL().toString();
        Logger.debugln("读取返回数据从 "+OutFormat.shorterString(url));
        // 读取 HTTP 状态码
        int responseCode = conn.getResponseCode();
        Logger.debugln("HTTP "+responseCode+" "+conn.getResponseMessage());
        // 读取返回数据
        BufferedReader in;
        if(responseCode==200) {
            // 正常读取返回数据
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
        } else {
            // HTTP 状态码不正常时
            in = new BufferedReader(new InputStreamReader(conn.getErrorStream(),"UTF-8"));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // 返回返回数据
        return response.toString();
    }
    
    public static String[] getDefaultCookie() {
        try {
            Logger.debugln("联机获取默认 Cookie");
            // 设置请求到 https://www.bilibili.com/
            useCookie = false;
            HttpURLConnection conn = setGetConnURL("https://www.bilibili.com/");
            // 从响应头中获取 Cookie
            Map<String, List<String>> headers = conn.getHeaderFields();
            List<String> cookies = headers.get("Set-Cookie");
            String[] cookie = cookies.toArray(new String[0]);
            // 保存 Cookie
            CookieFile.save(cookie);
            // 返回 Cookie
            useCookie = true;
            return cookie;
        } catch(IOException e) {
            Logger.warnln("联机获取 Cookie 发生异常, 使用内置 Cookie");
            return DEFAULT_COOKIE.split(";");
        }
    }
    
}
