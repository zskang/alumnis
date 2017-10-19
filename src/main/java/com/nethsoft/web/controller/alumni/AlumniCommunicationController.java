package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniNotice;
import com.nethsoft.web.entity.alumni.AlumniUser;
import com.nethsoft.web.service.alumni.AlumniNoticeService;
import com.nethsoft.web.service.alumni.AlumniUserService;
import com.nethsoft.web.service.campus.CommonCodeService;
import com.nethsoft.web.support.alumni.AlumniMailUtil;
import com.nethsoft.web.support.alumni.AlumniMsmUtil;
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
import java.util.List;

/**
 * 站内通信
 */
@Controller
@RequestMapping(value = "/alumni/communication")
public class AlumniCommunicationController extends BaseController{
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/communication/";
    @Autowired
    private AlumniUserService alumniUserService;
    @Autowired
    private AlumniNoticeService alumniNoticeService;

    /**
     * 短信和邮件页面
     * @param model
     * @param pageBean
     * @param name  姓名
     * @param mobile  手机
     * @param csrq  出生日期
     * @param email 邮箱
     * @param rxnf  入学年份
     * @param zyfx  专业方向
     * @param className  班名
     * @param industry  行业
     * @param region  辖区
     * @return
     */
    @RequestMapping("/sms_mail")
    public String index(Model model, PageBean pageBean,String name,String mobile,String csrq,String email,String rxnf,String zyfx,
                        String className,String industry,String region) {
        pageBean = AlumniUserController.packagePageBean(pageBean,name,mobile,csrq,email,rxnf,zyfx,className,industry,region);
        pageBean.addOrder(Order.desc("modifyTime"));
        List<AlumniUser> list = alumniUserService.listEntity(pageBean);
        model.addAttribute("list", list);
        model.addAttribute("yearRange",CommonCodeService.getYearRange());
        return TPL + "sms_mail";
    }

    /**
     * 发送邮件
     * @param emailArr  收件人邮箱数组
     * @param subject  主题
     * @param message  内容
     */
    @RequestMapping("/sendMail")
    public @ResponseBody boolean sendMail(@RequestParam(value="emailArr") String[] emailArr,@RequestParam(value="subject") String subject,
                                         @RequestParam(value="message") String message) {
        return AlumniMailUtil.sendCommonMail(emailArr,subject,message);
    }

    /**
     * 发送短信
     * @param phone  手机号
     * @param message  信息
     * @return
     */
    @RequestMapping("/sendMessage")
    public @ResponseBody JSONObject sendMessage(@RequestParam(value="phone") String phone, @RequestParam(value="message") String message) {
        JSONObject obj = null;
        try {
            String result = AlumniMsmUtil.sendTextSms(phone,message);
            String status = result.substring(result.indexOf("<status>")+8,result.indexOf("</status>"));
            if (status.equals("0")) {
                obj = WebResult.success();
            }else if(status.equals("106")){
                obj = WebResult.error("有错误号码");
            }else if(status.equals("100")){
                obj = WebResult.error("发送失败");
            }
        } catch (Exception e) {
            logger.error(e);
            obj = WebResult.error(e);
        }
        return obj;
    }

    /**
     * 通知页面
     * @param model
     * @param pageBean
     * @param title
     * @param date
     * @return
     */
    @RequestMapping("/notice")
    public String pushIndex(Model model,PageBean pageBean,String title,String date) {
        if(StringUtil.isNotEmpty(title)){
            pageBean.addCriterion(Restrictions.like("title", title.trim(), MatchMode.ANYWHERE));
        }
        if(StringUtil.isNotEmpty(date)){
            String startDate = date + " 00:00:00";
            String endDate = date + " 23:59:59";
            pageBean.addCriterion(Restrictions.between("createTime",startDate,endDate));
        }
        pageBean.addOrder(Order.desc("createTime"));
        List<AlumniNotice> list = alumniNoticeService.listByPage(pageBean);
        model.addAttribute("list", list);
        return TPL + "notice/index";
    }

    /**
     * 到推送页面
     * @param model
     * @return
     */
    @RequestMapping("/notice/push")
    public String push(Model model) {
        //年份范围
        model.addAttribute("yearRange",CommonCodeService.getYearRange());
        return TPL + "notice/push";
    }

    /**
     * 推送消息
     * @param title  标题
     * @param content  内容
     * @param tags  标签
     * @return
     */
    @RequestMapping("/push/do")
    public @ResponseBody JSONObject doPush(HttpServletRequest request, @RequestParam(value="title") String title,
                  @RequestParam(value="content") String content, @RequestParam(value="tags") String tags) {
        String path = request.getScheme()+"://" + request.getServerName()+":"+request.getServerPort()+request.getContextPath()
                +"/open/alumni/notice/detail/";
        AlumniNotice alumniNotice = new AlumniNotice(title,content,getCurrentTime(),tags);
        try{
            alumniNoticeService.insertAndPush(alumniNotice,path);
            return WebResult.success();
        }catch (Exception e){
            return WebResult.error(e.getMessage());
        }
    }

    /**
     * 转向编辑页面
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/notice/edit/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String edit(Model model, @PathVariable String id) {
        AlumniNotice alumniNotice = alumniNoticeService.getById(id);
        if (alumniNotice == null) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        model.addAttribute("alumniNotice", alumniNotice);
        return TPL + "notice/edit";
    }

    /**
     * 执行编辑
     * @param alumniNotice
     * @return
     */
    @RequestMapping(value = "/notice/edit/do", method = {RequestMethod.POST})
    public  @ResponseBody JSONObject doEdit(AlumniNotice alumniNotice) {
        try {
            AlumniNotice alumniNoticeEq = alumniNoticeService.getById(alumniNotice.getId());
            if (alumniNoticeEq == null) {
                return WebResult.error("非法操作");
            }
            alumniNotice.setCreateTime(getCurrentTime());
            alumniNoticeService.merge(alumniNotice);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/notice/delete/{id}", method = { RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody JSONObject delete(@PathVariable String id) {
        try {
            alumniNoticeService.delete(id);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

}
