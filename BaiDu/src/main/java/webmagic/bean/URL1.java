package webmagic.bean;

public class URL1 {
    private String url;//url链接
    private Object assemble_id;//碎片id
    private Object domain_id;//课程id

    //构造函数
    public URL1 url(String url){
        this.url=url;
        return this;
    }
    public String getUrl(){
        return url;
    }

    public URL1 assemble_id(Object assemble_id){
        this.assemble_id=assemble_id;
        return this;
    }
    public Object getAssemble_id(){
        return assemble_id;
    }

    public URL1 domain_id(Object domain_id){
        this.domain_id=domain_id;
        return this;
    }
    public Object getDomain_id(){
        return domain_id;
    }
}
