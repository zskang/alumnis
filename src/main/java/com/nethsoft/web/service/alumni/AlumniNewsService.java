package com.nethsoft.web.service.alumni;

import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.entity.alumni.AlumniVoteConfigure;
import com.nethsoft.web.service.BaseService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.support.Constant;
import com.nethsoft.web.support.alumni.ExtractUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.ParameterMode;
import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Service
public class AlumniNewsService extends BaseService<AlumniNews> {
    private Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private AlumniCommentOpenService alumniCommentOpenService;
    @Autowired
    private AlumniVoteConfigureService alumniVoteConfigureService;
    @Autowired
    private AlumniNewsAgreeService alumniNewsAgreeService;
    @Autowired
    private AlumniEnterService alumniEnterService;
    @Autowired
    private AlumniVoteService alumniVoteService;
    @Autowired
    private CampusStudentService campusStudentService;
    @Autowired
    private AlumniCommentService alumniCommentService;

    /**
     * 查询新闻数据
     * @param pageBean
     * @param title  标题
     * @param type  新闻类型
     * @return
     * @throws Exception
     */
    public JSONArray find(PageBean pageBean,String title,String type) throws Exception{
        if(StringUtil.isNotEmpty(title)){
            pageBean.addCriterion(Restrictions.like("title", title.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addCriterion(Restrictions.eq("type",type));
        pageBean.addOrder(Order.asc("top"));
        pageBean.addOrder(Order.desc("newsTime"));
        List<AlumniNews> list = super.listByPage(pageBean);
        JSONArray jsonArray = new JSONArray();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            Date currentDate = DateUtil.parseDate(DateUtil.now("yyyy-MM-dd"));
            for(AlumniNews alumniNews : list){
                jsonObject = new JSONObject();
                boolean flag = DateUtil.parseDate(alumniNews.getNewsTime()).before(currentDate);
                jsonObject.element("id",alumniNews.getId()).element("title",alumniNews.getTitle()).element("date",alumniNews.getNewsTime())
                        .element("preview",alumniNews.getPreview()).element("type",flag?0:1);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }


    /**
     * 删除相关的所有数据
     * @param id  新闻ID
     * @param type  新闻类型
     */
    public void del(String id,String type){
        //校友活动
//        if(type.equals(Constant.NEWS_TYPE_ALUMNI_ACTIVITY)){
//            //删除报名数据
//            super.executeSQL("delete from alumni_enter where news_id='"+id+"'");
//            //删除投票数据
//            super.executeSQL("delete from alumni_vote where news_id='"+id+"'");
//            //删除投票选项数据
//            super.executeSQL("delete from alumni_vote_configure where news_id='"+id+"'");
//        }
//        //删除评论数据
//        super.executeSQL("delete from alumni_comment where news_id='"+id+"'");
//        //删除点赞数据
//        super.executeSQL("delete from alumni_news_agree where news_id='"+id+"'");
//        //删除轮播图数据
//        super.executeSQL("delete from alumni_banner where newsid='"+id+"'");
//        super.delete(id);
        //调用存储过程删除
        ProcedureCall pro_test = super.baseDao.getSession().createStoredProcedureCall("pro_delete_news");
        pro_test.registerParameter(0,String.class, ParameterMode.IN).bindValue(id);
        pro_test.registerParameter(1,String.class,ParameterMode.IN).bindValue(type);
        ProcedureOutputs procedureOutputs = pro_test.getOutputs();
    }

    /**
     * 新闻详情
     * @param alumniNews  新闻
     * @param pageBean
     * @param token  用户ID
     * @return
     */
    public JSONObject recordInfo(AlumniNews alumniNews,PageBean pageBean,String token){
        //获取评论数据
        String type = alumniNews.getType();
        String key = "";
        if(type.equals(Constant.NEWS_TYPE_ALMA_MATER)){
            key = Constant.KEY_COMMENT_OPEN_ALMA_MATER;
        }else if(type.equals(Constant.NEWS_TYPE_ALUMNI_DYNAMICS)){
            key = Constant.KEY_COMMENT_OPEN_ALUMNI_DYNAMICS;
        }else if(type.equals(Constant.NEWS_TYPE_ALUMNI_ACTIVITY)){
            key = Constant.KEY_COMMENT_OPEN_ALUMNI_ACTIVITY;
        }else if(type.equals(Constant.NEWS_TYPE_ALUMNI_DONATIONS)){
            key = Constant.KEY_COMMENT_OPEN_ALUMNI_DONATIONS;
        }
        Object comment_open = alumniCommentOpenService.getValueByKey(key);
        JSONArray jsonArray = null;
        if(comment_open == null){
            jsonArray = new JSONArray();
        }else if(comment_open.toString().equals("1")){//不公开
            jsonArray = new JSONArray();
        }else{
            pageBean.addCriterion(Restrictions.eq("news",alumniNews));
            jsonArray = alumniCommentService.listJSONArray(pageBean);
        }
        //获取评论数据

        Integer browseNum = alumniNews.getBrowse();
        //浏览次数加1
        if(browseNum == null){
            browseNum = 1;
        }else{
            browseNum += 1;
        }
        alumniNews.setBrowse(browseNum);
        super.update(alumniNews);
        String newsid = alumniNews.getId();
        //判断是否点过赞
        int agreeCount = alumniNewsAgreeService.count(Restrictions.eq("newsId",newsid),Restrictions.eq("studentId",token));
        //当前注册的总人数
        int totalNum = campusStudentService.countAlumni();
        //报名的总人数
        int enterNum = alumniEnterService.count(Restrictions.eq("news.id",newsid));
        //投票的总人数
        int voteNum = alumniVoteService.queryForInt("select count(distinct(student_id)) from ALUMNI_VOTE where news_id='"+newsid+"'");
        //判断是否报过名
        int enterCount = alumniEnterService.count(Restrictions.eq("news.id",newsid),Restrictions.eq("student.id",token));

        JSONObject result = WebResult.success().element("comment",jsonArray).element("browseNum",browseNum)
                .element("agreeNum",alumniNews.getAgree()).element("agreeState",agreeCount==0?"0":"1").element("title",alumniNews.getTitle())
                .element("totalNum",totalNum).element("enterNum",enterNum).element("voteNum",voteNum).element("enterState",enterCount==0?"0":"1");
        //如果是校友活动，查询投票选项
        if(type.equals(Constant.NEWS_TYPE_ALUMNI_ACTIVITY)){
            List<AlumniVoteConfigure> list = alumniVoteConfigureService.list(Restrictions.eq("news.id",newsid));
            JSONArray array = new JSONArray();
            if(list != null && list.size()>0){
                JSONObject jsonObject = null;
                for(AlumniVoteConfigure alumniVoteConfigure : list){
                    jsonObject = new JSONObject();
                    jsonObject.element("value",alumniVoteConfigure.getId()).element("name",alumniVoteConfigure.getName());
                    array.add(jsonObject);
                }
            }
            result.element("voteType",alumniNews.getVoteType()).element("options",array);
        }
        return result;
    }

    /**
     * 保存新闻，用于提取学校的新闻
     * @param url  新闻列表地址
     * @param type  新闻类型
     */
    public void save4Extract(String url,String type){
        JSONArray jsonArray = null;
        List<AlumniNews>  newsList = new LinkedList<AlumniNews>();
        List<Map<String,Object>> list = null;
        AlumniNews news = null;
        try {
            //爬取学校的新闻
            jsonArray = ExtractUtil.listNews(url);
            if(!jsonArray.isEmpty()){
                //到库中查询新闻的标题，用于判断是否已存在
                list = super.queryForList("select title \"title\" from alumni_news where type='"+ type+"'");
                for(int i = 0;i<jsonArray.size();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    final String title = jsonObject.getString("title");
                    if(StringUtil.isEmpty(title)){
                        continue;
                    }
                    boolean flag = false;
                    if(!list.isEmpty()){
                        flag = CollectionUtils.exists(list, new Predicate() {
                            @Override
                            public boolean evaluate(Object object) {
                                Map<String,Object> map = (Map<String,Object>) object;
                                return map.get("title").toString().equals(title);
                            }
                        });
                    }
                    if(flag){
                        continue;
                    }
                    String id = jsonObject.getString("id");
                    if(!id.startsWith("articleId")){
                        continue;
                    }
                    String contentStr = ExtractUtil.detail("http://alumni.ustb.edu.cn/infoSingleArticle.do?"+id);
                    String date = jsonObject.getString("date");
                    Clob content = new SerialClob(contentStr.toCharArray());
                    news = new AlumniNews(title,content,date,type,"1","/file/alumni/161116/ce7da26a.png");
                    newsList.add(news);
                }
                if(!newsList.isEmpty()){
                    super.save(newsList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("爬取新闻失败："+e.getMessage());
        }
    }

}
