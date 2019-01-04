import Processor.BaiDuZhiDaoProcessor1;
import Processor.CSDNProcessor1;
import Processor.ToutiaoProcessor1;
import Processor.ZhiHuProcessor1;
import webmagic.bean.pipeline.MyProcessorSQL;

import java.util.List;
import DownloadPictures.UrlFileDownloadUtil;
import DownloadPictures.getUrl;
import Processor.*;
import utils.mysqlUtils;
import webmagic.bean.URL1;
import webmagic.bean.pipeline.*;

import java.util.ArrayList;
import java.util.List;
public class SpidersRun1 {
        public static void main(String[] args){
            MyProcessorSQL myProcessorSQL =new MyProcessorSQL();
            List<Object> courses=myProcessorSQL.getCourses();
            Object course=courses.get(0);
            ZhiHuProcessor1.zhihuCrawel(course);
            CSDNProcessor1.CSDNCrawel(course);
            BaiDuZhiDaoProcessor1.BaiduZhiDaoCrawel(course);
            ToutiaoProcessor1.TouTiaoCrawel(course);
            DistinctDao.QuChong();
            List<URL1> url1s=new ArrayList<URL1>();
            url1s = getUrl.getUrl();
            UrlFileDownloadUtil.downloadPictures(url1s);
    }

}
