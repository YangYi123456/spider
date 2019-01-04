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
//实现去重

public class DistinctDao {
    public static  void QuChong(){
        mysqlUtils mysql = new mysqlUtils();
        List<Object> params=new ArrayList<Object>();
        List<Map<String,Object>> results=new ArrayList<Map<String, Object>>();
        String sql="select * from assemble_copy where assemble_id in (select min(assemble_id) from assemble_copy group by assemble_text)";
        try{
             results=mysql.returnMultipleResult(sql,params);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mysql.closeconnection();
        }


        String addSql =   "insert into  assemble"
                + "(assemble_content, assemble_scratch_time,assemble_text,facet_id,source_id,type,domain_id) values (?,?,?,?,?,?,?)";
        for(Map<String,Object>result:results) {
            mysqlUtils mysql1 = new mysqlUtils();
            // 定义插入语句参数
            List<Object> params1=new ArrayList<Object>();
            // params1.add(result.get("assemble_id"));
            params1.add(result.get("assemble_content"));
            params1.add(result.get("assemble_scratch_time"));
            params1.add(result.get("assemble_text"));
            params1.add(result.get("facet_id"));
            params1.add(result.get("source_id"));
            params1.add(result.get("type"));
            params1.add(result.get("domain_id"));
            try {
                mysql1.addDeleteModify(addSql, params1);
            } catch (SQLException exception) {
                System.out.println(exception.getMessage());
            } finally {
                mysql1.closeconnection();
            }
        }
    }
}

