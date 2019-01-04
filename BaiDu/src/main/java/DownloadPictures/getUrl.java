package DownloadPictures;

import app.config;
import utils.mysqlUtils;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Observable;
import java.lang.Object;
import webmagic.bean.URL1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;
//从数据库中读出数据
//解析url
public class getUrl {
    public static final String REGEX="img\\ssrc=\"https:(.*?\\.(jpg|png))\"";
    public static    List<URL1> urlList=new ArrayList<URL1>();
    public static List<Map<String,Object>> getInfo() {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select assemble_id,assemble_content,domain_id from assemble";
        List<Object> params = new ArrayList<Object>();
        try {
            results = mysql.returnMultipleResult(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return results;
    }
    public static List<URL1> getUrl(){
        List<Map<String, Object>>  results=getInfo();
        //result里面是碎片名和抓取的碎片内容
        for(Map<String,Object> result:results){
            int i=1;
            Pattern p=Pattern.compile(REGEX);
            Matcher m=p.matcher(result.get("assemble_content").toString());
            while(m.find()){
                String img="https:"+m.group(1);
                URL1 urls=new URL1();//这个必须要放在循环里面，否则后面的数据会覆盖前面的数据
                urls.url(img);
                Object id=result.get("assemble_id");
                urls.assemble_id(id);
                Object id1=result.get("domain_id");
                urls.domain_id(id1);
                urlList.add(urls);
                i++;
            }
        }
        return urlList;
    }
}
