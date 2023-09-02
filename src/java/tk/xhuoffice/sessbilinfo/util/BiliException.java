package tk.xhuoffice.sessbilinfo.util;

public class BiliException extends RuntimeException {
    
    // 异常信息
    private String message;
    
    public BiliException() {}
    
    public BiliException(String message) {
        super(message);
        this.message = message;
    }
    
}
