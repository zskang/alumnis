package com.nethsoft.web.controller.system;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.Log;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.core.util.StringUtil;

@Controller
@RequestMapping(value="/system/log")
public class LogController extends BaseController<Log>{
	
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model, PageBean pageBean, String time, String order){
		if(ObjectUtil.isNull(time) || StringUtil.isBlank(time)){
			model.addAttribute("defaultTime", getCurrentTime("yyyy-MM-dd")+" 00:00:00 - "+ getCurrentTime("yyyy-MM-dd")+" 23:59:59");
		}else{
			String[] times = time.split(" - ");
			pageBean.addCriterion(Restrictions.gt("time", times[0].trim()));
			pageBean.addCriterion(Restrictions.lt("time", times[1].trim()));
			model.addAttribute("defaultTime", time);
			model.addAttribute("time", time);
		}
		//排序
		if(ObjectUtil.isNull(order)){
			pageBean.addOrder(Order.desc("time"));
			model.addAttribute("order", "desc");
		}else{
			if("asc".equals(order))
				pageBean.addOrder(Order.asc("time"));
			else
				pageBean.addOrder(Order.desc("time"));
			model.addAttribute("order", order);
		}
		List<Log> list = logService.listByPage(pageBean);
		model.addAttribute("list", list);
		return "system/log/index";
	}
}
