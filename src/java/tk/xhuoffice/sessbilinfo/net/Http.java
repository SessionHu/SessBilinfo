package tk.xhuoffice.sessbilinfo.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Http {
    
    // NO <init>
    private Http() {}

    protected static final String ANDROID_APP_UA = "Dalvik/2.1.0 (Linux; U; Android 12; MLD-AL00 Build/HUAWEIMLD-AL00) 7.38.0 os/android model/MLD-AL00 mobi_app/Ai4cCreatorAndroid build/7380300 channel/master innerVer/7380310 osVer/12 network/2 grpc-java-cronet/1.36.1";
    protected static final String ANDROID_TV_WEBVIEW_UA = "Mozilla/5.0 (Linux; U; Android 4.2.1; zh-cn; 9R15_E710U Build/JOP40D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30";
    protected static final String WIN8X_EDGE_UA = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.100";
    protected static final String WIN10_EDGE_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/115.0.1901.203";
    protected static final String MAC_OS_X_INTEL_SAFARI_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_16_2) AppleWebKit/537.36 (KHTML, like Gecko) Version/14.5.70 Safari/537.36";
    protected static final String LINUX_UBUNTU_FIREFOX_UA = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/109.0";
    private static final String DEFAULT_COOKIE = "b_ut=7; i-wanna-go-back=-1; b_nut=1693285885; buvid3=88FDE25E-30BA-1D47-62B8-FAA9D96069D785506infoc; innersign=0";
    
    private static String[] cookieCache = null;
    private static String userAgentCache = null;
    
    public static void clearCache() {
        cookieCache = null;
        userAgentCache = null;
    }
    
    public static boolean useCookie = true;
    /**
     * Connect timeout. */
    public static int timeoutc = 5000;
    /**
     * Read timeout. */
    public static int timeoutr = 10000;

    /**
     * Encode URL with {@link StringCoder#urlEncode(String)}.
     * @param str Raw URL
     * @return    Encoded URL
     */
    public static String encode(String str) {
        return StringCoder.urlEncode(str);
    }
    
    public static String get(String url) {
        String connMsg = null;
        Throwable excause = null;
        for(int t = 0; t < 3; t++) {
            // 确定重试提示 log 级别
            int l;
            if(t<2) {
                l = 2;
            } else {
                l = 3;
            }
            // 进行请求
            try {
                // 请求数据
                String data = getDataFromURL(url);
                // 输出返回的数据
                return data;
            } catch(java.net.UnknownHostException e) {
                // 域名解析错误
                Logger.println(connMsg="域名解析失败, 请检查网络连接与hosts文件配置",l);
                excause = e;
            } catch(javax.net.ssl.SSLHandshakeException e) {
                // SSL 握手错误
                Logger.println(connMsg="SSL 握手失败, 请检查网络连接是否稳定",l);
                excause = e;
            } catch(java.net.ConnectException e) {
                String msg = e.getMessage();
                excause = e;
                if(msg.contains("timed out")) {
                    // 连接超时
                    Logger.println(connMsg="连接超时, 请检查网络连接是否稳定",l);
                } else {
                    handleUnknownException(e);
                    break;
                }
            } catch(java.net.SocketTimeoutException|javax.net.ssl.SSLException e) {
                String msg = e.getMessage();
                excause = e;
                if(msg.equals("Read timed out")) {
                    // 读取超时
                    Logger.println(connMsg="读取超时, 请检查网络连接是否稳定",l);
                } else if(msg.toLowerCase().equals("connect timed out")) {
                    // 连接超时
                    Logger.println(connMsg="连接超时, 请检查网络连接是否稳定",l);
                } else {
                    handleUnknownException(e);
                    break;
                }
            } catch(Exception e) {
                // 异常报告
                connMsg = handleUnknownException(e);
                excause = e;
                break;
            }
            Logger.debugln("第 "+(t+1)+" 次重试");
        }
        throw new HttpConnectException(connMsg,excause);
    }
    
    private static String handleUnknownException(Exception e) {
        Logger.fataln("HTTP 请求发生未知异常");
        OutFormat.outThrowable(e,4);
        return e.getMessage();
    }
    
    public static String getDataFromURL(String inurl) throws IOException {
        // load cookie
        loadCookieCache();
        // 创建请求
        HttpURLConnection conn = setGetConnURL(inurl);
        // 读取数据
        String data = readResponseData(conn);
        // 返回结果
        return data;
    }
    
    public static void loadCookieCache() {
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
    }

    
    /**
     * Set {@code HttpURLConnection} with request method GET.
     * @param inurl  URL in string.
     * @return       {@code HttpURLConnection} with request method GET.
     */
    public static HttpURLConnection setGetConnURL(String inurl) {
        try {
            return setGetConnURL(new URI(inurl).toURL());
        } catch(java.net.URISyntaxException | java.net.MalformedURLException e) {
            throw new HttpConnectException("非法的 URL: "+e.getMessage(),e);
        }
    }
    
    /**
     * Set {@code HttpURLConnection} with request method GET.
     * @param url  URL.
     * @return     {@code HttpURLConnection} with request method GET.
     */
    public static HttpURLConnection setGetConnURL(URL url) {
        // print debug log
        String strurl = url.toString();
        Logger.debugln("设置请求到 "+OutFormat.shorterString(16,strurl,24));
        // 打开连接
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
        } catch(IOException e) {
            throw new HttpConnectException("打开连接失败",e);
        }
        // 设置请求方法为 GET
        try {
            conn.setRequestMethod("GET");
        } catch(java.net.ProtocolException e) {
            throw new HttpConnectException("非法的请求方法",e);
        }
        // 设置连接超时时间
        conn.setConnectTimeout(timeoutc);
        // 设置读取超时时间
        conn.setReadTimeout(timeoutr);
        // 设置 User-Agent 请求头
        {
            // choose userAgent
            String userAgent = null;
            if(strurl.contains("web")||strurl.contains("html5")) {
                if(userAgentCache!=null) {
                    userAgent = userAgentCache;
                } else {
                    String os = System.getProperty("os.name").toLowerCase();
                    if(os.contains("windows")) {
                        if(os.contains("8")) {
                            userAgent = WIN8X_EDGE_UA;
                        } else {
                            userAgent = WIN10_EDGE_UA;
                        }
                    } else if(os.contains("linux")) {
                        userAgent = LINUX_UBUNTU_FIREFOX_UA;
                    } else if(os.contains("mac")) {
                        userAgent = MAC_OS_X_INTEL_SAFARI_UA;
                    } else {
                        userAgent = ANDROID_TV_WEBVIEW_UA;
                    }
                    userAgentCache = userAgent;
                }
            } else {
                userAgent = ANDROID_APP_UA;
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
        // 设置 Referer
        if(strurl.contains("bili")) {
            conn.setRequestProperty("referer","https://www.bilibili.com");
        }
        // return
        return conn;
    }
    
    public static String readResponseData(HttpURLConnection conn) throws IOException {
        // 打印调试日志
        Logger.debugln("读取返回数据从 "+OutFormat.shorterString(16,conn.getURL().toString(),24));
        // connect
        conn.connect();
        // header
        for(String line : getFormattedHeaderFields(conn)) {
            Logger.debugln(line);
        }
        // encoding
        String encoding = "UTF-8";
        try {
            for(String part : conn.getContentType().split(";\\s*")) {
                if(part.matches("^[^=]+=[^=]+$") && part.toLowerCase().startsWith("charset")) {
                    encoding = part.substring(part.indexOf("=")+1);
                }
            }
        } catch(NullPointerException e) {
            // server did not return encoding
        }
        // 读取返回数据
        InputStreamReader in;
        if(conn.getResponseCode()==200) {
            // 正常读取返回数据
            in = new InputStreamReader(conn.getInputStream(),encoding);
        } else {
            // HTTP 状态码不正常时
            in =new InputStreamReader(conn.getErrorStream(),encoding);
        }
        int c;
        StringBuilder response = new StringBuilder();
        while((c = in.read()) != -1) {
            response.append((char)c);
        }
        in.close();
        // 打印调试日志
        Logger.debugln("读取返回数据从 "+OutFormat.shorterString(16,conn.getURL().toString(),24)+" 完毕");
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
            conn.connect();
            String[] cookie = conn.getHeaderFields().get("Set-Cookie").toArray(new String[0]);
            // 保存 Cookie
            CookieFile.save(cookie);
            // 返回 Cookie
            useCookie = true;
            return cookie;
        } catch(NullPointerException | IOException e) {
            Logger.warnln("联机获取 Cookie 发生异常, 使用内置 Cookie");
            OutFormat.outThrowable(e,0);
            return DEFAULT_COOKIE.split(";\\s*");
        }
    }

    public static String[] getFormattedHeaderFields(HttpURLConnection conn) {
        Map<String,List<String>> heads = conn.getHeaderFields();
        List<String> result = new ArrayList<>();
        for(Map.Entry<String,List<String>> entry : heads.entrySet()) {
            for(String val : entry.getValue()) {
                if(entry.getKey()!=null) {
                    result.add(entry.getKey()+": "+val);
                } else {
                    result.add(val);
                }
            }
        }
        return result.toArray(new String[0]);
    }
    
}
