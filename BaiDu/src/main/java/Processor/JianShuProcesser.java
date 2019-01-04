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
public class JianShuProcesser implements PageProcessor {
    private Site site = Site.me()
            .setRetryTimes(config.retryTimesSO)
            .setRetrySleepTime(config.retrySleepTimeSO)
            .setSleepTime(config.sleepTimeSO)
            .setTimeOut(config.timeOutSO)
            .addHeader("User-Agent", config.userAgent)
            .addHeader("Accept", "*/*");

    public Site getSite() {
        return site;
    }

    public void process(Page page) {
        List<String> fragments = new ArrayList<String>();
        List<String> fragmentsPureText = new ArrayList<String>();
        if (page.getUrl().regex("https://www\\.jianshu\\.com/search.*").match()) {
            System.out.println(page.getHtml());
            List<String> urls;
            urls = page.getHtml().xpath("//ul[@class='note-list']/li/div[@class='content']//a/@href").all();
            //  List<String>  tmpUrls = page.getHtml().xpath("//div[@class='search-content']").all();
            // System.out.println(tmpUrls);
            //此处应该添加请求的附加信息，extras
           String url_before = new String();
            for (String url : urls) {
                if(!url.equals(url_before)&&!url.equals(url_before+"#comments")){
                    Request request = new Request();
                    request.setUrl("https://www.jianshu.com" + url);
                    System.out.println(request);
                    request.setExtras(page.getRequest().getExtras());
                }
                url_before = url;
            }
        } else if(page.getUrl().regex("https://www\\.jianshu\\.com/p.*").match()){
            fragments = page.getHtml().xpath("div[@class='show-content']").all();
            fragmentsPureText = page.getHtml().xpath("div[@class='show-content']/tidyText()").all();
            FragmentContent fragmentContent = new FragmentContent(fragments, fragmentsPureText);
            page.putField("fragmentContent", fragmentContent);
        }
    }

    public static void JianShuCrawel(Object courseName) {
        System.setProperty("selenuim_config", "C:\\IDEA\\config.ini");
        MyProcessorSQL myProcessorSQL = new MyProcessorSQL();
        List<Map<String, Object>> allFacetsInformation = myProcessorSQL.getAllFacets(courseName);
        List<Request> requests = new ArrayList<Request>();
        for (Map<String, Object> facetInformation : allFacetsInformation) {
            for(int i=1;i<4;i++){
                Request request = new Request();
                String facet_name = myProcessorSQL.getFacetName(facetInformation.get("facet_id"));
                String url = "https://www.jianshu.com/search?q="
                        +"数据结构"
                        +"树状数组"
                        +"摘要"
                        +"page%3D3&page="
                        +i
                        +"&type=note";
                    /*
                    + facetInformation.get("domain_name") + " "
                    + facetInformation.get("topic_name") + " "
                    //+facetInformation.get("facet_name");
                    + facet_name;*/
                //添加链接，设置额外信息
                facetInformation.put("source_id", "jianshu");
                requests.add(request.setUrl(url).setExtras(facetInformation));
            }
        }
        for (Request request : requests) {
            MySpider.create(new JianShuProcesser())
                    .addRequest(request)
                 //   .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
                 //   .setScheduler(new FileCacheQueueScheduler("D:\\urlFile"))
                    .setDownloader(new SeleniumDownloader("C:\\IDEA\\chromedriver.exe"))
                    .thread(config.threadSO)
                    .addPipeline(new SqlPipeline())
                    .addPipeline(new ConsolePipeline())
                    .run();
        }
    }
}
