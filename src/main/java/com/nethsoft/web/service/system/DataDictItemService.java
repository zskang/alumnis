package com.nethsoft.web.service.system;

import org.springframework.stereotype.Service;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.web.entity.system.DataDict;
import com.nethsoft.web.entity.system.DataDictItem;
import com.nethsoft.web.service.BaseService;
import com.nethsoft.core.util.ObjectUtil;

@Service
public class DataDictItemService extends BaseService<DataDictItem>{

	public void updateItem(String id, String[] key, String[] value, DataDict dd) {
		//删除原配置项
		this.executeHQL("delete from DataDictItem where dataDict.id='"+id+"'");
		if(ObjectUtil.isNull(key) && ObjectUtil.isNull(value))// key、value全部为null时不执行增加操作
			return ;
		for(int i = 0;i<value.length;i++){
			String k = (ObjectUtil.isNull(key)||key.length==0)?"":key[i];
			String v = value[i];
			
			//保证k和v只有一个是有效值
			if(!StringUtil.isEmpty(v)){
				DataDictItem ddItem = new DataDictItem();
				
				ddItem.setDataDict(dd);
				ddItem.setK(k);
				ddItem.setV(v);
				ddItem.setLocation(i+1);
				ddItem.setCreateUser(getCurrentUser().getUserName());
				ddItem.setCreateTime(getCurrentTime());
				
				this.save(ddItem);
			}
		}
	}

}
