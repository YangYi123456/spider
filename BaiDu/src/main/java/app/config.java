package app;
//import com.spreada.utils.chinese.ZHConverter;

import java.util.Random;
public class config {




        public static String projectName = "BaiDu";

        /**
         * Mysql 配置
         */
        public static String DBNAME = "course";//数据库名称
        public static String HOST = "localhost";
        public static String USERNAME = "root";
        public static String PASSWD = "";
        public static int PORT = 3306;

        public static String MYSQL_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DBNAME + "?user=" + USERNAME + "&password=" + PASSWD + "&characterEncoding=UTF8"; // 阿里云服务器：域名+http端口

        public static String SUBJECT_TABLE = "subject";
        public static String DOMAIN_TABLE = "domain";
        public static String FACET_TABLE = "facet";
        public static String DEPENDENCY = "dependency";
        public static String TOPIC_TABLE="topic";
        public static String ASSEMBLE_TABLE="assemble";

        /**
         * 爬虫
         */
        //public static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
        public static int TEXTLENGTH = 50; // 保存文本最短长度

        public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"; // 代理设置

        // Stackoverflow网站爬虫参数配置：网站防扒做的比较好，爬虫条件苛刻
        public static int threadSO = 1; // SO网站的爬虫线程为1，否则会被禁止
        public static int retryTimesSO = new Random().nextInt(2) + 3; // 爬虫连接失败重试次数，注意设置为随机数，避免被发现是爬虫（下同）
        public static int retrySleepTimeSO = new Random().nextInt(10000) + 10000; // 爬虫连接失败重试的间隔
        public static int sleepTimeSO = new Random().nextInt(10000) + 10000; // 爬虫请求连接的时间间隔
        public static int timeOutSO = new Random().nextInt(10000) + 20000; // 爬虫连接超时的时间间隔
        public static String originSO = "https://stackoverflow.com"; // 网站域名
        public static String hostsSO = "as-sec.casalemedia.com"; // 网站主机信息

        // 其它网站爬虫参数配置：网站防扒做的不太好，爬虫条件更宽松
        public static int THREAD = 3;
        public static int retryTimes = new Random().nextInt(2) + 3;
        public static int retrySleepTime = new Random().nextInt(2000) + 1000;
        public static int sleepTime = new Random().nextInt(3000) + 3000;
        public static int timeOut = new Random().nextInt(3000) + 3000;

}

