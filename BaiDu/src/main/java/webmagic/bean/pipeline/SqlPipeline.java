package webmagic.bean.pipeline;

import utils.mysqlUtils;
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

    public class SqlPipeline implements Pipeline {
        public void process(ResultItems resultItems, Task task) {
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                FragmentContent fragmentContent = (FragmentContent) entry.getValue();
                List<String> fragments = fragmentContent.getFragments();
                List<String> fragmentsPureText = fragmentContent.getFragmentsPureText();
                for (int i = 0; i < fragments.size(); i++) {
                    mysqlUtils mysql = new mysqlUtils();
                    // 定义插入语句参数
                    String addSql = "insert into  assemble_copy"
                            + "(assemble_content, assemble_scratch_time,assemble_text,facet_id,source_id,type,domain_id) values (?,?,?,?,?,?,?)";
                    // 分面信息
                    Map<String, Object> facetTableMap = resultItems.getRequest().getExtras();
                    // 添加碎片表需要的元组值
                    List<Object> params = new ArrayList<Object>();
                    // params.add(i);
                    params.add(fragments.get(i));
                    String type = "text";
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
                    params.add(date);
                    params.add(fragmentsPureText.get(i));
                    params.add(facetTableMap.get("facet_id"));
                    params.add(facetTableMap.get("source_id"));
                    params.add(type);
                    params.add(facetTableMap.get("domain_id"));
                    try {
                        mysql.addDeleteModify(addSql, params);
                    } catch (SQLException exception) {
                        System.out.println(exception.getMessage());
                    } finally {
                        mysql.closeconnection();
                    }
                }

            }
        }
    }
