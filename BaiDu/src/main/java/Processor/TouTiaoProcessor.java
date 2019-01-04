package Processor;
import DownloadPictures.UrlFileDownloadUtil;
import DownloadPictures.getUrl;
import Spider1.MySpider;
import app.config;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.selector.JsonPathSelector;
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
public class TouTiaoProcessor implements PageProcessor {
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
    public void process(Page page) {
        List<String> fragments=new ArrayList<String>();
        List<String>fragmentsPureText=new ArrayList<String>();
        if(page.getUrl().regex("https://www\\.toutiao\\.com/search.*").match()){
            List<String> urls;
            String jsonstr=page.getHtml().xpath("//body/pre/text()").get();
            urls=new JsonPathSelector("$.data[*].open_url").selectList(jsonstr);
            //此处应该添加请求的附加信息，extras
            for (String url : urls) {
                Request request = new Request();
                request.setUrl("https://www.toutiao.com"+url);
                System.out.println(request.getUrl());
                request.setExtras(page.getRequest().getExtras());
                page.addTargetRequest(request);
            }
        }
        else{
            fragments=page.getHtml().xpath("div[@class='article-content']").all();
            fragmentsPureText=page.getHtml().xpath("div[@class='article-content']/tidyText()").all();
            FragmentContent fragmentContent=new FragmentContent(fragments,fragmentsPureText);
            page.putField("fragmentContent",fragmentContent);
        }
    }
    public static void TouTiaoCrawel(Object courseName){
        System.setProperty("selenuim_config", "C:\\IDEA\\config.ini");
        MyProcessorSQL myProcessorSQL=new MyProcessorSQL();
        List<Map<String,Object>> allFacetsInformation=myProcessorSQL.getAllFacets(courseName);
        List<Request> requests=new ArrayList<Request>();
        for(Map<String,Object> facetInformation:allFacetsInformation) {
            Request request = new Request();
           String facet_name = myProcessorSQL.getFacetName(facetInformation.get("facet_id"));
            String url = "https://www.toutiao.com/search_content/?offset=0&format=json&keyword="
                    +"数据结构"
                    +"树状数组"
                    +"摘要"
                    /*
                    + facetInformation.get("domain_name")
                    + facetInformation.get("topic_name")
                    //+facetInformation.get("facet_name");
                    + facet_name*/
                    +"&autoload=true&count=20&cur_tab=1&from=search_tab&pd=synthesis";
            //添加链接，设置额外信息
            facetInformation.put("source_id", "9");
            requests.add(request.setUrl(url).setExtras(facetInformation));
        }

        MySpider.create(new TouTiaoProcessor())
                .addRequests(requests)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
               // .setScheduler(new FileCacheQueueScheduler("D:\\urlFile"))
                .setDownloader(new SeleniumDownloader("C:\\IDEA\\chromedriver.exe"))
                .thread(config.THREAD)
                .addPipeline(new SqlPipeline())
                .addPipeline(new ConsolePipeline())
                .run();
    }
}
