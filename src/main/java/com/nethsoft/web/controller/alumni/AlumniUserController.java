package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.FileUtil;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.alumni.AlumniUser;
import com.nethsoft.web.entity.alumni.CodeNation;
import com.nethsoft.web.entity.alumni.CodeXslb;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.alumni.AlumniUserService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.service.campus.CommonCodeService;
import com.nethsoft.web.support.AppUtil;
import com.nethsoft.web.support.ImUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户
 */
@Controller
@RequestMapping(value = "/alumni/user")
public class AlumniUserController extends BaseController<AlumniUser> {
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/user/";
    @Autowired
    private AlumniUserService alumniUserService;
    @Autowired
    private CampusStudentService campusStudentService;
    @Autowired
    private CommonCodeService commonCodeService;

    /**
     * 首页
     */
    @RequestMapping(method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean,String name,String mobile,String csrq,String email,String rxnf,String zyfx,
                        String className,String industry,String region) {
        pageBean = packagePageBean(pageBean,name,mobile,csrq,email,rxnf,zyfx,className,industry,region);
        pageBean.addOrder(Order.desc("modifyTime"));
        List<AlumniUser> list = alumniUserService.listEntity(pageBean);
        model.addAttribute("list", list);
        model.addAttribute("yearRange",CommonCodeService.getYearRange());
        return TPL + "index";
    }

    /**
     * 封装查询参数
     * @param pageBean
     * @param name 姓名
     * @param mobile  手机
     * @param csrq 出生日期
     * @param email 邮箱
     * @param rxnf  入学年份
     * @param zyfx 专业方向
     * @param className  班名
     * @param industry 行业
     * @param region 辖区
     * @return
     */
    public static PageBean packagePageBean(PageBean pageBean,String name,String mobile,String csrq,String email,String rxnf,
                                     String zyfx,String className,String industry,String region){
        if(StringUtil.isNotEmpty(name)){
            pageBean.addCriterion(Restrictions.like("campusStudent.name", name.trim(), MatchMode.ANYWHERE));
        }
        if(StringUtil.isNotEmpty(mobile)){
            pageBean.addCriterion(Restrictions.like("campusStudent.mobile", mobile.trim(), MatchMode.ANYWHERE));
        }
        if(StringUtil.isNotEmpty(csrq)){
            csrq = csrq.replace("-","");
            pageBean.addCriterion(Restrictions.eq("csrq",csrq));
        }
        if(StringUtil.isNotEmpty(email)){
            pageBean.addCriterion(Restrictions.like("campusStudent.email", email.trim(), MatchMode.ANYWHERE));
        }
        if(StringUtil.isNotEmpty(rxnf)){
            pageBean.addCriterion(Restrictions.eq("rxnf",rxnf));
        }
        if(StringUtil.isNotEmpty(zyfx)){
            pageBean.addCriterion(Restrictions.eq("zyfx",zyfx));
        }
        if(StringUtil.isNotEmpty(className)){
            pageBean.addCriterion(Restrictions.eq("bm",className.trim()));
        }
        if(StringUtil.isNotEmpty(industry)){
            pageBean.addCriterion(Restrictions.eq("industry",industry));
        }
        if(StringUtil.isNotEmpty(region)){
            pageBean.addCriterion(Restrictions.eq("region",region));
        }
        return pageBean;
    }

    private static final String UPLOAD_ROOT = ApplicationCoreConfigHelper.getProperty("upload.folder.root");

