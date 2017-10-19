package com.nethsoft.web.service.system;

import org.springframework.stereotype.Service;

import com.nethsoft.web.entity.system.Email;
import com.nethsoft.web.service.BaseService;
import com.nethsoft.core.util.ObjectUtil;

@Service
public class EmailService extends BaseService<Email>{
	/**
	 * 保存到待发邮箱中
	 */
	@Override
	public void save(Email email) {
		if(ObjectUtil.isNull(email.getCreateUser()))
			email.setCreateUser(getCurrentUser());
		if(ObjectUtil.isNull(email.getCreateTime()))
			email.setCreateTime(getCurrentTime());
		super.save(email);
	}
}
