package com.nethsoft.web.service.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.web.entity.alumni.AlumniAssociation;
import com.nethsoft.web.entity.alumni.AlumniGroupMember;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlumniGroupMemberService extends BaseService<AlumniGroupMember> {

    @Autowired
    private AlumniGroupService groupService;
    @Autowired
    private AlumniAssociationService associationService;

    /**
     * 根据群组ID查询成员
     * @param groupId  群组ID
     * @return
     */
    public JSONArray findByGroupId(String groupId){
        List<AlumniGroupMember> list = super.list(Restrictions.eq("groupId",groupId));
        JSONArray jsonArray = new JSONArray();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            CampusStudent student = null;
            for(AlumniGroupMember member : list){
                student = member.getStudent();
                Hibernate.initialize(student);
                jsonObject = new JSONObject();
                jsonObject.element("name",student.getName()).element("imusername",student.getImusername())
                        .element("photo", StringUtil.isEmpty(student.getPhoto())?"":student.getPhoto());
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 退出群组
     * @param groupMember
     */
    public void delMember(AlumniGroupMember groupMember){
        if(groupMember.getPosition().equals("群主")){//解散群组
            String groupId = groupMember.getGroupId();
            //删除群组成员
            super.executeSQL("delete from alumni_group_member where groupId=?",groupId);
            //删除群组
            groupService.delete(groupId);
            AlumniAssociation  association = associationService.getByProperties("groupId",groupId);
            if(association != null){//此群组绑定了社团，仅仅解散群组，还是社团成员
                association.setGroupId(null);
                associationService.update(association);
            }
        }else{
            super.delete(groupMember);
        }
    }

    /**
     * 添加群组成员
     * @param groupId
     * @param student
     * @param position
     */
    public void addMember(String groupId,CampusStudent student,String position){
        AlumniGroupMember member = new AlumniGroupMember(groupId, student, position, getCurrentTime());
        super.save(member);
    }
}
