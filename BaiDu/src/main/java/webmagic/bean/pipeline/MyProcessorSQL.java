package webmagic.bean.pipeline;

import app.config;
import utils.mysqlUtils;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Observable;
import java.lang.Object;

public class MyProcessorSQL {
//判断某门课程的数据是否存在
    public Boolean judgeByClass(String table,String domain){
        mysqlUtils mysql=new mysqlUtils();
        Boolean exist=false;
        String sql="select * from"+table +"where domain_name=?";
        List<Object> params=new ArrayList<Object>();
        params.add(domain);
        try{
            List<Map<String,Object>> results=mysql.returnMultipleResult(sql,params);
            if(results.size()!=0){
                exist=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mysql.closeconnection();
        }
        return exist;
    }
//获取所有的课程
    public List<Object> getCourses(){
        List<Object> courses=new ArrayList<Object>();
        mysqlUtils mysql=new mysqlUtils();
        String sql="select domain_id from domain";
        List<Object> params=new ArrayList<Object>();
        try{
            List<Map<String,Object>> results=mysql.returnMultipleResult(sql,params);
            for(Map<String,Object>m:results){
                courses.add(m.get("domain_id"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mysql.closeconnection();
        }
        return courses;
    }

    //判断某个分面是否已经存在数据库
    public Boolean judgeFacetByClassAndTerm(String table,String domain_name,String topic_name,String facet_name){
        mysqlUtils mysql=new mysqlUtils();
        Boolean exist=false;
        List<String> topic_id=new ArrayList<String>();
        String sql="select facet.* from"+ table+","+config.TOPIC_TABLE+","+config.DOMAIN_TABLE+"where domain_name=? and topic_name=? and facet_name=? and facet.topic_id=topic.topic_id and topic.domain_id=domain.domain_id";
        List<Object> params=new ArrayList<Object>();
        params.add(domain_name);
        params.add(topic_name);
        params.add(facet_name);
        try{
            List<Map<String,Object>> results=mysql.returnMultipleResult(sql,params);
            if(results.size()!=0){
                exist=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mysql.closeconnection();
        }
        return exist;
    }

    //获取分面信息（未考虑分面层级）
    public List<Map<String,Object>> getAllFacets(Object courseName){
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        mysqlUtils mysql=new mysqlUtils();
        String sql="select facet.facet_name,topic.topic_name,domain.domain_name,facet.facet_id,domain.domain_id from facet,topic,domain where facet.topic_id=topic.topic_id and topic.domain_id=domain.domain_id and domain.domain_id=?";
        //String sql="select facet.facet_name,topic.topic_name,domain.domain_name,facet.facet_id,domain.domain_id from"+config.FACET_TABLE+","+config.DOMAIN_TABLE+config.TOPIC_TABLE +"where domain.domain_name=? and facet.topic_id=topic.topic_id and topic.domain_id=domain.domain_id";
        List<Object> params=new ArrayList<Object>();
        params.add(courseName);
        try {
            results = mysql.returnMultipleResult(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return results;
    }

    //根据分面层级获取分面的名字
    public String getFacetName(Object facet_id){
        Object parent_id=new Object();
        String FacetName=new String();
        List<String> facet_names=new ArrayList<String>();
        List<Map<String,Object>> results=new ArrayList<Map<String, Object>>();
        mysqlUtils mysql=new mysqlUtils();
        //查询分面层级
        String sql="select facet_name,facet_layer from facet where facet_id=?";
        List<Object> params=new ArrayList<Object>();
        params.add(facet_id);
        try{
            results=mysql.returnMultipleResult(sql,params);
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        for(Map<String,Object>result:results){
             Object obj=result.get("facet_layer");
             //将结果集中的对象类型转换为整数类型
             int layer=1;
             if(obj instanceof Integer){
                 layer = (Integer)obj;
             }
             if(layer==1){
                    Object obj1=result.get("facet_name");
                    FacetName=obj1.toString();
                   // return FacetName;
             }

             else{
                 facet_names.add(result.get("facet_name").toString());
                     if(layer==2){
                         mysqlUtils mysql1=new mysqlUtils();
                         List<Map<String,Object>> results1=new ArrayList<Map<String, Object>>();
                         String sql1="select facet2.facet_id,facet2.facet_name,facet2.facet_layer from facet as facet1,facet as facet2 where facet1.parent_facet_id=facet2.facet_id and facet1.facet_id=?";
                         List<Object> params1=new ArrayList<Object>();
                             params1.add(facet_id);
                         try{
                             results1=mysql1.returnMultipleResult(sql1,params1);
                         }catch (Exception e){
                             e.printStackTrace();
                         }
                         finally{
                             mysql1.closeconnection();
                         }
                         for(Map<String,Object>result1:results1){
                             facet_names.add(result1.get("facet_name").toString());
                             //layer=(Integer)result1.get("facet_layer");
                             //parent_id=result1.get("facet_id");
                         }
                     }
                     else if(layer==3){
                         //获取直接上级的名字
                         mysqlUtils mysql1=new mysqlUtils();
                         List<Map<String,Object>> results1=new ArrayList<Map<String, Object>>();
                         String sql1="select facet2.facet_id,facet2.facet_name,facet2.facet_layer from facet as facet1,facet as facet2 where facet1.parent_facet_id=facet2.facet_id and facet1.facet_id=?";
                         List<Object> params1=new ArrayList<Object>();
                         params1.add(facet_id);
                         try{
                             results1=mysql1.returnMultipleResult(sql1,params1);
                         }catch (Exception e){
                             e.printStackTrace();
                         }
                         finally{
                             mysql1.closeconnection();
                         }
                         for(Map<String,Object>result1:results1){
                             facet_names.add(result1.get("facet_name").toString());
                             //layer=(Integer)result1.get("facet_layer");
                             parent_id=result1.get("facet_id");
                         }

                         //获取间接上级的名字
                         mysqlUtils mysql2=new mysqlUtils();
                         List<Map<String,Object>> results2=new ArrayList<Map<String, Object>>();
                         String sql2="select facet2.facet_name,facet2.facet_layer from facet as facet1,facet as facet2 where facet1.parent_facet_id=facet2.facet_id and facet1.facet_id=?";
                         List<Object> params2=new ArrayList<Object>();
                         params2.add(parent_id);
                         try{
                             results2=mysql2.returnMultipleResult(sql2,params2);
                         }catch (Exception e){
                             e.printStackTrace();
                         }
                         finally{
                             mysql2.closeconnection();
                         }
                         for(Map<String,Object>result1:results2){
                             facet_names.add(result1.get("facet_name").toString());
                             //layer=(Integer)result1.get("facet_layer");
                         }
                     }
                 }
                  for(int i=facet_names.size()-1;i>=0;i--){
                     FacetName=FacetName+" "+facet_names.get(i);
                 }
             }
        return FacetName;
    }
}
