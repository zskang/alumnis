package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.campus.CampusVerCode;
import com.nethsoft.web.service.campus.CampusVerCodeService;
import com.nethsoft.web.support.AppUtil;
import com.nethsoft.web.support.BaseObject;
import com.nethsoft.web.support.alumni.AlumniMsmUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码
 */
@RestController
@RequestMapping(value = "/open/alumni/vercode")
public class OpenAlumniVerCodeCotroller extends BaseObject{
    private Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private CampusVerCodeService campusVerCodeService;
    
    @RequestMapping(value="/send")
	public JSONObject sendCode(String mobile) {
		if(!StringUtil.isMobile(mobile)){
			return WebResult.error("手机号错误");
		}
		String code=AppUtil.createVerificationCode();
		try {
			String result = AlumniMsmUtil.sendCode(mobile,code);
			String status = result.substring(result.indexOf("<status>")+8,result.indexOf("</status>"));
			if(status.equals("0")){//发送成功，其他的都算失败
				CampusVerCode campusVerCode = new CampusVerCode(mobile, code,getCurrentTime());
				campusVerCodeService.save(campusVerCode);
				return WebResult.success();
			}else{
				return WebResult.error("获取验证码失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return WebResult.error(e);
		}
	}

}
