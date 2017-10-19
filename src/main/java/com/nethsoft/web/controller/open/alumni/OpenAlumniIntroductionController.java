package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.web.entity.alumni.AlumniIntroduction;
import com.nethsoft.web.service.alumni.AlumniIntroductionService;
import com.nethsoft.web.support.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 校友简介
 */
@Controller
@RequestMapping(value = "/open/alumni/introduction")
public class OpenAlumniIntroductionController                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            {

	@Autowired
	private AlumniIntroductionService introductionService;

	@RequestMapping()
	public String index(Model model) {
		List<AlumniIntroduction> list = introductionService.list();
		String content = "";
		if(!list.isEmpty()){
			content = AppUtil.converClobToString(list.get(0).getContent());
		}
		model.addAttribute("content", content);
		return "/alumni/introduction/index_open";
	}

}
