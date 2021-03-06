package Processor;
import DownloadPictures.UrlFileDownloadUtil;
import DownloadPictures.getUrl;
import Spider1.MySpider;
import app.config;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import webmagic.bean.FragmentContent;
import webmagic.bean.URL1;
import webmagic.bean.pipeline.*;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.*;
public class CSDNProcessor1 implements PageProcessor{
    private Site site = Site.me()
            .setRetryTimes(config.retryTimes)
            .setRetrySleepTime(config.retrySleepTime)
            .setSleepTime(config.sleepTime)
            .setTimeOut(config.timeOut)
            .addHeader("User-Agent", config.userAgent)
            .addHeader("Accept", "*/*");
    public Site getSite() {
        return site;
    }

    public void process(Page page){
        List<String> fragments=new ArrayList<String>();
        List<String>fragmentsPureText=new ArrayList<String>();
        if(page.getUrl().regex("https://so.*").match())
        {
            List<String> urls;
            urls=page.getHtml().xpath("dl[@class='search-list J_search']/dt/div[@class='limit_width']/a/@href").all();
            String url_before = new String();
            for(String url:urls){
                Request request = new Request();
                String REGEX="https://blog\\.csdn\\.net/.*/article/details/\\d+";
                Pattern p=Pattern.compile(REGEX);
                Matcher m=p.matcher(url);
                if(m.find()&& (!url.equals(url_before))) {
                    request.setUrl(url);
                    System.out.println(url);
                    request.setExtras(page.getRequest().getExtras());
                    page.addTargetRequest(request);
                }
                url_before = url;
            }
        }
        else {
            fragments = page.getHtml().xpath("div[@id='article_content']").all();
            fragmentsPureText = page.getHtml().xpath("div[@id='article_content']/tidyText()").all();
            FragmentContent fragmentContent = new FragmentContent(fragments, fragmentsPureText);
            page.putField("fargmentContent", fragmentContent);
        }
    }
    public static void CSDNCrawel(Object courseName){
        System.setProperty("selenuim_config", "C:\\IDEA\\config.ini");
        MyProcessorSQL myProcessorSQL=new MyProcessorSQL();
        List<Map<String,Object>> allFacetsInformation=myProcessorSQL.getAllFacets(courseName);
        List<Request> requests=new ArrayList<Request>();
        Map<String,Object> facetInformation=allFacetsInformation.get(0);
            for(int i=1;i<4;i++)
            {
                Request request = new Request();
                String facet_name = myProcessorSQL.getFacetName(facetInformation.get("facet_id"));
                String url = "https://so.csdn.net/so/search/s.do?p="+i+"&q="
                        +"数据结构"
                        +"树状数组"
                        +"摘要"
                        +"&t=&domain=&o=&s=&u=&l=&f=";

                //添加链接，设置额外信息
                facetInformation.put("source_id", "4");
                requests.add(request.setUrl(url).setExtras(facetInformation));
                // System.out.println(request);
            }
        MySpider.create(new CSDNProcessor1())
                .addRequests(requests)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
                //.setScheduler(new FileCacheQueueScheduler("D:\\urlFile"))
                .setDownloader(new SeleniumDownloader("C:\\IDEA\\chromedriver.exe"))
                .thread(3)
                .addPipeline(new SqlPipeline())
                .addPipeline(new ConsolePipeline())
                .run();
    }
}