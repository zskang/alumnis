package com.nethsoft.web.controller.campus;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.CodeClass;
import com.nethsoft.web.service.campus.CommonCodeService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping(value = "/campus/commoncode/")
public class CommonCodeController extends BaseController {
    @Autowired
    private CommonCodeService commonCodeService;

    /**
     * 行业树
     * @return
     */
    @RequestMapping(value="tree/industry")
    public @ResponseBody JSONArray industry() {
        return commonCodeService.industryTree();
    }

    /**
     * 专业树
     * @return
     */
    @RequestMapping(value="tree/collage")
    public @ResponseBody JSONArray collage() {
        return commonCodeService.collageTree();
    }

    /**
     * 专业树，学院+专业
     * @return
     */
    @RequestMapping(value="tree/major")
    public @ResponseBody JSONArray major() {
        return commonCodeService.majorTree();
    }

    /**
     * 班级  单选
     * @return
     */
    @RequestMapping(value="class")
    public  String classTree(Model model,PageBean pageBean, String className) {
        if(StringUtil.isNotEmpty(className)){
            pageBean.addCriterion(Restrictions.like("name", className.trim(), MatchMode.ANYWHERE));
        }
        List<CodeClass> list =  commonCodeService.listClass(pageBean);
        model.addAttribute("list", list);
        return "/campus/common/class";
    }

    /**
     * 班级 多选
     * @return
     */
    @RequestMapping(value="class/multiselect")
    public  String classTreeMultiselect(Model model,PageBean pageBean, String className) {
        if(StringUtil.isNotEmpty(className)){
            pageBean.addCriterion(Restrictions.like("name", className.trim(), MatchMode.ANYWHERE));
        }
        List<CodeClass> list =  commonCodeService.listClass(pageBean);
        model.addAttribute("list", list);
        return "/campus/common/class_multiselect";
    }

    /**
     * 辖区树
     * @return
     */
    @RequestMapping(value="tree/region")
    public @ResponseBody List<JSONObject> regionTree() {
        return commonCodeService.regionTree();
    }


}
