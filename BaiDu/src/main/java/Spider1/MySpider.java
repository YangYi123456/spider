package Spider1;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import java.util.List;
public class MySpider extends Spider {
    public MySpider(PageProcessor pageProcessor){
        super(pageProcessor);
    }
    public static MySpider create(PageProcessor pageProcessor){
        return new MySpider(pageProcessor);
    }
    public MySpider addRequests(List<Request> requests){
        for(Request request:requests){
            this.addRequest(request);
        }
        return this;
    }
}
