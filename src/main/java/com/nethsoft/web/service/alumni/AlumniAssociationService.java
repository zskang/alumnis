package com.nethsoft.web.service.alumni;

import com.easemob.server.example.httpclient.apidemo.EasemobChatGroups;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.web.entity.alumni.AlumniAssociation;
import com.nethsoft.web.entity.alumni.AlumniAssociationMember;
import com.nethsoft.web.entity.alumni.AlumniGroup;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AlumniAssociationService extends BaseService<AlumniAssociation> {

    @Autowired
    private AlumniGroupService groupService;
    @Autowired
    private AlumniGroupMemberService groupMemberService;
    @Autowired
    private AlumniAssociationMemberService associationMemberService;

    public void del(String id){
        super.executeSQL("delete from alumni_association_member where association_id = ?", id);
        super.delete(id);
    }

    /**
     * 创建公开群组
     * @param groupName
     * @param desc
     * @param owner
     */
    public static String createOpenGroup(String groupName,String desc,String owner){
        ObjectNode dataObjectNode = JsonNodeFactory.instance.objectNode();
        dataObjectNode.put("groupname", groupName);
        dataObjectNode.put("desc", desc);
        dataObjectNode.put("approval", false);//不需要批准
        dataObjectNode.put("public", true);//公开
        dataObjectNode.put("maxusers", 300);
        dataObjectNode.put("owner", owner);
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        dataObjectNode.put("members", arrayNode);
        ObjectNode objectNode = EasemobChatGroups.creatChatGroups(dataObjectNode);
        if(objectNode != null && "200".equals(objectNode.get("statusCode").toString())){
            return objectNode.get("data").get("groupid").textValue();
        }
        return "";
    }

    /**
     * 在群组中移除一个人
     * @param groupId
     * @param userName
     * @return
     */
    public static boolean deleteUserFromGroup(String groupId,String userName){
        ObjectNode objectNode = EasemobChatGroups.deleteUserFromGroup(groupId,userName);
        return objectNode.get("data").get("result").booleanValue();
    }

    /**
     * 删除群组
     * @param groupId
     * @return
     */
    public static boolean deleteChatGroups(String groupId){
        ObjectNode objectNode = EasemobChatGroups.deleteChatGroups(groupId);
        return objectNode.get("data").get("success").booleanValue();
    }

    /**
     * 在群组中加一个人
     * @param groupId
     * @param userName
     * @return
     */
    public static boolean addUserToGroup(String groupId,String userName){
        ObjectNode objectNode = EasemobChatGroups.addUserToGroup(groupId,userName);
        return objectNode.get("data").get("result").booleanValue();
    }

    /**
     * 创建社团【包含群组】
     * @param name  社团名称
     * @param summary 社团描述
     * @param student
     * @return
     */
    public boolean create(String name,String summary, CampusStudent student) throws RuntimeException{
        //创建群组
        String groupId = createOpenGroup(name,summary,student.getImusername());
        if(StringUtil.isEmpty(groupId)){
            throw new RuntimeException("创建环信群组失败");
        }
        AlumniAssociation association = new AlumniAssociation(name,summary,getCurrentTime(),groupId);
        super.save(association);
        associationMemberService.addMember(association,student,"群主");
        return createMainGroup(association,student);
    }

    /**
     *创建群组，以群主的身份
     * @param association
     * @param student
     * @return
     */
    public boolean createMainGroup(AlumniAssociation association,CampusStudent student){
        AlumniGroup group = new AlumniGroup(association.getGroupId(),association.getName(),association.getSummary(),"true","true",getCurrentTime());
        groupService.save(group);
        groupMemberService.addMember(association.getGroupId(),student,"群主");
        return true;
    }

    /**
     * 退出社团
     * @param association
     * @param student
     * @return
     */
    public boolean remove(AlumniAssociation association, CampusStudent student) throws RuntimeException{
        boolean flag = true;
        AlumniAssociationMember associationMember = associationMemberService.get(association,student);
        if(associationMember == null){
            return false;
        }
        String groupId = association.getGroupId();
        if(associationMember.getPosition().equals("群主")){//解散社团
            if(StringUtil.isNotEmpty(groupId)){//此社团有群聊
                //删除群组成员
                groupMemberService.executeSQL("delete from alumni_group_member where groupId=?",groupId);
                //删除群组
                groupService.delete(groupId);
                //删除环信中的群组
                flag = deleteChatGroups(groupId);
                if(!flag){
                    throw new RuntimeException("删除环信群组失败");
                }
            }
            //删除社团成员
            associationMemberService.del(association.getId());
            //删除社团
            super.delete(association);
        }else{//成员
            if(StringUtil.isNotEmpty(groupId)){//此群组有群聊
                //删除群组成员
                groupMemberService.executeSQL("delete from alumni_group_member where groupId=? and student_id=?",groupId,student.getId());
                //删除环信服务器中的成员
                flag = deleteUserFromGroup(groupId,student.getImusername());
                if(!flag){
                    throw new RuntimeException("退出环信群组失败");
                }
            }
            //删除社团成员
            associationMemberService.del(association.getId(),student.getId());
        }
        return flag;
    }

    /**
     * 加入社团,如果此社团没有群组，则创建群组
     * @param association
     * @param student
     * @return
     */
    public boolean add(AlumniAssociation association, CampusStudent student){
        boolean flag = true;
        String groupId = association.getGroupId();
        String position = "成员";
        if(StringUtil.isEmpty(groupId)){//此社团还没有群组，默认第一个加入社团的人为群主
            //创建群组
            groupId = createOpenGroup(association.getName(),association.getSummary(),student.getImusername());
            if(StringUtil.isEmpty(groupId)){
                return false;
            }
            association.setGroupId(groupId);
            association.setModifyTime(getCurrentTime());
            super.update(association);
            flag = createMainGroup(association,student);
            position = "群主";
        }
        associationMemberService.addMember(association,student,position);
        return flag;
    }

    /**
     * 加入群聊
     * @param association
     * @param student
     * @return
     */
    public boolean addGroupChat(AlumniAssociation association, CampusStudent student){
        boolean flag = true;
        String groupId = association.getGroupId();
        //加入环信群组
        flag = addUserToGroup(groupId, student.getImusername());
        if (!flag) {
            return flag;
        }
        //添加群组成员
        groupMemberService.addMember(groupId,student,"成员");
        return flag;
    }
}
