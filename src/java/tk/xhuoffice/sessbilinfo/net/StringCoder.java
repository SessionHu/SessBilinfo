package tk.xhuoffice.sessbilinfo.net;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Encode or decode String in Base64 or URL.
 */

public class StringCoder {
    
    // DO NOT <init>!
    private StringCoder() {}

    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    
    /**
     * URL Encode.
     * @param str Input URL
     * @return    Encoded URL
     */
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str,"UTF-8"); // Java 8 doesn't support Chatset in URLEncoder
        } catch(java.io.UnsupportedEncodingException e) {
            return null;
        }
    }
    
    /**
     * URL Decode.
     * @param str Input URL
     * @return    Decoded URL
     */
    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str,"UTF-8"); // Java 8 doesn't support Chatset in URLDecoder either
        } catch(java.io.UnsupportedEncodingException e) {
            return null;
        }
    }
    
    private static final Base64.Encoder BASE64ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64DECODER = Base64.getDecoder();
    
    /**
     * Base64 Encode.
     * @param str Input text
     * @return    Base64
     */
    public static String base64Encode(String str) {
        return new String(BASE64ENCODER.encode(str.getBytes(UTF_8)),UTF_8);
    }
    
    /**
     * Base64 Decode.
     * @param str Input Base64
     * @return    Text
     */
    public static String base64Decode(String str) {
        return new String(BASE64DECODER.decode(str.getBytes(UTF_8)),UTF_8);
    }
    
}
