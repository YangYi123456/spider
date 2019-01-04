package Processor;
import Scheduler.SpikeFileCacheQueueScheduler;
import Spider1.MySpider;
import app.config;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import webmagic.bean.FragmentContent;
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
import webmagic.bean.URL1;
import webmagic.bean.pipeline.DistinctDao;

import DownloadPictures.*;
public class ZhiHuProcessor implements PageProcessor {
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
        //爬取碎片
        //关于知乎
        //首页没有爬取的必要
        //专栏：一篇文章，静态爬取即可
        //问题：动态爬取，爬取下面的答案
        List<String> fragments=new ArrayList<String>();
        List<String>fragmentsPureText=new ArrayList<String>();
        if(page.getUrl().regex("https://www\\.zhihu\\.com/search.*").match())
        {
            page.setSkip(true);
            //不爬取，因为都是部分信息
            //fragments = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText ztext CopyrightRichText-richText']").all();
            //fragmentsPureText = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText ztext CopyrightRichText-richText']/tidyText()").all();
        }
        //爬取专栏
        else if(page.getUrl().regex("https://zhuanlan\\.zhihu\\.com/p/\\d+").match()){
             fragments = page.getHtml().xpath("div[@class='RichText ztext Post-RichText']").all();
             fragmentsPureText = page.getHtml().xpath("div[@class='RichText ztext Post-RichText']/tidyText()").all();
        }
        //爬取问题下面的答案
        else if(page.getUrl().regex("https://www\\.zhihu\\.com/question/\\d+").match()){
            fragments = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText ztext CopyrightRichText-richText']").all();
            fragmentsPureText = page.getHtml().xpath("div[@class='RichContent-inner']/span[@class='RichText ztext CopyrightRichText-richText']/tidyText()").all();
        }
        FragmentContent fragmentContent = new FragmentContent(fragments, fragmentsPureText);
        page.putField("fragmentContent", fragmentContent);

        List<String> urls;
        urls = page.getHtml().xpath("div[@class='ContentItem ArticleItem']/h2[@class='ContentItem-title']/a/@href").all();
        for (String url : urls) {
            Request request = new Request();
            String REGEX="(//zhuanlan.*)";
            Pattern p=Pattern.compile(REGEX);
            Matcher m=p.matcher(url);
            if(m.find())
            {
                request.setUrl("https:" + m.group(0));
             //   System.out.println("专栏网址：");
              //  System.out.println(request.getUrl());
            }
            request.setExtras(page.getRequest().getExtras());
            page.addTargetRequest(request);
        }
        List<String>urls1;
        urls1=page.getHtml().xpath("div[@class='ContentItem AnswerItem']/h2[@class='ContentItem-title']//a/@href").all();
        for(String url:urls1) {
            Request request = new Request();
            url = url.substring(0, 18);
           // System.out.println("问答网址：");
            request.setUrl("https://www.zhihu.com" + url);
            request.setExtras(page.getRequest().getExtras());
           // System.out.println(request.getUrl());
            page.addTargetRequest(request);
        }
    }

    public static void zhihuCrawel(Object courseName){
        System.setProperty("selenuim_config", "C:\\IDEA\\config.ini");
        MyProcessorSQL myProcessorSQL=new MyProcessorSQL();
        List<Map<String,Object>> allFacetsInformation=myProcessorSQL.getAllFacets(courseName);
        List<Request> requests=new ArrayList<Request>();

        for(Map<String,Object> facetInformation:allFacetsInformation){
            Request request = new Request();
            String facet_name=myProcessorSQL.getFacetName(facetInformation.get("facet_id"));
            String url="https://www.zhihu.com/search?type=content&q="

                    +"数据结构"
                    +"树状数组"
                    +"摘要";
            /*
                    +facetInformation.get("domain_name")+" "
                    +facetInformation.get("topic_name")+" "
                    //+facetInformation.get("facet_name");
                    +facet_name;*/
            facetInformation.put("source_id","2");
           // System.out.println(url);
           // System.out.println();
            requests.add(request.setUrl(url).setExtras(facetInformation));
        }
        SpikeFileCacheQueueScheduler file=new SpikeFileCacheQueueScheduler("D:\\urlFile");
        file.setRegx("https://www\\.zhihu\\.com/search.*");
            MySpider.create(new ZhiHuProcessor())
                    .addRequests(requests)
                    .setScheduler(new QueueScheduler().setDuplicateRemover(new HashSetDuplicateRemover()))
                    //.setScheduler(file)
                   // .setScheduler(new FileCacheQueueScheduler("D:\\path"))
                    .setDownloader(new SeleniumDownloader("C:\\IDEA\\chromedriver.exe"))
                    .thread(config.THREAD)
                    .addPipeline(new SqlPipeline())
                    .addPipeline(new ConsolePipeline())
                    .run();
        }
    }