package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.FileUtil;
import com.nethsoft.core.util.ImageUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.controller.common.UploadController;
import com.nethsoft.web.entity.alumni.AlumniDonate;
import com.nethsoft.web.entity.alumni.AlumniDonateFeedback;
import com.nethsoft.web.service.alumni.AlumniDonateFeedbackService;
import com.nethsoft.web.service.alumni.AlumniDonateService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * 捐赠反馈
 */
@Controller
@RequestMapping(value = "/alumni/donate/feedback")
public class AlumniDonateFeedbackController extends BaseController<AlumniDonateFeedback> {
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/donate/feedback/";
    @Autowired
    private AlumniDonateFeedbackService alumniDonateFeedbackService;
    @Autowired
    private AlumniDonateService alumniDonateService;

    /**
     * 首页
     */
    @RequestMapping(method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,String date,String name) {
        if(StringUtil.isNotEmpty(date)){
            String startDate = date + " 00:00:00";
            String endDate = date + " 23:59:59";
            pageBean.addCriterion(Restrictions.between("createTime",startDate,endDate));
        }
        if(StringUtil.isNotEmpty(name)){
            pageBean.addCriterion(Restrictions.like("student.name", name.trim(), MatchMode.ANYWHERE));
        }
        pageBean.addOrder(Order.desc("createTime"));
        List<AlumniDonate> list = alumniDonateService.find(pageBean);
        model.addAttribute("list", list);
        return TPL + "index";
    }

    /**
     * 转向新增页面
     */
    @RequestMapping(value = "/add/{id}", method =  {RequestMethod.GET})
    public String add(Model model, @PathVariable String id) {
        AlumniDonate alumniDonate = alumniDonateService.getById(id);
        if (alumniDonate == null) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        model.addAttribute("donateId", id);
        return TPL + "add";
    }

    private static final String UPLOAD_ROOT = ApplicationCoreConfigHelper.getProperty("upload.folder.root");

    /**
     * 创建捐赠图片
     * @param request
     * @param donateId
     * @return
     */
    @RequestMapping(value = "/image/create", method =  {RequestMethod.POST})
    public  @ResponseBody JSONObject createImage(HttpServletRequest request,@RequestParam("donateId") String donateId) {
        try {
            AlumniDonate alumniDonate = alumniDonateService.get(donateId);
            if(alumniDonate == null){
                return WebResult.error("参数错误");
            }
            String fileName =  donateId+".png";
            //临时图片
            String tempPath = request.getServletContext().getRealPath(UPLOAD_ROOT+"alumni"+File.separator+fileName);
            //模板图片
            String templatePath = request.getServletContext().getRealPath(UPLOAD_ROOT+"alumni"+ File.separator+"donate.png");
            //生成图片
            createImage(templatePath,tempPath,alumniDonate.getStudent().getName(),alumniDonate.getMoney().toString());
            //上传到文件服务器
            String path = UploadController.upload(FileUtil.readFileToByteArray(new File(tempPath)),"png");
            //删除临时图片
            FileUtil.deleteFile(tempPath);
            if(StringUtil.isEmpty(path)){
                return WebResult.error("生成图片失败");
            }else{
                return WebResult.success().element("path",path);
            }
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.getMessage());
        }
    }

    /**
     * 生成图片
     * @param srcImagePath
     * @param toPath
     * @param name
     * @param money
     */
    private void createImage(String srcImagePath,String toPath,String name,String money){
        try{
            float alpha = 1.0F;
            String  font = "黑体";
            int fontStyle = Font.PLAIN;
            int fontSize = 18;
            Color color = Color.BLACK;
            String inputWords = name+"校友:";
            int x = 85;
            int y = 330;
            String imageFormat = "png";
            ImageUtil.alphaWords2Image(srcImagePath, alpha, font, fontStyle, fontSize, color, inputWords, x, y, imageFormat, toPath); //设置文字水印

            inputWords = "北京科技大学承蒙捐赠人民币";
            inputWords += StringUtil.hangeToBig(money);
            inputWords += "，至为铭感。谨呈证书，以兹永志。";
            int length = inputWords.length();
            x = 120;
            y = 360;
            //第一行
            String first = inputWords.substring(0,15);
            ImageUtil.alphaWords2Image(toPath, alpha, font, fontStyle, fontSize, color, first, x, y, imageFormat, toPath);
            String second = inputWords.substring(15,33);
            x = 85;
            y = 390;
            //第二行
            ImageUtil.alphaWords2Image(toPath, alpha, font, fontStyle, fontSize, color, second, x, y, imageFormat, toPath);
            if(first.length()+second.length()<length){
                x = 85;
                y = 420;
                String third = inputWords.substring(33);
                //第三行
                ImageUtil.alphaWords2Image(toPath, alpha, font, fontStyle, fontSize, color, third, x, y, imageFormat, toPath);
            }
            x = 270;
            y = 490;
            inputWords =  getCurrentTime("yyyy年M月dd日");
            ImageUtil.alphaWords2Image(toPath, alpha, font, fontStyle, fontSize, color, inputWords, x, y, imageFormat, toPath);
        }catch (Exception e){
            logger.error(e);
        }
    }

    /**
     * 执行新增
     */
    @RequestMapping(value = "/add/do", method =  {RequestMethod.POST})
    @ResponseBody
    public JSONObject doAdd(HttpServletRequest request,AlumniDonateFeedback alumniDonateFeedback,@RequestParam("donateId") String donateId) {
        try {
            AlumniDonate alumniDonate = alumniDonateService.get(donateId);
            if(alumniDonate == null){
                return WebResult.error("参数错误");
            }
            String path = request.getScheme()+"://" + request.getServerName()+":"+request.getServerPort()+request.getContextPath();
            path += "/open/alumni/notice/donate/detail/";
            alumniDonateFeedback.setDonate(alumniDonate);
            alumniDonateFeedback.setCreateTime(getCurrentTime());
            alumniDonateFeedbackService.insertAndPush(alumniDonateFeedback,alumniDonate.getStudent().getId(),path);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.getMessage());
        }
    }

}
