package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.alumni.AlumniAssociation;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.AlumniAssociationMemberService;
import com.nethsoft.web.service.alumni.AlumniAssociationService;
import com.nethsoft.web.service.alumni.AlumniGroupMemberService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 社团分会
 */
@RestController
@RequestMapping(value = "/open/alumni/association/")
public class OpenAlumniAssociationController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private CampusStudentService campusStudentService;
	@Autowired
	private AlumniAssociationMemberService alumniAssociationMemberService;
	@Autowired
	private AlumniAssociationService alumniAssociationService;
	@Autowired
	private AlumniGroupMemberService groupMemberService;

	/**
	 * 社团列表
	 * @return
     */
	@RequestMapping(value = "list")
	public JSONObject index() {
		try{
			List<AlumniAssociation> list = alumniAssociationService.list(Order.desc("modifyTime"));
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = null;
			if(list != null && list.size()>0){
				for(AlumniAssociation association : list){
					jsonObject = new JSONObject();
					jsonObject.element("id",association.getId()).element("name",association.getName());
					jsonArray.add(jsonObject);
				}
			}
			jsonObject = new JSONObject();
			jsonObject.element("id","").element("name","其他");
			jsonArray.add(jsonArray.size(),jsonObject);
			return WebResult.success().element("data",jsonArray);
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 我的社团
	 * @param token  用户ID
	 * @return
     */
	@RequestMapping(value="list/own")
	public JSONObject list(String token) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			return WebResult.success().element("data",alumniAssociationMemberService.findByStudentId(token));
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 加入社团
	 * @param token  用户ID
	 * @param id  社团ID
     * @return
     */
	@RequestMapping(value="join")
	public JSONObject join(String token,String id) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(id)) {
			return WebResult.error("社团ID不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			AlumniAssociation association = alumniAssociationService.getById(id);
			if(association == null){
				return WebResult.error("找不到此社团");
			}
			int count = alumniAssociationMemberService.count(Restrictions.eq("campusStudent.id",token),Restrictions.eq("association.id",id));
			if(count >0){
				return WebResult.error("已加入此社团了");
			}
			if(alumniAssociationService.add(association,campusStudent)){
				return WebResult.success();
			}else{
				return WebResult.error("加入失败");
			}
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 退出社团
	 * @param token  用户ID
	 * @param id  社团ID
     * @return
     */
	@RequestMapping(value="quit")
	public JSONObject quit(String token,String id) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(id)) {
			return WebResult.error("社团ID不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			AlumniAssociation association = alumniAssociationService.getById(id);
			if(association == null){
				return WebResult.error("找不到此社团");
			}
			if(alumniAssociationService.remove(association,campusStudent)){
				return WebResult.success();
			}else{
				return WebResult.error("退出失败");
			}
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 创建社团
	 * @param token  用户ID
	 * @param name  社团名称
	 * @param summary  社团描述
     * @return
     */
	@RequestMapping(value="create")
	public JSONObject create(String token,String name,String summary) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(name)) {
			return WebResult.error("社团名称不能为空");
		}
		if(StringUtil.isEmpty(summary)) {
			return WebResult.error("社团描述不能为空");
		}
		try{
			int count = alumniAssociationService.count(Restrictions.eq("name",name));
			if(count > 0){
				return WebResult.error("已存在此社团");
			}
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			if(alumniAssociationService.create(name,summary,campusStudent)){
				return WebResult.success();
			}else{
				return WebResult.error("创建失败");
			}
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 加入群聊
	 * @param token  用户ID
	 * @param id  社团ID
     * @return
     */
	@RequestMapping(value="groupchat/join")
	public JSONObject joinGroupChat(String token,String id) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(id)) {
			return WebResult.error("社团ID不能为空");
		}
		try{
			CampusStudent campusStudent = campusStudentService.getById(token);
			if(campusStudent == null){
				return WebResult.error("找不到此用户");
			}
			AlumniAssociation association = alumniAssociationService.getById(id);
			if(association == null){
				return WebResult.error("找不到此社团");
			}
			if(StringUtil.isEmpty(association.getGroupId())){
				return WebResult.error("此社团还没有群聊");
			}
			int count = groupMemberService.count(Restrictions.eq("groupId", association.getGroupId()), Restrictions.eq("student.id", campusStudent.getId()));
			if (count > 0) {
				return WebResult.error("已加入此社团群聊了");
			}
			if(alumniAssociationService.addGroupChat(association,campusStudent)){
				return WebResult.success();
			}else{
				return WebResult.error("加入失败");
			}
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

}
