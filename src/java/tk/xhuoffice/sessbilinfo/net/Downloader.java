package tk.xhuoffice.sessbilinfo.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;

/**
 * A simple HTTP(s) Downloader.
 */

public class Downloader {

    private File file;
    private FileOutputStream out;
    private HttpURLConnection conn;
    
    /**
     * Download file with specified url, file name and path.
     * @param url          string of {@link java.net.URL}
     * @param fileDirPath  directory for downloaded file
     * @param fileName     filename of downloaded file
     * @throws java.io.FileNotFoundException  if the file does not exist but cannot be created, 
     *                                        or cannot be opened for any other reason
     */
    public Downloader(String url, String fileDirPath, String fileName) throws java.io.FileNotFoundException {
        // args
        if(url==null) {
            throw new NullPointerException("url");
        }
        if(fileDirPath==null || fileDirPath.trim().isEmpty()) {
            fileDirPath = System.getProperty("user.home")+"/downloads/";
        }
        if(!fileDirPath.endsWith("/")) {
            fileDirPath += "/";
        }
        // url
        URL url0;
        try {
            url0 = new URI(url).toURL();
        } catch(java.net.URISyntaxException | java.net.MalformedURLException e) {
            throw new HttpConnectException("URL 不合法: "+e.getMessage(),e);
        }
        // file
        if(fileName==null || fileName.trim().isEmpty()) {
            fileName = Paths.get(url0.getPath()).getFileName().toString();
        } else {
            fileName = fileName.replace('/','／')
                               .replace('\\','＼')
                               .replace("\"","\'\'")
                               .replace(':','：')
                               .replace('*','＊')
                               .replace('<','＜')
                               .replace('>','＞')
                               .replace('|','｜');
        }
        this.file = new File(fileDirPath+fileName);
        try {
            CookieFile.checkParentDir(file.getAbsolutePath());
        } catch(IOException e) {
            Logger.warnln("检查下载目录失败: "+e.toString());
        }
        file.delete();
        // outputstream
        this.out = new FileOutputStream(this.file);
        // load cookie
        Http.loadCookieCache();
        // set connection
        this.conn = Http.setGetConnURL(url0);
    }

    /**
     * Download file with specified url, path.
     * @param url          string of {@link java.net.URL}
     * @param fileDirPath  directory for downloaded file
     * @throws java.io.FileNotFoundException  if the file does not exist but cannot be created, 
     *                                        or cannot be opened for any other reason
     */
    public Downloader(String url, String fileDirPath) throws java.io.FileNotFoundException {
        this(url,fileDirPath,null);
    }

    /**
     * Download file with specified url.
     * @param url          string of {@link java.net.URL}
     * @throws java.io.FileNotFoundException  if the file does not exist but cannot be created, 
     *                                        or cannot be opened for any other reason
     */
    public Downloader(String url) throws java.io.FileNotFoundException {
        this(url,null,null);
    }

    private long length;
    private long progress;

    /**
     * Start downloading file.
     * @return Downloaded file.
     * @throws IOException if it occurred while downloading
     */
    public File download() throws IOException {
        // try to connect
        try {
            conn.connect();
        } catch(IOException e) {
            Logger.warnln("连接至 "+(this.conn.getURL().toString())+" 时发生异常");
            OutFormat.outThrowable(e,2);
        }
        // header
        if(Logger.debug) {
            for(String line : Http.getFormattedHeaderFields(conn)) {
                Logger.debugln(line);
            }
        }
        // content-length
        this.length = conn.getContentLengthLong();
        // progress reporter
        this.progressReporter = new Thread(() -> {
            while(!(this.progress>=this.length||this.conn==null)) {
                Logger.footln(String.format("Download progress: %d/%d (%d%s)", this.progress, this.length, this.progress*100L/this.length, "%"));
                try {
                    Thread.sleep(1000L);
                } catch(InterruptedException e) {
                    OutFormat.outThrowable(e,2);
                }
            }
            Logger.clearFootln();
        },  "DownloadProgressReporter-"+this.file.getName());
        // download
        InputStream in = null;
        try {
            in = conn.getInputStream();
            // download
            int bufferSize = 0;
            byte[] buffer = new byte[1024];
            this.progressReporter.start();
            while((bufferSize=in.read(buffer,0,1024))!=-1) {
                this.out.write(buffer,0,bufferSize);
                this.progress += bufferSize;
            }
            try {
                this.progressReporter.join();
            } catch(InterruptedException e) {
                OutFormat.outThrowable(e,0);
            }
            // set date
            this.file.setLastModified(this.conn.getLastModified());
            // ok
            Logger.println("文件 "+this.file.getName()+" 下载完毕");
        } catch(IOException e) {
            throw e;
        } finally {
            // close in
            try {
                if(in!=null) {
                    in.close();
                }
            } catch(IOException e) {
                Logger.warnln("关闭输入流时发生异常: "+e.getMessage());
                OutFormat.outThrowable(e,0);
            }
            in = null;
            // no connection
            this.conn = null;
            // close out
            try {
                if(out!=null) {
                    out.close();
                }
            } catch(IOException e) {
                Logger.warnln("关闭文件时发生异常: "+e.getMessage());
                OutFormat.outThrowable(e,0);
            }
            out = null;
        }
        return this.file;
    }
    private Thread progressReporter;

    /**
     * Returns a string representation of this downloader.
     * @return a string representation of this downloader.
     */
    @Override
    public String toString() {
        return "Downloader@"+this.conn.getURL();
    }
    
    /**
     * Returns a hash code value for this downloader.
     * @return a hash code value for this downloader.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        long result = 1454;
        result = prime * result + this.file.hashCode();
        if(this.conn!=null) {
            result = prime * result + this.conn.hashCode();
        }
        if(this.out!=null) {
            result = prime * result + this.out.hashCode();
        }
        result = prime * result + this.toString().hashCode();
        result = prime * result + this.length;
        result = prime * result + this.progress;
        return (int)result;
    }
    
    /**
     * Indicates whether some other object is "equal to" this one.
     * @return {@code true} if this object is the same as the {@code obj} argument.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj==this) {
            return true;
        }
        if(obj==null) {
            return false;
        }
        if(obj instanceof Downloader) {
            Downloader dlr = (Downloader)obj;
            return dlr.hashCode()==this.hashCode();
        }
        return false;
    }
    
    /**
     * For test.
     * @param args args
     * @throws IOException  from {@link Downloader(String,String)} and {@link download()}
     */
    public static void main(String[] args) throws IOException {
        String url = "http://cachefly.cachefly.net/1mb.test";
        String dir = System.getProperty("user.home")+"/downloads/";
        Logger.println("tk.xhuoffice.sessbilinfo.net.Downloader 下载演示");
        Logger.println("-------------------------------------------------");
        Logger.println("下载地址 "+url);
        Logger.println("目标目录 "+dir);
        Downloader dl = new Downloader(url,dir);
        dl.download();
    }
    
}
