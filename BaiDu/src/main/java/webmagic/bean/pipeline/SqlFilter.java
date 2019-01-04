package webmagic.bean.pipeline;
import app.config;
import webmagic.bean.FragmentContent;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import utils.mysqlUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 数据库过滤
 * 删除文本很短的记录，MINLENGTH=200；
 */
public class SqlFilter {
    public void DeleteMin(){
        mysqlUtils mysql=new mysqlUtils();
        List<Object> params=new ArrayList<Object>();
        List<Map<String,Object>> results=new ArrayList<Map<String, Object>>();
        String sql="delete from assemble_copy where length(assemble.text)<200";
        try{
            results=mysql.returnMultipleResult(sql,params);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mysql.closeconnection();
        }
    }
}
