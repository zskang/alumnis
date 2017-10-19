package com.nethsoft.web.service.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniEnter;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.service.BaseService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlumniEnterService extends BaseService<AlumniEnter> {

    public List<AlumniEnter> find(PageBean pageBean){
        List<AlumniEnter> list = super.listByPage(pageBean);
        if(list != null && list.size()>0){
            for(AlumniEnter alumniEnter : list){
                Hibernate.initialize(alumniEnter.getStudent());
            }
        }
        return list;
    }

    /**
     * 根据新闻ID查找报名人员情况
     * @param newsId
     * @return
     */
    public JSONArray find(String newsId){
        List<AlumniEnter> list = super.list(Restrictions.eq("news.id",newsId));
        JSONArray jsonArray = new JSONArray();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            for(AlumniEnter alumniEnter : list){
                Hibernate.initialize(alumniEnter.getStudent());
                jsonObject = new JSONObject();
                jsonObject.element("name",alumniEnter.getStudent().getName()).element("photo",
                        StringUtil.isEmpty(alumniEnter.getStudent().getPhoto())?"":alumniEnter.getStudent().getPhoto());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 查找报名情况
     * @param pageBean
     * @return
     */
    public JSONArray findByStudent(PageBean pageBean){
        List<AlumniEnter> list = super.list(pageBean);
        JSONArray jsonArray = new JSONArray();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            AlumniNews alumniNews = null;
            for(AlumniEnter alumniEnter : list){
                alumniNews = alumniEnter.getNews();
                Hibernate.initialize(alumniNews);
                jsonObject = new JSONObject();
                jsonObject.element("id",alumniNews.getId()).element("title",alumniNews.getTitle())
                        .element("date",alumniNews.getNewsTime());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 根据新闻查询报名人数
     * @param id
     * @return
     */
    public int count(String id){
        return super.count(Restrictions.eq("news.id",id));
    }
}
