package com.nethsoft.web.service.alumni;

import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniBanner;
import com.nethsoft.web.service.BaseService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlumniBannerService extends BaseService<AlumniBanner> {

    public List<AlumniBanner> find(PageBean pageBean){
        List<AlumniBanner> list = super.listByPage(pageBean);
        if(list != null && list.size()>0){
            for(AlumniBanner alumniBanner : list){
                Hibernate.initialize(alumniBanner.getNews());
            }
        }
        return list;
    }

    public JSONArray listJSONArray(){
        JSONArray jsonArray = new JSONArray();
        List<AlumniBanner> list = super.list();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            for(AlumniBanner alumniBanner : list){
                Hibernate.initialize(alumniBanner.getNews());
                jsonObject = new JSONObject();
                jsonObject.element("path",alumniBanner.getPath()).element("newsid",alumniBanner.getNews().getId())
                .element("title",alumniBanner.getNews().getTitle());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
}
