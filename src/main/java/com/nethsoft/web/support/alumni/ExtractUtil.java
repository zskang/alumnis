package com.nethsoft.web.support.alumni;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 爬取新闻工具类
 */
public class ExtractUtil {

    /**
     * 连接网络资源
     * @param url  地址
     * @param method  方法类型，get/post
     * @param param  参数
     * @return
     * @throws Exception
     */
    private static Document connect(String url,Method method,Map<String,String> param) throws Exception{
        Document document = null;
        Connection conn = null;
        Response response = null;
        conn = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).timeout(100000);
        if (param != null) {
            conn.data(param);
        }
        if(method != null){
            conn.method(method);
        }else{
            conn.method(Method.GET);
        }
        //配置模拟浏览器
        setUserAgent(conn);
        response = conn.execute();
        document = Jsoup.parse(response.body());
        String charset = getCharset(document);
        if(!charset.toUpperCase().equals("UTF-8")){
            String str = new String(response.bodyAsBytes(),"UTF-8");
            document = Jsoup.parse(str);
        }
        return document;
    }

    /**
     * 模拟浏览器
     * @param conn
     */
    private static void setUserAgent(Connection conn){
        conn.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
    }

    /**
     * 获取字符集
     * @param doc
     * @return
     * @throws Exception
     */
    private static String getCharset (Document doc) throws Exception {
        Elements eles = doc.select("meta[http-equiv=Content-Type]");
        Iterator<Element> itor = eles.iterator();
        while (itor.hasNext())
            return matchCharset(itor.next().toString());
        return "gb2312";
    }

    /**
     * 匹配字符集
     * @param content
     * @return
     */
    private static String matchCharset(String content) {
        String chs = "gb2312";
        Pattern p = Pattern.compile("(?<=charset=)(.+)(?=\")");
        Matcher m = p.matcher(content);
        if (m.find())
            return m.group();
        return chs;
    }

    /**
     * 爬取新闻数据
     * @param url
     * @return
     * @throws Exception
     */
    public static JSONArray listNews(String url) throws Exception{
        JSONArray jsonArray = new JSONArray();
        //连接并获取body
        Document document = connect(url,null,null);
        if (document != null) {
            Element body = document.body();
            String bodyHtml = body.html();
            body = Jsoup.parse(bodyHtml).body();
            Element articleList = body.getElementsByClass("articleList").get(1);
            Elements divs = articleList.getElementsByTag("div");
            Map<String,String> map = null;
            Element tag_a = null;
            String href = "";
            for(int i = 0;i<divs.size();i++){
                map = new HashMap<String, String>();
                tag_a = divs.get(i).getElementsByTag("a").get(0);
                href = tag_a.attr("href");
                map.put("id",href.substring(href.indexOf("?")+1));
                map.put("title",tag_a.text());
                map.put("date",divs.get(i).getElementsByTag("span").get(0).ownText());
                //预览图写死，上传到文件服务器上的默认图片
                map.put("preview","/file/alumni/161116/ce7da26a.png");
                jsonArray.add(map);
            }
        }
        return jsonArray;
    }

    /**
     * 新闻详情
     * @param url
     * @return
     * @throws Exception
     */
    public static String detail(String url) throws Exception {
        String str = "";
        //连接并获取body
        Document document = connect(url,null,null);
        if (document != null) {
            Element body = document.body();
            String bodyHtml = body.html();
            body = Jsoup.parse(bodyHtml).body();
            Element element = body.getElementsByClass("body").get(1);
            Elements imgs = element.getElementsByTag("img");
            if(imgs != null && imgs.size()>0){
                String prefix = "http://alumni.ustb.edu.cn/";
                //给图片添加前缀
                for(Element img : imgs){
                    if(img.attr("src").startsWith("/downloadTheolFile.do")){
                        img.attr("src",prefix+img.attr("src"));
                    }
                }
            }
            str = element.outerHtml();
        }
        return str;
    }

    /**
     * 捐赠动态
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static JSONArray listDynamics(String url, Map<String,String> param) throws Exception{
        JSONArray jsonArray = new JSONArray();
        //连接并获取body
        Document document = connect(url,Method.POST,param);
        if (document != null) {
            Element body = document.body();
            String bodyHtml = body.html();
            body = Jsoup.parse(bodyHtml).body();
            //表格
            Element table = body.getElementsByClass("datalist").get(0);
            Elements trs =  table.getElementsByTag("tr");
            JSONObject jsonObject = new JSONObject();
            for(int i = 1;i<trs.size();i++){
                Elements tds = trs.get(i).getElementsByTag("td");
                jsonObject.element("donateDate",tds.get(0).ownText()).element("username",tds.get(1).ownText())
                    .element("project",tds.get(2).ownText()).element("graduateDate",tds.get(3).ownText())
                    .element("department",tds.get(4).ownText()).element("area",tds.get(5).ownText())
                    .element("donateSum",tds.get(6).ownText()).element("donateYear",tds.get(7).ownText())
                    .element("donateManner",tds.get(8).ownText()).element("donateGroup",tds.get(9).ownText());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

}