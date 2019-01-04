package DownloadPictures;

import java.io.File;
import java.util.List;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.net.URL;
import java.net.MalformedURLException;
import webmagic.bean.URL1;
public class UrlFileDownloadUtil {
    /*传入要下载的URL列表，将url所对应的图片下载到本地*/
    public static void downloadPictures(List<URL1> urlList) {
        URL url = null;
        for (int i = 0; i < urlList.size(); i++) {
            String baseDir = "D:\\spider1\\"+urlList.get(i).getDomain_id()+"\\";
            File file1 =new File(baseDir);
            if(!file1.exists()){
                file1.mkdirs();
            }
            try {
                    String[] files = urlList.get(i).getUrl().split("/");
                    String name = urlList.get(i).getAssemble_id() + "__" + files[files.length - 1];
                    url = new URL(urlList.get(i).getUrl());
                    DataInputStream dataInputStream = new DataInputStream(url.openStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(baseDir + name));
                    byte[] buffer = new byte[1024 * 50];
                    int length;
                    while((length=dataInputStream.read(buffer))>0){
                        fileOutputStream.write(buffer,0,length);
                    }
                    System.out.println("已经下载"+baseDir+name);
                    dataInputStream.close();
                    fileOutputStream.close();
                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
}