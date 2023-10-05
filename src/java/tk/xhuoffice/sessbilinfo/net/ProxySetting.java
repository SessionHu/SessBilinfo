package tk.xhuoffice.sessbilinfo.net;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import tk.xhuoffice.sessbilinfo.util.Logger;


public class ProxySetting {
    
    public static boolean useSys = true;
    public static boolean useProxy = false;
    
    public static Proxy getSystemProxy() {
        try {
            ProxySelector proxySelector = ProxySelector.getDefault();
            List<Proxy> selectedProxies = proxySelector.select(new URI("http://www.example.com"));
            if(selectedProxies.isEmpty()) {
                return Proxy.NO_PROXY;
            } else {
                return selectedProxies.get(0);
            }
        } catch(java.net.URISyntaxException e) {
            return Proxy.NO_PROXY;
        }
    }
    
    public static Proxy getConfigProxy() {
        // get properties
        String styp = Setting.read("proxy.type", "http").toLowerCase();
        String host = Setting.read("proxy.host", "127.0.0.1");
        int port = 10809; {
            try {
                port = Integer.parseInt(Setting.read("proxy.port", "10809"));
            } catch(NumberFormatException e) {
                Setting.set("proxy.port", "10809");
            }
        }
        // proxy type
        Proxy.Type ptyp = null; {
            if(styp.equals("http")) {
                ptyp = Proxy.Type.HTTP;
            } else if(styp.equals("socks")||styp.equals("socks4")||styp.equals("socks5")) {
                ptyp = Proxy.Type.SOCKS;
            } else {
                Logger.warnln("不支持的代理类型 "+styp+", 使用系统代理");
                return Proxy.NO_PROXY;
            }
        }
        // proxy address
        InetSocketAddress address = new InetSocketAddress(host,port);
        // return Proxy
        return new Proxy(ptyp,address);
    }
    
    public static Proxy getProxy() {
        if(useSys) {
            return getSystemProxy();
        } else {
            Proxy config = getConfigProxy();
            if(config.equals(Proxy.NO_PROXY)) {
                return getSystemProxy();
            } else {
                return config;
            }
        }
    }
    
    public static Proxy proxy = null;
    
    public static Proxy get() {
        if(proxy==null) {
			proxy = getProxy();
        }
        return proxy;
    }
    
    private static final String username = Setting.read("proxy.username", "");
    private static final String password = Setting.read("proxy.password", "");
    
    public static HttpURLConnection setHttpProxyAuth(HttpURLConnection conn) {
        if(username.isEmpty()||password.isEmpty()) {
            // nothing here...
        } else if(proxy.type().equals(Proxy.Type.HTTP)) {
            String val = "Basic " + StringCoder.base64Encode(username+":"+password);
            conn.setRequestProperty("Proxy-Authorization", val);
        }
        return conn;
    }
    
}
