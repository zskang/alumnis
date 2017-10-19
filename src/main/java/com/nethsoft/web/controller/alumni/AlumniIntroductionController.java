package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.entity.alumni.AlumniIntroduction;
import com.nethsoft.web.service.alumni.AlumniIntroductionService;
import com.nethsoft.web.support.AppUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.rowset.serial.SerialClob;
import java.util.Date;
import java.util.List;

/**
 * 校友会简介
 */
@Controller
@RequestMapping(value = "/alumni/introduction")
public class AlumniIntroductionController{
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/introduction/";
    @Autowired
    private AlumniIntroductionService introductionService;

    /**
     * 首页
     */
    @RequestMapping(method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model) {
        List<AlumniIntroduction> list = introductionService.list();
        AlumniIntroduction introduction = null;
        if(!list.isEmpty()){
            introduction = list.get(0);
            introduction.setContentStr(AppUtil.converClobToString(introduction.getContent()));
        }
        model.addAttribute("introduction", introduction);
        return TPL + "index";
    }

    /**
     * 保存
     * @param alumniAssociation
     * @return
     */
    @RequestMapping(value = "/save", method =  {RequestMethod.POST})
    @ResponseBody
    public JSONObject save(AlumniIntroduction alumniAssociation) {
        try {
            alumniAssociation.setContent(new SerialClob(alumniAssociation.getContentStr().toCharArray()));
            alumniAssociation.setModifyTime(new Date());
            if(StringUtil.isEmpty(alumniAssociation.getId())){
                introductionService.save(alumniAssociation);
            }else{
                introductionService.update(alumniAssociation);
            }
            return WebResult.success().element("id",alumniAssociation.getId());
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

}
