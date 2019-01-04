import DownloadPictures.UrlFileDownloadUtil;
import DownloadPictures.getUrl;
import Processor.*;
import utils.mysqlUtils;
import webmagic.bean.URL1;
import webmagic.bean.pipeline.*;

import java.util.ArrayList;
import java.util.List;
public class SpidersRun {
    public static void main(String[] args){
        MyProcessorSQL myProcessorSQL =new MyProcessorSQL();
        List<Object> courses=myProcessorSQL.getCourses();
        for(Object course:courses) {
            ZhiHuProcessor1.zhihuCrawel(course);
            CSDNProcessor1.CSDNCrawel(course);
            BaiDuZhiDaoProcessor1.BaiduZhiDaoCrawel(course);
            ToutiaoProcessor1.TouTiaoCrawel(course);
            //ZhiHuProcessor.zhihuCrawel(course);
            //CSDNProfessor.CSDNCrawel(course);
            //BaiDuZhiDaoProcessor.BaiduZhiDaoCrawel(course);
           // TouTiaoProcessor.TouTiaoCrawel(course);
            //JianShuProcesser.JianShuCrawel(course);
        }
        //DistinctDao.QuChong();
      //  List<URL1> url1s=new ArrayList<URL1>();
       // url1s = getUrl.getUrl();
       // UrlFileDownloadUtil.downloadPictures(url1s);
    }
}
