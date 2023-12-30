package tk.xhuoffice.sessbilinfo.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Downloader {
    
    private File file = null;
    private String path = null;
    private String fname = null;
    private HttpURLConnection conn = null;
    private OutputStream out = null;

    private String status = "init";
    
    private String contentType = null;
    
    public Downloader(String url, String fileDirPath) {
        // file & path
        if(!fileDirPath.endsWith("/")) {
            fileDirPath += "/";
        }
        URL url0 = null;
        try {
            url0 = new URL(url);
        } catch(java.net.MalformedURLException e) {
            throw new HttpConnectException("非法的 URL: "+e.getMessage());
        }
        this.fname = Paths.get(url0.getPath()).getFileName().toString();;
        this.path = fileDirPath + this.fname;
        this.file = new File(this.path);
        try {
            try {
                CookieFile.checkParentDir(this.path);
            } catch(IOException e) {}
            this.out = new FileOutputStream(this.file);
        } catch(java.io.FileNotFoundException e) {
            Logger.errln("无法打开文件: "+e.getMessage());
            return;
        }
        // load cookie
        Http.loadCookieCache();
        // create connection
        this.conn = Http.setGetConnURL(url0);
        // return
        this.status = "ready";
        return;
    }
    
    public Downloader(String url, String fileDirPath, String fileName) {
        // file & path
        if(!fileDirPath.endsWith("/")) {
            fileDirPath += "/";
        }
        this.fname = fileName.replace('/','／')
                             .replace('\\','＼')
                             .replace('"','\'')
                             .replace(':','：')
                             .replace('*','＊')
                             .replace('<','＜')
                             .replace('>','＞')
                             .replace('|','｜');
        this.path = fileDirPath + this.fname;
        this.file = new File(this.path);
        try {
            try {
                CookieFile.checkParentDir(this.path);
            } catch(IOException e) {}
            this.out = new FileOutputStream(this.file);
        } catch(java.io.FileNotFoundException e) {
            Logger.errln("无法打开文件: "+e.getMessage());
            return;
        }
        // load cookie
        Http.loadCookieCache();
        // create connection
        this.conn = Http.setGetConnURL(url);
        // return
        this.status = "ready";
        return;
    }

    private long length = -1;
    public long getLength() {
        return this.length;
    }

    private long progress = 0;
    public long getProgress() {
        return this.progress;
    }
    
    /**
     * Download file.
     * @return Downloaded             file
     * @throws IllegalStateException  When file has already been downloaded or file could not be created
     */
    public File download() {
        // check if can download
        if(this.out==null||this.status.equals("init")) {
            throw new IllegalStateException( "File "+this.fname+" could not be created");
        }
        if(this.conn==null||this.status.equals("finished")) {
            throw new IllegalStateException( "File "+this.fname+" has already been downloaded");
        }
        // connect
        this.status = "downloading";
        try {
            conn.connect();
        } catch(IOException e) {
            Logger.errln("连接至 "+(this.conn.getURL().toString())+" 时发生异常");
            OutFormat.outThrowable(e,3);
            this.status = "failed";
            return this.file;
        }
        // file length
        this.length = this.conn.getContentLengthLong();
        Logger.println("File length: " + length);
        // download
        InputStream in = null;
        try {
            in = new BufferedInputStream(conn.getInputStream());
            // content type
            this.contentType = HttpURLConnection.guessContentTypeFromStream(in);
            if(this.contentType==null) {
                this.contentType = conn.getContentType();
            }
            Logger.println("File type: "+this.contentType);
            // download
            int bufferSize = 0;
            byte[] buffer = new byte[1024];
            this.progressReporter.start();
            while((bufferSize=in.read(buffer,0,1024))!=-1) {
                this.out.write(buffer,0,bufferSize);
                this.progress+=bufferSize;
            }
            try {
                this.progressReporter.join();
            } catch(InterruptedException e) {}
            Logger.println("文件 "+this.fname+" 下载完毕");
            this.status = "finished";
        } catch(IOException e) {
            Logger.errln("从 "+(this.conn.getURL().toString())+" 下载时发生异常");
            OutFormat.outThrowable(e,3);
            this.status = "failed";
        } finally {
            try {
                this.out.close();
            } catch(IOException e) {
                Logger.warnln("关闭文件时发生异常: "+e.getMessage());
                OutFormat.outThrowable(e,0);
            }
            try {
                if(in!=null) {
                    in.close();
                }
            } catch(IOException e) {
                Logger.warnln("关闭输入流时发生异常: "+e.getMessage());
                OutFormat.outThrowable(e,0);
            }
            this.conn = null;
        }
        return this.file;
    }

    private Thread progressReporter = new Thread(() -> {
        while(!(this.progress>=this.length||this.conn==null)) {
            Logger.footln(String.format("Download progress: %d/%d (%d%s)", this.progress, this.length, this.progress*100L/this.length, "%"));
            try {
                Thread.sleep(1000L);
            } catch(InterruptedException e) {}
        }
        Logger.clearFootln();
    },  "DownloadProgressReporter-"+this.fname);
    
    @Override
    public String toString() {
        return "Downloader-"+this.fname;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        long result = 1454;
        result = prime * result + this.path.hashCode();
        result = prime * result + this.fname.hashCode();
        result = prime * result + this.file.hashCode();
        if(this.conn!=null) {
            result = prime * result + this.conn.hashCode();
        }
        if(this.out!=null) {
            result = prime * result + this.out.hashCode();
        }
        if(this.contentType!=null) {
            result = prime * result + this.contentType.hashCode();
        }
        result = prime * result + this.toString().hashCode();
        result = prime * result + this.length;
        result = prime * result + this.progress;
        result = prime * result + this.status.hashCode();
        return (int)result;
    }
    
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
     */
    public static void main(String[] args) {
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
