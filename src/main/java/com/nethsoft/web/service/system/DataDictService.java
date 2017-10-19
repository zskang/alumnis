package com.nethsoft.web.service.system;

import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import com.nethsoft.web.entity.system.DataDict;
import com.nethsoft.web.service.BaseService;

@Service
public class DataDictService extends BaseService<DataDict> {

	public List<DataDict> list_NoLazy(Order asc) {
		List<DataDict> list = this.list(asc);
		for(DataDict dd : list)
			Hibernate.initialize(dd.getItems());
		return list;
	}
	
}
