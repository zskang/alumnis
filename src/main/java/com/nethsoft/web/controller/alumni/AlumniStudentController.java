package com.nethsoft.web.controller.alumni;

import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.service.campus.CampusXjbService;
import com.nethsoft.web.service.campus.CommonCodeService;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 校友信息
 */
@Controller
@RequestMapping("/alumni/student")
public class AlumniStudentController {
	private final Logger logger = Logger.getLogger(this.getClass());
	 private final String TPL = "/alumni/student/";
	@Autowired
	private CampusXjbService xjbService;

    /**
     * 校友信息
     * @param model
     * @param pageBean
     * @param xm  姓名
     * @param csrq  出生日期
     * @param rxnj  入学年级
     * @param zyfx  专业方向
     * @param bm  班名
     * @return
     */
    @RequestMapping(method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,String xm,String csrq,String rxnj,String zyfx,String bm) {
        JSONArray list = xjbService.find(pageBean,xm,csrq,rxnj,zyfx,bm);
        model.addAttribute("list",list);
        model.addAttribute("yearRange", CommonCodeService.getYearRange());
        return TPL + "index";
    }

}
