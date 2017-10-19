package com.nethsoft.web.service.alumni;

import com.nethsoft.web.entity.alumni.AlumniAssociation;
import com.nethsoft.web.entity.alumni.AlumniAssociationMember;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class AlumniAssociationMemberService extends BaseService<AlumniAssociationMember> {

    /**
     * 根据用户ID查询社团信息，添加属性：joined,是否加入过此社团
     * @param studentId
     * @return
     */
    public JSONArray findByStudentId(String studentId){
        String sql = "select b.id,b.name,(select count(*) from ALUMNI_GROUP_MEMBER c where c.groupid = b.groupid and c.student_id='"+studentId+"') total " +
                "from ALUMNI_ASSOCIATION_MEMBER a, ALUMNI_ASSOCIATION b where a.association_id = b.id and a.student_id='"+studentId+"' " +
                "order by a.CREATE_TIME desc";
        List<Map<String,Object>> list = super.queryForList(sql);
        JSONArray jsonArray = new JSONArray();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            for(Map<String,Object> map : list){
                jsonObject = new JSONObject();
                jsonObject.element("id",map.get("ID").toString()).element("name",map.get("NAME").toString())
                        .element("joined","0".equals(map.get("TOTAL").toString())?"0":"1");
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 按时间倒序找第一个
     * @param studentId
     * @return
     */
    public AlumniAssociationMember findOneByStudentId(String studentId){
        List<AlumniAssociationMember> list = super.list(Restrictions.eq("campusStudent.id",studentId), Order.desc("createTime"));
        AlumniAssociationMember member = null;
        if(list != null && list.size()>0){
            member = list.get(0);
                Hibernate.initialize(member.getAssociation());
        }
        return member;
    }

    /**
     * 删除社团成员
     * @param associationId
     * @param studentId
     */
    public void del(String associationId,String studentId){
        super.executeSQL("delete from alumni_association_member where association_id=? and student_id=?",associationId,studentId);
    }

    /**
     * 删除社团成员
     * @param associationId
     */
    public void del(String associationId){
        super.executeSQL("delete from alumni_association_member where association_id=?",associationId);
    }

    /**
     * 查找社团成员
     * @param association
     * @param student
     * @return
     */
    public AlumniAssociationMember get(AlumniAssociation association, CampusStudent student){
       return super.get(Restrictions.eq("association",association),Restrictions.eq("campusStudent",student));
    }

    /**
     * 添加社团成员
     * @param association
     * @param student
     * @param position
     */
    public void addMember(AlumniAssociation association,CampusStudent student,String position){
        AlumniAssociationMember member = new AlumniAssociationMember(association,student,position,getCurrentTime());
        super.save(member);
    }

}
