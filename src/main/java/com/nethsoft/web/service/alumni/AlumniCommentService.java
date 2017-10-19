package com.nethsoft.web.service.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniComment;
import com.nethsoft.web.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlumniCommentService extends BaseService<AlumniComment> {

    public JSONArray listJSONArray(PageBean pageBean){
        JSONArray jsonArray = new JSONArray();
        pageBean.addOrder(Order.desc("createTime"));
        List<AlumniComment> list = super.listByPage(pageBean);
        if(!list.isEmpty()){
            JSONObject jsonObject = null;
            for(AlumniComment alumniComment : list){
                jsonObject = new JSONObject();
                jsonObject.element("id",alumniComment.getId()).element("content",alumniComment.getContent())
                .element("modifyTime",alumniComment.getCreateTime());
                Hibernate.initialize(alumniComment.getStudent());
                jsonObject.element("name",alumniComment.getStudent().getName());
                jsonObject.element("photo", StringUtil.isEmpty(alumniComment.getStudent().getPhoto())?"":alumniComment.getStudent().getPhoto());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
}
