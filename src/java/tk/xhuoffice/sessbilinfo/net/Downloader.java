package tk.xhuoffice.sessbilinfo.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import tk.xhuoffice.sessbilinfo.util.Logger;
import tk.xhuoffice.sessbilinfo.util.OutFormat;


public class Downloader {
    
    private File file = null;
    private String path = null;
    private String fname = null;
    private HttpURLConnection conn = null;
    private OutputStream out = null;
    
    private String contentType = null;
    
    public Downloader(String url, String fileDir) {
        // file & path
        if(!fileDir.endsWith("/")) {
            fileDir += "/";
        }
        int endIndex = url.lastIndexOf("?"); {
            if(endIndex<1) {
                endIndex = url.length();
            }
        }
        this.fname = url.substring(url.lastIndexOf("/")+1, endIndex);
        this.path = fileDir + this.fname;
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
    }
    
    public Downloader(String url, String fileDirPath, String fileName) {
        // file & path
        if(!fileDirPath.endsWith("/")) {
            fileDirPath += "/";
        }
        this.fname = fileName;
        this.path = fileDirPath + fileName;
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
    }
    
    /**
     * Download file.
     * @return Downloaded             file
     * @throws IllegalStateException  When file has already been downloaded or file could not be created
     */
    public File download() {
        // check if can download
        if(this.out==null) {
            throw new IllegalStateException( "File "+this.fname+" could not be created");
        }
        if(this.conn==null) {
            throw new IllegalStateException( "File "+this.fname+" has already been downloaded");
        }
        // connect
        try {
            conn.connect();
        } catch(IOException e) {
            Logger.errln("连接至 "+OutFormat.shorterString(this.conn.getURL().toString())+" 时发生异常");
            OutFormat.outThrowable(e,3);
            return this.file;
        }
        // file length
        long length = this.conn.getContentLengthLong();
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
            long progress = 0;
            byte[] buffer = new byte[1024];
            while((bufferSize=in.read(buffer,0,1024))!=-1) {
                this.out.write(buffer,0,bufferSize);
                progressReport((progress+=bufferSize),length);
            }
            Logger.clearFootln();
            Logger.println("文件 "+this.fname+" 下载完毕");
        } catch(IOException e) {
            Logger.errln("从 "+OutFormat.shorterString(this.conn.getURL().toString())+" 下载时发生异常");
            OutFormat.outThrowable(e,3);
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
        }
        this.conn = null;
        return this.file;
    }
    
    private long lastProgressReport = 0L;
    
    private void progressReport(long progress, long length) {
        if(((System.currentTimeMillis()/1000L)-this.lastProgressReport)>=1L) {
            Logger.footln(String.format("Download progress: %d/%d", progress, length));
            lastProgressReport = System.currentTimeMillis()/1000L;
        }
    }
    
    public static void main(String[] args) {
        String url = "http://cachefly.cachefly.net/50mb.test";
        String dir = System.getProperty("user.home")+"/downloads/";
        Logger.println("tk.xhuoffice.sessbilinfo.net.Downloader 下载演示");
        Logger.println("-------------------------------------------------");
        Logger.println("下载地址 "+url);
        Logger.println("目标目录 "+dir);
        Downloader dl = new Downloader(url,dir);
        dl.download();
    }
    
}
