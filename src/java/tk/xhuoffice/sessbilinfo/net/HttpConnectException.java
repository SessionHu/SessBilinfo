package tk.xhuoffice.sessbilinfo.net;


public class HttpConnectException extends RuntimeException {
    
    private String message;
    private Throwable cause;
    
    public HttpConnectException() {}
    
    public HttpConnectException(String message) {
        super(message);
        this.message = message;
    }
    
    public HttpConnectException(String message, Throwable cause) {
        super(message,cause);
        this.message = message;
        this.cause = cause;
    }
    
}