    /**
     * 生成Excel
     * @param request
     * @param pageBean
     * @param type  one：导出当前页，all:导出全部
     * @param name  姓名
     * @param mobile 手机
     * @param csrq  出生日期
     * @param email  邮箱
     * @param rxnf  入学年份
     * @param zyfx  专业方向
     * @param className  班名
     * @param industry  行业
     * @param region  辖区
     * @return
     */
    @RequestMapping(value="/createExcel")
    public @ResponseBody JSONObject createExcel(HttpServletRequest request,PageBean pageBean,String type,String name,String mobile,
                       String csrq,String email,String rxnf,String zyfx, String className, String industry, String region) {
        pageBean = packagePageBean(pageBean,name,mobile,csrq,email,rxnf,zyfx,className,industry,region);
        List<String[]> list = alumniUserService.listStringArray(pageBean,type);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //文件名，当前时间
        String fileName =  sdf.format(new Date())+".xls";
        //临时路径
        String tempPath = request.getServletContext().getRealPath(UPLOAD_ROOT+"alumni"+File.separator+fileName);
        //模板路径
        String templatePath = request.getServletContext().getRealPath(UPLOAD_ROOT+"alumni"+ File.separator+"alumni.xls");
        OutputStream os = null;
        try{
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(templatePath)));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if(list != null && list.size()>0){
                String[] rowData = null;
                HSSFRow row = null;
                HSSFCell cell = null;
                for(int i = 0;i<list.size();i++){
                    rowData = list.get(i);
                    row = sheet.createRow(i+2);
                    for(int j = 0;j<rowData.length;j++){
                        cell = row.createCell(j);
                        cell.setCellValue(rowData[j]);
                    }
                }
            }
            os = new FileOutputStream(new File(tempPath));
            wb.write(os);
            return WebResult.success().element("fileName",fileName);
        }catch (Exception e){
            return WebResult.error(e);
        }finally {
            if(os != null){
                try{
                    os.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导出Excel
     * @param request
     * @param response
     * @param fileName
     * @throws Exception
     */
    @RequestMapping("/export")
    public void export(HttpServletRequest request,HttpServletResponse response, @RequestParam("fileName") String fileName) throws Exception {
        //文件路径
        String path = request.getServletContext().getRealPath(UPLOAD_ROOT + "alumni" + File.separator + fileName);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        byte[] data = FileUtil.readFileToByteArray(new File(path));
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();
        //删除文件
        FileUtil.deleteFile(path);
    }

    /**
     * 导入Excel
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping("/import")
    public  @ResponseBody JSONObject importExcel(@RequestParam(value = "file") MultipartFile file) {
        return alumniUserService.insertExcel(file);
    }


    /**
     * 转向新增页面
     */
    @RequestMapping(value = "/add", method =  { RequestMethod.GET})
    public String add(Model model) {
        List<CodeNation> nationList = commonCodeService.listNation();
        List<CodeXslb> xslbList = commonCodeService.listXslb();
        model.addAttribute("nation",nationList);
        model.addAttribute("xslb",xslbList);
        model.addAttribute("yearRange",CommonCodeService.getYearRange());
        return TPL + "add";
    }

    /**
     * 执行新增
     */
    @RequestMapping(value = "/add/do", method =  {RequestMethod.POST})
    @ResponseBody
    public JSONObject doAdd(AlumniUser alumniUser,CampusStudent campusStudent) {
        try {
            //根据姓手机号检测此用户是否已注册过
            if(campusStudentService.isRegistered(campusStudent.getMobile())){
                return WebResult.error("该手机号已注册过");
            }
            //根据姓名，手机号，入学年份检测此用户是否已注册过
            if(campusStudentService.isRegistered(campusStudent.getName(),campusStudent.getMobile(),alumniUser.getRxnf())){
                return WebResult.error("该用户已注册过");
            }
            //生成环信账号
            String imusername = AppUtil.makeOrderNum();
            //身份证号后6位作为密码
            String idcard = campusStudent.getIdcard();
            String impassword = idcard.substring(idcard.length()-6);
            campusStudent.setImusername(imusername);
            campusStudent.setImpassword(impassword);
            //注册环信
            boolean flag = ImUtil.createUser(imusername,impassword,campusStudent.getName());
            if(!flag){
                return WebResult.error("注册环信失败");
            }
            //密码暂时不加密
//            campusStudent.setPassword(Md5Util.encode(campusStudent.getPassword()));
            //学校那边的数据出生日期格式为yyyyMMdd
            if(StringUtil.isNotEmpty(alumniUser.getCsrq())){
                alumniUser.setCsrq(alumniUser.getCsrq().replace("-",""));
            }
            alumniUser.setModifyTime(getCurrentTime());
            campusStudentService.save(campusStudent);
            alumniUser.setCampusStudent(campusStudent);
            alumniUserService.save(alumniUser);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 转向编辑页面
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/edit/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
    public String edit(Model model, @PathVariable String id) {
        AlumniUser alumniUser = alumniUserService.get(id);
        if (!ObjectUtil.isNotNull(alumniUser)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        model.addAttribute("alumniUser", alumniUser);
        List<CodeNation> nationList = commonCodeService.listNation();//民族
        List<CodeXslb> xslbList = commonCodeService.listXslb();//学院吧
        model.addAttribute("nation",nationList);
        model.addAttribute("xslb",xslbList);
        String[] xzArr = {"4","3","2","1","0.5"};//学制
        String[] xlArr = {"学士","无"};//学历
        model.addAttribute("xzArr",xzArr);
        model.addAttribute("xlArr",xlArr);
        model.addAttribute("yearRange",CommonCodeService.getYearRange());
        return TPL + "edit";
    }

    /**
     * 编辑操作
     * @param alumniUser
     * @param campusStudent
     * @return
     */
    @RequestMapping(value = "/edit/do", method =  {RequestMethod.POST})
    @ResponseBody
    public JSONObject doEdit(AlumniUser alumniUser,CampusStudent campusStudent) {
        try {
            //从数据库中获取最新数据
            AlumniUser eqAlumniUser = alumniUserService.get(alumniUser.getId());
            if (ObjectUtil.isNull(eqAlumniUser)) {
                return WebResult.error("非法操作");
            }
            //学校那边的数据出生日期格式为yyyyMMdd
            if(StringUtil.isNotEmpty(alumniUser.getCsrq())){
                alumniUser.setCsrq(alumniUser.getCsrq().replace("-",""));
            }
            alumniUser.setModifyTime(getCurrentTime());
            CampusStudent campusStudent1 = eqAlumniUser.getCampusStudent();
            campusStudent.setId(campusStudent1.getId());
            campusStudent.setImusername(campusStudent1.getImusername());
            campusStudent.setImpassword(campusStudent1.getImpassword());
            //密码暂时不加密
//            campusStudent.setPassword(Md5Util.encode(campusStudent.getPassword()));
            campusStudentService.update(campusStudent);
            alumniUser.setCampusStudent(campusStudent);
            //TODO 设置需要修改的属性值
            alumniUserService.update(alumniUser);
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
    @RequestMapping(value = "/delete/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject delete(@PathVariable String id) {
        try {
            //删除相关的所有数据
            alumniUserService.del(id);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 查看详情
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String detail(Model model, @PathVariable String id) {
        AlumniUser alumniUser = alumniUserService.get(id);
        if (!ObjectUtil.isNotNull(alumniUser)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        model.addAttribute("alumniUser", alumniUser);
        return TPL + "detail";
    }

    /**
     * 查重
     * @param id
     * @return
     */
    @RequestMapping(value = "/check/{id}")
    public String check(Model model,@PathVariable String id) {
        if(StringUtil.isEmpty(id)){
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        AlumniUser alumniUser = alumniUserService.get(id);
        if (!ObjectUtil.isNotNull(alumniUser)) {
            return ControllerCommon.UNAUTHORIZED_ACCESS;
        }
        //按照姓名，手机号，出生年月，email，入学年份，专业，六项中3项满足就视为同一人
        String name = alumniUser.getCampusStudent().getName();
        String mobile = alumniUser.getCampusStudent().getMobile();
        String email = alumniUser.getCampusStudent().getEmail();
        String csrq = alumniUser.getCsrq();
        if(StringUtil.isNotEmpty(csrq)){
            csrq = csrq.replace("-","");
        }
        String rxnf = alumniUser.getRxnf();
        String zyh = alumniUser.getZyh();
        String sql = "select t.* from (select a.id,b.name,a.xb,a.csrq,b.idcard,b.mobile,b.email,a.rxnf,a.zyh,a.zyfx,a.qq,a.wechat,a.industry," +
                "decodeSql from alumni_user a inner join campus_student b on a.student_id = b.id " +
                "whereSql) t where t.checkNum>2";
        String decodeSql = "";
        String whereSql = "";
        if(StringUtil.isNotEmpty(name)){
            decodeSql += "+decode(b.name,'" + name +"',1,0)";
            whereSql += "or b.name='"+name+"' ";
        }
        if(StringUtil.isNotEmpty(mobile)){
            decodeSql += "+decode(b.mobile,'" + mobile +"',1,0)";
            whereSql += "or b.mobile='"+mobile+"' ";
        }
        if(StringUtil.isNotEmpty(csrq)){
            decodeSql += "+decode(a.csrq,'" + csrq +"',1,0)";
            whereSql += "or a.csrq='"+csrq+"' ";
        }
        if(StringUtil.isNotEmpty(email)){
            decodeSql += "+decode(b.email,'" + email +"',1,0)";
            whereSql += "or b.email='"+email+"' ";
        }
        if(StringUtil.isNotEmpty(rxnf)){
            decodeSql += "+decode(a.rxnf,'" + rxnf +"',1,0)";
            whereSql += "or a.rxnf='"+rxnf+"' ";
        }
        if(StringUtil.isNotEmpty(zyh)){
            decodeSql += "+decode(a.zyh,'" + zyh +"',1,0)";
            whereSql += "or a.zyh='"+zyh+"' ";
        }
        if(decodeSql.startsWith("+")){
            decodeSql = decodeSql.substring(1);
        }
        if(whereSql.startsWith("or")){
            whereSql = whereSql.substring(2);
        }
        if(StringUtil.isEmpty(decodeSql)){
            decodeSql = "0 checkNum";
        }else{
            decodeSql = "("+decodeSql+") checkNum";
        }
        if(StringUtil.isNotEmpty(whereSql)){
            whereSql = "where "+whereSql;
        }
        sql = sql.replace("decodeSql",decodeSql).replace("whereSql",whereSql);
        List<Map<String, Object>> list = alumniUserService.queryForList(sql);
        model.addAttribute("list",list);
        model.addAttribute("id",id);
        return TPL + "check";
    }

    /**
     * 替换
     * @param id  基础ID
     * @param delIdArr  待删除的ID
     * @return
     */
    @RequestMapping(value = "/replace")
    @ResponseBody
    public JSONObject replace(@RequestParam("id") String id,@RequestParam("delIdArr[]") String[] delIdArr) {
        try{
            AlumniUser alumniUser = alumniUserService.get(id);
            List<AlumniUser> list = alumniUserService.get(delIdArr);
            if(alumniUser == null || list.isEmpty()){
                return WebResult.error("出错了");
            }
            //若基础数据中存在空值，则从待删除的数据拷贝
            CampusStudent campusStudent = alumniUser.getCampusStudent();
            CampusStudent campusStudent1 = null;
            AlumniUser alumniUser1 = null;
            List<CampusStudent> delStndentList = new ArrayList<CampusStudent>(delIdArr.length);
            for(int i = 0;i<list.size();i++){
                alumniUser1 = list.get(i);
                campusStudent1 = alumniUser1.getCampusStudent();
                delStndentList.add(campusStudent1);
                //姓名
                if(StringUtil.isEmpty(campusStudent.getName())){
                    if(StringUtil.isNotEmpty(campusStudent1.getName())){
                        campusStudent.setName(campusStudent1.getName());
                    }
                }
                //性别
                if(StringUtil.isEmpty(alumniUser.getXb())){
                    if(StringUtil.isNotEmpty(alumniUser1.getXb())){
                        alumniUser.setXb(alumniUser1.getXb());
                    }
                }
                //身份证号
                if(StringUtil.isEmpty(campusStudent.getIdcard())){
                    if(StringUtil.isNotEmpty(campusStudent1.getIdcard())){
                        campusStudent.setIdcard(campusStudent1.getIdcard());
                    }
                }
                //手机号
                if(StringUtil.isEmpty(campusStudent.getMobile())){
                    if(StringUtil.isNotEmpty(campusStudent1.getMobile())){
                        campusStudent.setMobile(campusStudent1.getMobile());
                    }
                }
                //邮箱
                if(StringUtil.isEmpty(campusStudent.getEmail())){
                    if(StringUtil.isNotEmpty(campusStudent1.getEmail())){
                        campusStudent.setEmail(campusStudent1.getEmail());
                    }
                }
                //出生日期
                if(StringUtil.isEmpty(alumniUser.getCsrq())){
                    if(StringUtil.isNotEmpty(alumniUser1.getCsrq())){
                        alumniUser.setCsrq(alumniUser1.getCsrq());
                    }
                }
                //入学年份
                if(StringUtil.isEmpty(alumniUser.getRxnf())){
                    if(StringUtil.isNotEmpty(alumniUser1.getRxnf())){
                        alumniUser.setRxnf(alumniUser1.getRxnf());
                    }
                }
                //专业号
                if(StringUtil.isEmpty(alumniUser.getZyh())){
                    if(StringUtil.isNotEmpty(alumniUser1.getZyh())){
                        alumniUser.setZyh(alumniUser1.getZyh());
                        alumniUser.setZyfx(alumniUser1.getZyfx());
                    }
                }
                //qq
                if(StringUtil.isEmpty(alumniUser.getQq())){
                    if(StringUtil.isNotEmpty(alumniUser1.getQq())){
                        alumniUser.setQq(alumniUser1.getQq());
                    }
                }
                //微信
                if(StringUtil.isEmpty(alumniUser.getWechat())){
                    if(StringUtil.isNotEmpty(alumniUser1.getWechat())){
                        alumniUser.setWechat(alumniUser1.getWechat());
                    }
                }
                //行业
                if(StringUtil.isEmpty(alumniUser.getIndustry())){
                    if(StringUtil.isNotEmpty(alumniUser1.getIndustry())){
                        alumniUser.setIndustry(alumniUser1.getIndustry());
                    }
                }
                //背景图片
                if(StringUtil.isEmpty(alumniUser.getBackgroundImage())){
                    if(StringUtil.isNotEmpty(alumniUser1.getBackgroundImage())){
                        alumniUser.setBackgroundImage(alumniUser1.getBackgroundImage());
                    }
                }
                //公司名称
                if(StringUtil.isEmpty(alumniUser.getCompanyName())){
                    if(StringUtil.isNotEmpty(alumniUser1.getCompanyName())){
                        alumniUser.setCompanyName(alumniUser1.getCompanyName());
                    }
                }
                //通信地址
                if(StringUtil.isEmpty(alumniUser.getAddress())){
                    if(StringUtil.isNotEmpty(alumniUser1.getAddress())){
                        alumniUser.setAddress(alumniUser1.getAddress());
                    }
                }
                //职位
                if(StringUtil.isEmpty(alumniUser.getPosition())){
                    if(StringUtil.isNotEmpty(alumniUser1.getPosition())){
                        alumniUser.setPosition(alumniUser1.getPosition());
                    }
                }
                //毕业年份
                if(StringUtil.isEmpty(alumniUser.getBynf())){
                    if(StringUtil.isNotEmpty(alumniUser1.getBynf())){
                        alumniUser.setBynf(alumniUser1.getBynf());
                    }
                }
                //签名
                if(StringUtil.isEmpty(alumniUser.getSignature())){
                    if(StringUtil.isNotEmpty(alumniUser1.getSignature())){
                        alumniUser.setSignature(alumniUser1.getSignature());
                    }
                }
            }
            campusStudentService.update(campusStudent);
            alumniUser.setCampusStudent(campusStudent);
            alumniUser.setModifyTime(getCurrentTime());
            alumniUserService.update(alumniUser);
            alumniUserService.del(delIdArr,delStndentList);
            return WebResult.success();
        }catch (Exception e){
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

}
