package com.nethsoft.web.service.alumni;

import com.nethsoft.web.entity.alumni.AlumniVoteConfigure;
import com.nethsoft.web.service.BaseService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlumniVoteConfigureService extends BaseService<AlumniVoteConfigure> {

    public JSONArray find(String newsId){
        List<AlumniVoteConfigure> list = super.list(Restrictions.eq("news.id",newsId));
        JSONArray jsonArray = new JSONArray();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            for(AlumniVoteConfigure configure : list){
                jsonObject = new JSONObject();
                jsonObject.element("id",configure.getId()).element("name",configure.getName());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
}
