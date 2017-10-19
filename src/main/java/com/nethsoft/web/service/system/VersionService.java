package com.nethsoft.web.service.system;

import org.springframework.stereotype.Service;
import com.nethsoft.web.entity.system.Version;
import com.nethsoft.web.service.BaseService;

@Service
public class VersionService extends BaseService<Version>{
	
	/**
	 * 获取系统版本
	 * @return
	 */
	public Version getVersion(){
		return getByHql("from Version");
	}
}
