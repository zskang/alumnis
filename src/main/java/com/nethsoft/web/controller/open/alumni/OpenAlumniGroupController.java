package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.alumni.AlumniGroup;
import com.nethsoft.web.entity.alumni.AlumniGroupMember;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.AlumniGroupMemberService;
import com.nethsoft.web.service.alumni.AlumniGroupService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 群组
 */
@RestController
@RequestMapping(value = "/open/alumni/group/")
public class OpenAlumniGroupController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private CampusStudentService campusStudentService;
	@Autowired
	private AlumniGroupService groupService;
	@Autowired
	private AlumniGroupMemberService groupMemberService;

	/**
	 * 创建群组
	 * @param token  用户ID
	 * @param groupid  群组ID
	 * @param name  群组名称
	 * @param desc  群组描述
	 * @param open  是否公开
	 * @param allowinvites  是否允许群成员邀请别人加入此群
	 * @param members  成员
     * @return
     */
	@RequestMapping(value="add")
	public JSONObject add(String token,String groupid,String name,String desc,String open,String allowinvites,String[] members) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(groupid)) {
			return WebResult.error("群组ID不能为空");
		}
		if(StringUtil.isEmpty(name)) {
			return WebResult.error("群组名称不能为空");
		}
		if(StringUtil.isEmpty(desc)) {
			return WebResult.error("群组描述不能为空");
		}
		if(StringUtil.isEmpty(open)) {
			return WebResult.error("群组公开属性不能为空");
		}
		String[] arr = {"true","false"};
		if(!ArrayUtils.contains(arr,open)){
			return WebResult.error("群组公开属性错误");
		}
		if(StringUtil.isEmpty(allowinvites)) {
			return WebResult.error("开放群成员邀请属性不能为空");
		}
		if(!ArrayUtils.contains(arr,allowinvites)){
			return WebResult.error("开放群成员邀请属性错误");
		}
		try{
			CampusStudent student = campusStudentService.getById(token);
			if(student == null){
				return WebResult.error("找不到此用户");
			}
			int count = groupService.count(Restrictions.eq("name",name));
			if(count > 0){
				return WebResult.error("已存在此群组");
			}
			String currentTime = getCurrentTime();
			AlumniGroupMember groupMember = null;
			List<AlumniGroupMember> memberList = new LinkedList<AlumniGroupMember>();
			if(!ArrayUtils.isEmpty(members)){
				List<CampusStudent> studentList = campusStudentService.getByIds(members);
				if(studentList.isEmpty()){
					return WebResult.error("找不到邀请成员");
				}
				for(CampusStudent campusStudent : studentList){
					groupMember = new AlumniGroupMember(groupid,campusStudent,"成员",currentTime);
					memberList.add(groupMember);
				}
			}
			AlumniGroup group = new AlumniGroup(groupid,name,desc,open,allowinvites,currentTime);
			groupService.save(group);
			groupMember = new AlumniGroupMember(groupid,student,"群主",currentTime);
			memberList.add(groupMember);
			groupMemberService.save(memberList);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 群组成员列表
	 * @param groupid  群组ID
	 * @return
     */
	@RequestMapping(value="member/list")
	public JSONObject listMember(String groupid) {
		if(StringUtil.isEmpty(groupid)) {
			return WebResult.error("群组ID不能为空");
		}
		try{
			return WebResult.success().element("data",groupMemberService.findByGroupId(groupid));
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 拉人进群
	 * @param groupid  群组ID
	 * @param users  用户ID数组
     * @return
     */
	@RequestMapping(value="member/add")
	public JSONObject addMember(String groupid,String[] users) {
		if(StringUtil.isEmpty(groupid)) {
			return WebResult.error("群组ID不能为空");
		}
		if(ArrayUtils.isEmpty(users)){
			return WebResult.error("邀请成员不能为空");
		}
		try{
			AlumniGroup group = groupService.getById(groupid);
			if(group == null){
				return WebResult.error("找不到此群组");
			}
			List<CampusStudent> studentList = campusStudentService.getByIds(users);
			if(studentList.isEmpty()){
				return WebResult.error("找不到邀请成员");
			}
			AlumniGroupMember member = null;
			List<AlumniGroupMember> list = new ArrayList<>(studentList.size());
			for(CampusStudent student : studentList){
				member = new AlumniGroupMember(groupid,student,"成员",getCurrentTime());
				list.add(member);
			}
			groupMemberService.save(list);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 退出群组
	 * @param groupid
	 * @param token
     * @return
     */
	@RequestMapping(value="member/del")
	public JSONObject delMember(String groupid,String token) {
		if(StringUtil.isEmpty(groupid)) {
			return WebResult.error("群组ID不能为空");
		}
		if(StringUtil.isEmpty(token)){
			return WebResult.error("用户ID不能为空");
		}
		try{
			AlumniGroup group = groupService.getById(groupid);
			if(group == null){
				return WebResult.error("找不到此群组");
			}
			AlumniGroupMember groupMember = groupMemberService.get(Restrictions.eq("groupId",groupid),Restrictions.eq("student.id",token));
			if(groupMember == null){
				return WebResult.error("不在此群组中");
			}
			groupMemberService.delMember(groupMember);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}


}
