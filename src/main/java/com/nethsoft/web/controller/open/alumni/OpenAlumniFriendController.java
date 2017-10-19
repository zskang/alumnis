package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.alumni.AlumniFriend;
import com.nethsoft.web.entity.alumni.AlumniUser;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.AlumniFriendService;
import com.nethsoft.web.service.alumni.AlumniUserService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 好友
 */
@RestController
@RequestMapping(value = "/open/alumni/friend/")
public class OpenAlumniFriendController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private CampusStudentService campusStudentService;
	@Autowired
	private AlumniFriendService friendService;
	@Autowired
	private AlumniUserService alumniUserService;

	/**
	 * 添加好友
	 * @param token  用户ID
	 * @param friendid  好友ID
     * @return
     */
	@RequestMapping(value="add")
	public JSONObject add(String token,String friendid) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(friendid)) {
			return WebResult.error("好友ID不能为空");
		}
		try{
			CampusStudent self = campusStudentService.getById(token);
			if(self == null){
				return WebResult.error("登录用户不存在");
			}
			CampusStudent friend = campusStudentService.getById(friendid);
			if(friend == null){
				return WebResult.error("目标用户不存在");
			}
			//判断是否已经添加过好友
			int count = friendService.count(Restrictions.eq("self.id",token),Restrictions.eq("friend.id",friendid));
			if(count > 0){
				return WebResult.success();
			}
			AlumniFriend alumniFriend = new AlumniFriend(self,friend,getCurrentTime());
			friendService.save(alumniFriend);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 好友列表
	 * @param token  用户ID
	 * @param name  姓名，搜索时传
     * @return
     */
	@RequestMapping(value="list")
	public JSONObject list(String token,String name) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		try{
			return WebResult.success().element("data",friendService.list(token,name));
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 删除好友
	 * @param token  用户ID
	 * @param friendid  好友ID
     * @return
     */
	@RequestMapping(value="del")
	public JSONObject del(String token,String friendid) {
		if(StringUtil.isEmpty(token)) {
			return WebResult.error("用户ID不能为空");
		}
		if(StringUtil.isEmpty(friendid)) {
			return WebResult.error("好友ID不能为空");
		}
		try{
			friendService.executeSQL("delete from alumni_friend where (student_self=? and student_friend=?) " +
					"or (student_self=? and student_friend=?)",token,friendid,friendid,token);
			return WebResult.success();
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

	/**
	 * 判断是否为好友关系
	 * @param username  登录用户的环信账号
	 * @param otherusername  其他人的环信账号
     * @return
     */
	@RequestMapping(value="check")
	public JSONObject check(String username,String otherusername) {
		if(StringUtil.isEmpty(username) || StringUtil.isEmpty(otherusername)) {
			return WebResult.error("环信账号不能为空");
		}
		try{
			CampusStudent student = campusStudentService.get(Restrictions.eq("imusername",username));
			CampusStudent otherStudent = campusStudentService.get(Restrictions.eq("imusername",otherusername));
			if(student == null || otherStudent == null){
				return WebResult.error("用户不存在");
			}
			String id = student.getId();
			String otherId = otherStudent.getId();
			AlumniUser alumniUser = alumniUserService.get(Restrictions.eq("campusStudent.id",otherId));
			if(alumniUser == null){
				return WebResult.error("用户不存在");
			}
			boolean flag = false;
			if(username.equals(otherusername)){
				flag = true;
			}else{
				String sql = "select count(*) from alumni_friend where (student_self='"+id+"' and student_friend='"+otherId+"')" +
						" or (student_self='"+otherId+"' and student_friend='"+id+"')";
				flag = friendService.queryForInt(sql) > 0;
			}
			return WebResult.success().element("isFriend",flag).element("token",otherId).element("name",otherStudent.getName())
					.element("rxnf",StringUtil.null2String(alumniUser.getRxnf()))
					.element("zyfx",StringUtil.null2String(alumniUser.getZyfx()));
		}catch (Exception e){
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}

}
