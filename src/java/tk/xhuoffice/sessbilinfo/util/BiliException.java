package tk.xhuoffice.sessbilinfo.util;


public class BiliException extends RuntimeException {
    
    // 异常信息
    private String message;
    
    private Throwable cause;
    
    public BiliException() {}
    
    public BiliException(String message) {
        super(message);
        this.message = message;
        this.detailMessage = message;
    }
    
    public BiliException(String message, Throwable cause) {
        super(message,cause);
        this.message = message;
        this.detailMessage = message;
        this.cause = cause;
    }

    public BiliException(String message, String detailMessage) {
        super(message);
        this.message = message;
        this.detailMessage = detailMessage;
    }

    private String detailMessage;

    public String getDetailMessage() {
        return this.detailMessage;
    }
    
}
