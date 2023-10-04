package tk.xhuoffice.sessbilinfo.net;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * This class can encode or decode String with Base64 or URL.
 */

public class StringCoder {
    
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    
    // URL Encode
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str,"UTF-8"); // Java 8 doesn't support Chatset in URLEncoder
        } catch(java.io.UnsupportedEncodingException e) {
            return null;
        }
    }
    
    // URL Decode
    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str,"UTF-8"); // Java 8 doesn't support Chatset in URLDecoder either
        } catch(java.io.UnsupportedEncodingException e) {
            return null;
        }
    }
    
    public static final Base64.Encoder base64encoder = Base64.getEncoder();
    public static final Base64.Decoder base64decoder = Base64.getDecoder();
    
    // Base64 Encode
    public static String base64Encode(String str) {
        return new String(base64encoder.encode(str.getBytes(UTF_8)),UTF_8);
    }
    
    /**
     * Base64 Decode
     *
     * Input String should not contain '\r' '\n'.
     */
    public static String base64Decode(String str) {
        return new String(base64decoder.decode(str.getBytes(UTF_8)),UTF_8);
    }
    
}
