package NLP.SimHash;



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import utils.mysqlUtils;
import app.config;
import webmagic.bean.FragmentContent;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import utils.mysqlUtils;
import utils.mysqlUtils1;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SimHash {

    private String tokens;

    private BigInteger intSimHash;

    private String strSimHash;

    private int hashbits = 64;

    public SimHash(String tokens) {
        this.tokens = tokens;
        this.intSimHash = this.simHash();
    }

    public SimHash(String tokens, int hashbits) {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.intSimHash = this.simHash();
    }

    public BigInteger simHash() {
        int[] v = new int[this.hashbits];
        StringTokenizer stringTokens = new StringTokenizer(this.tokens);//默认以” \t\n\r\f”（前有一个空格，引号不是）为分割符。
        while (stringTokens.hasMoreTokens()) {//返回是否还含有分隔符，布尔值
            String temp = stringTokens.nextToken();//返回从当前位置到下一个分隔符的字符串
            BigInteger t = this.hash(temp);//求hash值
            for (int i = 0; i < this.hashbits; i++) {//加起来
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (t.and(bitmask).signum() != 0) {
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {//转换为1，0
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            }else{
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
        System.out.println(this.strSimHash + " length " + this.strSimHash.length());
        return fingerprint;//计算f位的指纹
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(
                    new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    /**
     * 取两个二进制的异或，统计为1的个数，就是海明距离
     * @param other
     * @return
     */

    public int hammingDistance(SimHash other) {

        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;

        //统计x中二进制位数为1的个数
        //我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，对吧，然后，n&(n-1)就相当于把后面的数字清0，
        //我们看n能做多少次这样的操作就OK了。

        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    /**
     * calculate Hamming Distance between two strings
     *  二进制怕有错，当成字符串，作一个，比较下结果
     * @author
     * @param str1 the 1st string
     * @param str2 the 2nd string
     * @return Hamming Distance between str1 and str2
     */
    public int getDistance(String str1, String str2) {
        int distance;
        if (str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    /**
     * 如果海明距离取3，则分成四块，并得到每一块的bigInteger值 ，作为索引值使用
     * @param simHash
     * @param distance
     * @return
     */
    public List<BigInteger> subByDistance(SimHash simHash, int distance){
        int numEach = this.hashbits/(distance+1);
        List<BigInteger> characters = new ArrayList();

        StringBuffer buffer = new StringBuffer();

        int k = 0;
        for( int i = 0; i < this.intSimHash.bitLength(); i++){
            boolean sr = simHash.intSimHash.testBit(i);

            if(sr){
                buffer.append("1");
            }
            else{
                buffer.append("0");
            }

            if( (i+1)%numEach == 0 ){
                BigInteger eachValue = new BigInteger(buffer.toString(),2);
                System.out.println("----" +eachValue );
                buffer.delete(0, buffer.length());
                characters.add(eachValue);
            }
        }

        return characters;
    }

    /**
     * 从数据库读取相同数据源下面的文章assemble_text
     * 统计相同文章的个数和不同文章的个数
     *
     */
    public static void main(String[] args) {

        //知乎文章
        mysqlUtils mysql = new mysqlUtils();
        List<Object> params=new ArrayList<Object>();
        List<Map<String,Object>> results=new ArrayList<Map<String, Object>>();
        String sql="select assemble_text from assemble_copy where source_id=2";
        try{
            results=mysql.returnMultipleResult(sql,params);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mysql.closeconnection();
        }
        List<Object> textList=new ArrayList<Object>();
        for(Map<String,Object> result:results){
            Object s1 = result.get("assemble_text");
            textList.add(s1);
        }
        mysqlUtils1 mysql1 = new mysqlUtils1();
        List<Object> params1=new ArrayList<Object>();
        List<Map<String,Object>> results1=new ArrayList<Map<String, Object>>();
        String sql1="select assemble_text from assemble_copy where source_id=2";
        try{
            results1=mysql1.returnMultipleResult(sql1,params1);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mysql1.closeconnection();
        }
        List<Object> textList1=new ArrayList<Object>();
        for(Map<String,Object> result:results1){
            Object s1 = result.get("assemble_text");
            textList1.add(s1);
        }
        int equals=0;
        for(int i=0;i<textList.size();i++)
        {
            for(int j=0;j<textList1.size();j++) {
                String s = textList.get(i).toString();
                String s1 = textList1.get(j).toString();
                SimHash hash1 = new SimHash(s, 64);
                //System.out.println(hash1.intSimHash + "  " + hash1.intSimHash.bitLength());
                hash1.subByDistance(hash1, 3);
                SimHash hash2 = new SimHash(s1, 64);
                //System.out.println(hash2.intSimHash+ "  " + hash2.intSimHash.bitCount());
                hash1.subByDistance(hash2, 3);
                int dis = hash1.getDistance(hash1.strSimHash,hash2.strSimHash);
                System.out.println(hash1.hammingDistance(hash2) + " "+ dis);
                if(dis<=3){
                    equals++;
                }
            }
            System.out.println(equals);

        }
    }
}

