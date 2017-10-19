package com.nethsoft.web.service.alumni;

import com.nethsoft.web.entity.alumni.AlumniDonateProject;
import com.nethsoft.web.service.BaseService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlumniDonateProjectService extends BaseService<AlumniDonateProject> {

    public void del(String id){
        super.executeSQL("delete from alumni_donate where project_id=?",id);
        super.delete(id);
    }

    public JSONArray find(){
        JSONArray jsonArray = new JSONArray();
        List<Object[]> list = super.getBaseDao().getQuery("select id,name from AlumniDonateProject").list();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            for(Object[] objects :list){
                jsonObject = new JSONObject();
                jsonObject.element("id",objects[0]);
                jsonObject.element("name",objects[1]);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
}
