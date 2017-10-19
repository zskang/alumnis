package com.nethsoft.web.service.alumni;

import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.entity.alumni.AlumniVote;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AlumniVoteService extends BaseService<AlumniVote> {

    public JSONArray listJSONArray(String newsId){
        String hql = "select student.name,nvl(max(student.photo),'') from AlumniVote where news.id=:newsId group by student.name";
        List<Object[]> list = baseDao.getQuery(hql).setString("newsId",newsId).list();
        JSONArray jsonArray = new JSONArray();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            for(Object[] objects :list){
                jsonObject = new JSONObject();
                jsonObject.element("name",objects[0]);
                jsonObject.element("photo",objects[1]==null?"":objects[1]);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    public List<AlumniVote> find(PageBean pageBean){
        List<AlumniVote> list = super.listByPage(pageBean);
        if(list != null && list.size()>0){
            for(AlumniVote alumniVote : list){
                Hibernate.initialize(alumniVote.getStudent());
            }
        }
        return list;
    }

    public void save(String newsId,String stndentId,String[] options,AlumniNews alumniNews,CampusStudent campusStudent){
        //已本次投票为准，以前的都先删掉
        super.executeSQL("delete from alumni_vote where student_id=? and news_id=?",stndentId,newsId);
        List<AlumniVote> list = new ArrayList<AlumniVote>(options.length);
        AlumniVote alumniVote = null;
        for(String option : options){
            alumniVote = new AlumniVote(alumniNews,campusStudent,getCurrentTime(),option);
            list.add(alumniVote);
        }
        super.save(list);
    }

}
