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
public class BaiDuZhiDaoProcessor1 implements PageProcessor{
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
        if(page.getUrl().regex("https://zhidao\\.baidu\\.com/search.*").match()){
            List<String> urls;
            urls = page.getHtml().xpath("dl[@class='dl']//a[@class='ti']/@href").all();
            //此处应该添加请求的附加信息，extras
            for (String url : urls) {
                Request request = new Request();
                request.setUrl(url);
                System.out.println(request.getUrl());
                request.setExtras(page.getRequest().getExtras());
                page.addTargetRequest(request);
            }
        }
        else {
            fragments=page.getHtml().xpath("div[@class='best-text mb-10']").all();
            fragmentsPureText=page.getHtml().xpath("div[@class='best-text mb-10']/tidyText()").all();
            if(fragments!=null&&fragmentsPureText!=null)
            {
                FragmentContent fragmentContent=new FragmentContent(fragments,fragmentsPureText);
                page.putField("fragmentContent",fragmentContent);
            }
        }
    }
    public static void BaiduZhiDaoCrawel(Object courseName){
        System.setProperty("selenuim_config", "C:\\IDEA\\config.ini");
        MyProcessorSQL myProcessorSQL=new MyProcessorSQL();
        List<Map<String,Object>> allFacetsInformation=myProcessorSQL.getAllFacets(courseName);
        List<Request> requests=new ArrayList<Request>();
        Map<String,Object> facetInformation=allFacetsInformation.get(0);
            for(int i=0;i<30;i=i+10)
            {
                Request request = new Request();
                String facet_name = myProcessorSQL.getFacetName(facetInformation.get("facet_id"));
                String url = "https://zhidao.baidu.com/search?word="
                        +"数据结构"
                        +"树状数组"
                        +"摘要"
                        +"&ie=gbk&site=-1&sites=0&date=0&pn="
                        +i;
                    /*
                    + facetInformation.get("domain_name") + " "
                    + facetInformation.get("topic_name") + " "
                    //+facetInformation.get("facet_name");
                    + facet_name;*/
                //添加链接，设置额外信息
                facetInformation.put("source_id", "3");
                requests.add(request.setUrl(url).setExtras(facetInformation));
            }

        MySpider.create(new BaiDuZhiDaoProcessor1())
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