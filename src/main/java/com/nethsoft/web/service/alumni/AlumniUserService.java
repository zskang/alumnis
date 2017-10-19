package com.nethsoft.web.service.alumni;

import com.easemob.server.example.comm.Constants;
import com.easemob.server.example.httpclient.apidemo.EasemobIMUsers;
import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.*;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.entity.campus.CampusXjb;
import com.nethsoft.web.service.BaseService;
import com.nethsoft.web.service.campus.CampusStudentService;
import com.nethsoft.web.service.campus.CampusXjbService;
import com.nethsoft.web.service.campus.CommonCodeService;
import com.nethsoft.web.support.AppUtil;
import com.nethsoft.web.support.ImUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.ParameterMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class AlumniUserService extends BaseService<AlumniUser> {
    private Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private CampusStudentService campusStudentService;
    @Autowired
    private CampusXjbService campusXjbService;
    @Autowired
    private CommonCodeService commonCodeService;


    public List<AlumniUser> listEntity(PageBean pageBean){
        List<AlumniUser> list = super.listByPage(pageBean);
        if(!list.isEmpty()){
            for(AlumniUser alumniUser : list){
                Hibernate.initialize(alumniUser.getCampusStudent());
            }
        }
        return list;
    }

    public List<String[]> listStringArray(PageBean pageBean,String type){
        List<AlumniUser> list = null;
        if("one".equals(type)){
            list = super.listByPage(pageBean);
        }else if("all".equals(type)){
            list = super.list(pageBean.getCriterionsArray());
        }else{
            list = new ArrayList<AlumniUser>();
        }
        List<String[]> result = new LinkedList<String[]>();
        if(!list.isEmpty()){
           String[] arr = null;
            for(AlumniUser alumniUser : list){
                Hibernate.initialize(alumniUser.getCampusStudent());
                arr = new String[]{alumniUser.getCampusStudent().getName(), alumniUser.getCampusStudent().getMobile(), alumniUser.getCampusStudent().getIdcard(),
                        alumniUser.getXb(), alumniUser.getMz(), alumniUser.getCsrq(), alumniUser.getQq(), alumniUser.getWechat(), alumniUser.getCampusStudent().getEmail(),
                        alumniUser.getRxnf(), alumniUser.getBynf(), alumniUser.getXymc(), alumniUser.getBm(), alumniUser.getXslb(), alumniUser.getZyfx(),
                        alumniUser.getXz(), alumniUser.getXw(),alumniUser.getIndustry(), alumniUser.getCompanyName(), alumniUser.getRegion(), alumniUser.getPosition()};
                result.add(arr);
            }
        }
        return result;
    }

    public AlumniUser get(String id){
        AlumniUser alumniUser = super.getById(id);
        if(alumniUser != null){
            Hibernate.initialize(alumniUser.getCampusStudent());
        }
        return alumniUser;
    }

    public AlumniUser getByStudentId(String studentId){
        AlumniUser alumniUser = super.get(Restrictions.eq("campusStudent.id",studentId));
        if(alumniUser != null){
            Hibernate.initialize(alumniUser.getCampusStudent());
        }
        return alumniUser;
    }

    public List<AlumniUser> get(String[] idArr){
        List<AlumniUser> list = super.list(Restrictions.in("id",idArr), Order.desc("modifyTime"));
        if(list != null && list.size()>0){
            for(AlumniUser alumniUser : list){
                Hibernate.initialize(alumniUser.getCampusStudent());
            }
        }
        return list;
    }

    public void del(String id){
        CampusStudent student = get(id).getCampusStudent();
        //Excel导入的不用删除
        if(!Constants.DEFAULT_IMUSERNAME.equals(student.getImusername())){
            //删除环信账号
            EasemobIMUsers.deleteIMUserByuserName(student.getImusername());
        }
        //        String studentId = student.getId();
//        super.executeSQL("delete from alumni_association_member where STUDENT_ID = ?", studentId);
//        super.executeSQL("delete from alumni_comment where STUDENT_ID = ?", studentId);
//        super.executeSQL("delete from alumni_enter where STUDENT_ID = ?", studentId);
//        super.executeSQL("delete from alumni_feedback where STUDENT_ID = ?", studentId);
//        super.executeSQL("delete from alumni_vote where STUDENT_ID = ?", studentId);
//        super.executeSQL("delete from alumni_donate where STUDENT_ID = ?", studentId);
//        super.delete(id);
//        super.executeSQL("delete from CAMPUS_STUDENT where id = ?", studentId);
        //调用存储过程删除
        ProcedureCall pro_test = super.baseDao.getSession().createStoredProcedureCall("pro_delete_user");
        pro_test.registerParameter(0,String.class, ParameterMode.IN).bindValue(id);
        ProcedureOutputs procedureOutputs = pro_test.getOutputs();
    }

    public void del(String[] idArr,List<CampusStudent> studentList){
        CampusStudent student = null;
        String studentsql = "";
        for(int i = 0;i<studentList.size();i++){
            student = studentList.get(i);
            if(i == 0){
                studentsql += " student_id='"+student.getId()+"'";
            }else{
                studentsql += " or student_id='"+student.getId()+"'";
            }

            //默认的环信账号，没注册，不用删除
            if(Constants.DEFAULT_IMUSERNAME.equals(student.getImusername()) &&
                    Constants.DEFAULT_PASSWORD.equals(student.getImpassword())){
                continue;
            }
            //循环删除环信账号
            EasemobIMUsers.deleteIMUserByuserName(student.getImusername());
        }
        String usersql = "";
        for(int i = 0;i<idArr.length;i++){
            if(i == 0){
                usersql += " id='"+idArr[i]+"'";
            }else{
                usersql += " or id='"+idArr[i]+"'";
            }
        }
        super.executeSQL("delete from alumni_association_member where "+studentsql);
        super.executeSQL("delete from alumni_comment where "+studentsql);
        super.executeSQL("delete from alumni_enter where "+studentsql);
        super.executeSQL("delete from alumni_feedback where "+studentsql);
        super.executeSQL("delete from alumni_vote where "+studentsql);
        super.executeSQL("delete from alumni_donate where "+studentsql);
        super.executeSQL("delete from alumni_user where "+usersql);
        studentsql = studentsql.replaceAll("student_id","id");
        super.executeSQL("delete from campus_student where "+studentsql);
    }

    /**
     * 插入Excel数据
     * @param file
     * @return
     * @throws Exception
     */
    public JSONObject insertExcel(MultipartFile file){
        try{
            POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            // 读取第一页的内容
            HSSFSheet sheet = wb.getSheetAt(0);
            String title = sheet.getRow(0).getCell(0).getRichStringCellValue().toString().trim();
            if(StringUtil.isEmpty(title) || !title.startsWith("校友信息")){
                return WebResult.error("请下载模板Excel导入数据！");
            }
            List<AlumniUser> list = new LinkedList<AlumniUser>();
            List<CampusStudent> studentList = new LinkedList<CampusStudent>();
            CampusStudent campusStudent = null;
            AlumniUser alumniUser = null;
            //学院数据
            List<CodeXsb> xsbList = super.getBaseDao().list(CodeXsb.class);
            //专业数据
            List<CodeZyb> zybList = super.getBaseDao().list(CodeZyb.class);
            HSSFRow row = null;
            StringBuilder sb = new StringBuilder();
            // 从数据行开始读取数据
            String name,mobile,email,csrq,rxnf,zyfx,zyh,xsh;//姓名,手机,邮箱,出生日期,入学年份,专业方向,专业号,系所号
            boolean flag;
            int validRow = 0;//有效的记录数
            List<HSSFRow> errorList = new LinkedList<HSSFRow>();
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                flag = true;
                row = sheet.getRow(i);
                if(row == null) continue;
                //确认一个人的6个成分必须填写，否则不给导入
                name = formatCell(row.getCell(0));
                if(StringUtil.isEmpty(name)){
                    sb.append("第"+(i+1)+"行,姓名不得为空;<br>");
                    flag = false;
                }
                mobile = formatCell(row.getCell(1));
                if(StringUtil.isEmpty(mobile)){
                    sb.append("第"+(i+1)+"行,手机号不得为空;<br>");
                    flag = false;
                }else if(!StringUtil.isMobile(mobile)){
                    sb.append("第"+(i+1)+"行,手机号格式不对;<br>");
                    flag = false;
                }
                email = formatCell(row.getCell(8));
                if(StringUtil.isEmpty(email)){
                    sb.append("第"+(i+1)+"行,邮箱不得为空;<br>");
                    flag = false;
                }else if(!StringUtil.isEmail(email)){
                    sb.append("第"+(i+1)+"行,邮箱格式不对;<br>");
                    flag = false;
                }
                csrq = formatCell(row.getCell(5));
                if(StringUtil.isEmpty(csrq)){
                    sb.append("第"+(i+1)+"行,出生日期不得为空;<br>");
                    flag = false;
                }
                csrq = csrq.replace("-","");
                rxnf = formatCell(row.getCell(9));
                if(StringUtil.isEmpty(rxnf)){
                    sb.append("第"+(i+1)+"行,出生日期不得为空;<br>");
                    flag = false;
                }
                zyfx = formatCell(row.getCell(14));
                if(StringUtil.isEmpty(zyfx)){
                    sb.append("第"+(i+1)+"行,专业不得为空;<br>");
                    flag = false;
                }
                zyh = getZyhByZym(zybList,zyfx);
                if(StringUtil.isEmpty(zyh)){
                    sb.append("第"+(i+1)+"行,找不到此专业;<br>");
                    flag = false;
                }
                xsh = getXshByXsm(xsbList,formatCell(row.getCell(11)));
                if(StringUtil.isEmpty(xsh)){
                    sb.append("第"+(i+1)+"行,学院为空或者找不到此学院;<br>");
                    flag = false;
                }
                if(!flag){
                    errorList.add(row);
                    continue;
                }

                campusStudent = new CampusStudent();
                campusStudent.setName(name);//姓名
                campusStudent.setMobile(mobile);//手机号
                campusStudent.setEmail(email);//邮箱
                campusStudent.setIdcard(formatCell(row.getCell(2)));//身份证号码
                campusStudent.setImusername(Constants.DEFAULT_IMUSERNAME);//默认环信账号
                campusStudent.setImpassword(Constants.DEFAULT_PASSWORD);//环信默认密码
                campusStudent.setPassword(Constants.DEFAULT_PASSWORD);//环信默认密码当做登录密码，暂不加密
                studentList.add(campusStudent);

                alumniUser = new AlumniUser();
                alumniUser.setXb(formatCell(row.getCell(3)));//性别
                alumniUser.setMz(formatCell(row.getCell(4)));//民族
                alumniUser.setCsrq(csrq);//出生日期
                alumniUser.setQq(formatCell(row.getCell(6)));//qq
                alumniUser.setWechat(formatCell(row.getCell(7)));//微信
                alumniUser.setRxnf(rxnf);//入学年份
                alumniUser.setBynf(formatCell(row.getCell(10)));//毕业年份
                alumniUser.setXsh(xsh);//系所号
                alumniUser.setBm(formatCell(row.getCell(12)));//班名
                alumniUser.setXslb(formatCell(row.getCell(13)));//学生类别
                alumniUser.setZyfx(zyfx);//专业方向
                alumniUser.setZyh(zyh);//专业号
                alumniUser.setXz(formatCell(row.getCell(15)));//学制
                alumniUser.setXw(formatCell(row.getCell(16)));//学位
                alumniUser.setIndustry(formatCell(row.getCell(17)));//行业
                alumniUser.setCompanyName(formatCell(row.getCell(18)));//单位名称
                alumniUser.setRegion(formatCell(row.getCell(19)));//通信地址
                alumniUser.setPosition(formatCell(row.getCell(20)));//校友会职位
                alumniUser.setModifyTime(getCurrentTime());
                list.add(alumniUser);
                validRow++;
            }
            String fileName = "";
            if(errorList.isEmpty()){
                campusStudentService.save(studentList);
                for(int j = 0;j<list.size();j++){
                    list.get(j).setCampusStudent(studentList.get(j));
                }
                super.save(list);
                return WebResult.success();
            }else{
                if(validRow == 0){//都是无效的记录
                    if(StringUtil.isEmpty(sb)){
                        return WebResult.error("导入失败，请检查数据。");
                    }else{
                        return WebResult.error(sb.toString());
                    }
                }else{
                    fileName = createErrorExcel(errorList);
                    return WebResult.error(sb.toString()).element("fileName",fileName);
                }
            }
        }catch (Exception e){
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 生成错误表格
     * @param list  填写不正确的数据
     * @return
     */
    public String createErrorExcel(List<HSSFRow> list){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName =  sdf.format(new Date())+"error.xls";
        String upload_root = ApplicationCoreConfigHelper.getProperty("upload.folder.root");
        String tempPath = getRequest().getServletContext().getRealPath(upload_root+"alumni"+ File.separator+fileName);
        String templatePath = getRequest().getServletContext().getRealPath(upload_root+"alumni"+ File.separator+"alumni.xls");
        OutputStream os = null;
        try{
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(templatePath)));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if(list != null && list.size()>0){
                HSSFRow toRow = null;
                HSSFRow fromRow = null;
                for(int i = 0;i<list.size();i++){
                    toRow = sheet.createRow(i+2);
                    fromRow = list.get(i);
                    copyRow(fromRow,toRow);
                }
            }
            os = new FileOutputStream(new File(tempPath));
            wb.write(os);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(os != null){
                try{
                    os.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return fileName;
        }
    }

    /**
     * 复制行
     * @param fromRow
     * @param toRow
     */
    public static void copyRow(HSSFRow fromRow,HSSFRow toRow){
        for (Iterator cellIt = fromRow.cellIterator(); cellIt.hasNext();) {
            HSSFCell tmpCell = (HSSFCell) cellIt.next();
            HSSFCell newCell = toRow.createCell(tmpCell.getCellNum());
            copyCell(tmpCell, newCell);
        }
    }

    /**
     * 复制单元格
     * @param srcCell
     * @param distCell
     */
    public static void copyCell(HSSFCell srcCell, HSSFCell distCell) {
        // 不同数据类型处理
        int srcCellType = srcCell.getCellType();
        distCell.setCellType(srcCellType);
            if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
                if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
                    distCell.setCellValue(srcCell.getDateCellValue());
                } else {
                    distCell.setCellValue(srcCell.getNumericCellValue());
                }
            } else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
                distCell.setCellValue(srcCell.getRichStringCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {

            } else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
                distCell.setCellValue(srcCell.getBooleanCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
                distCell.setCellErrorValue(srcCell.getErrorCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
                distCell.setCellFormula(srcCell.getCellFormula());
            } else {
            }
    }

    /**
     * 根据系所名查找系所号
     * @param list
     * @param name
     * @return
     */
    private String getXshByXsm(List<CodeXsb> list,final String name){
        if(StringUtil.isEmpty(name)){
            return "";
        }
        CodeXsb codeXsb = (CodeXsb)CollectionUtils.find(list, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                CodeXsb codeXsb = (CodeXsb)o;
                return codeXsb.getXsm().equals(name);
            }
        });
        if(codeXsb == null){
            return "";
        }else{
            return codeXsb.getXsh();
        }
    }

    /**
     * 根据专业名称查找专业号
     * @param list
     * @param name
     * @return
     */
    private String getZyhByZym(List<CodeZyb> list,final String name){
        if(StringUtil.isEmpty(name)){
            return "";
        }
        CodeZyb codeZyb = (CodeZyb)CollectionUtils.find(list, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                CodeZyb codeZyb = (CodeZyb)o;
                return codeZyb.getZym().equals(name);
            }
        });
        if(codeZyb == null){
            return "";
        }else{
            return codeZyb.getZyh();
        }
    }


    private static String formatCell(HSSFCell cell) {
        String result = "";
        if (cell != null) {
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_NUMERIC:// 数字类型
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                        short format = cell.getCellStyle().getDataFormat();
                        SimpleDateFormat sdf = null;
                        if (format == 14 || format == 31 || format == 57
                                || format == 58) {
                            // 日期
                            sdf = new SimpleDateFormat("yyyy-MM-dd");
                        } else if (format == 20 || format == 32) {
                            // 时间
                            sdf = new SimpleDateFormat("HH:mm");
                        } else {
                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        }
                        Date date = cell.getDateCellValue();
                        result = sdf.format(date);
                    } else {
                        double value = cell.getNumericCellValue();
                        CellStyle style = cell.getCellStyle();
                        DecimalFormat format = new DecimalFormat();
                        String temp = style.getDataFormatString();
                        // 单元格设置成常规
                        if (temp.equals("General")) {
                            format.applyPattern("#");
                        }
                        result = format.format(value);
                    }
                    break;
                case HSSFCell.CELL_TYPE_STRING:// String类型
                    result = cell.getRichStringCellValue().toString().trim();
                    break;
                case HSSFCell.CELL_TYPE_BLANK:
                    result = "";
                default:
                    result = "";
                    break;
            }
        }
        return result;
    }

    public AlumniUser copyXj2User(CampusXjb campusXjb){
        CampusStudent campusStudent = new CampusStudent();
        String idcard = campusXjb.getSfzh();
        campusStudent.setIdcard(idcard);
        campusStudent.setCode(campusXjb.getXh());
        campusStudent.setName(campusXjb.getXm());
        campusStudent.setImusername(AppUtil.makeOrderNum());//生成环信用户名
        //身份证号后6位作为环信密码,没有的话使用环信默认密码
        campusStudent.setImpassword(StringUtil.isEmpty(idcard)?Constants.DEFAULT_PASSWORD:idcard.substring(idcard.length()-6));
        //身份证号后6位作为登录密码，没有的话使用环信默认密码当做登录密码
        campusStudent.setPassword(campusStudent.getImpassword());
        AlumniUser alumniUser = new AlumniUser();
        alumniUser.setModifyTime(getCurrentTime());
        try{
            BeanUtils.copyProperties(alumniUser,campusXjb);
        }catch (Exception e){
            e.printStackTrace();
        }
        alumniUser.setCampusStudent(campusStudent);
        return alumniUser;
    }

    /**
     * 用户列表
     * @param pageBean
     * @return
     */
    public JSONArray find(PageBean pageBean){
        List<AlumniUser> list = super.listByPage(pageBean);
        JSONArray jsonArray = new JSONArray();
        if(!list.isEmpty()){
            JSONObject jsonObject = null;
            CampusStudent student = null;
            String name = "";
            for(AlumniUser alumniUser : list){
                jsonObject = new JSONObject();
                student = alumniUser.getCampusStudent();
                Hibernate.initialize(student);
                name = student.getName();
                if(StringUtil.isNotEmpty(alumniUser.getRxnf())){
                    name += "("+alumniUser.getRxnf()+"级";
                    if(StringUtil.isNotEmpty(alumniUser.getZyfx())){
                        name += "-"+alumniUser.getZyfx();
                    }
                    name += ")";
                }
                jsonObject.element("id",student.getId()).element("name",name).element("imusername",student.getImusername())
                        .element("nickName",StringUtil.null2String(student.getNickname()))
                        .element("photo",StringUtil.null2String(student.getPhoto()))
                        .element("signature",StringUtil.null2String(alumniUser.getSignature()));
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 注册
     * @param name  姓名
     * @param mobile  手机号
     * @param rxnf  入学年份
     * @param zyh  专业号
     * @param zyfx  专业方向
     * @param email  邮箱
     * @param password  密码
     * @return
     */
    public JSONObject executeRegister(String name,String mobile,String rxnf,String email,String password,String zyh,String zyfx){
        //根据姓手机号检测此用户是否已注册过
        if(campusStudentService.isRegistered(mobile)){
            return WebResult.error("该手机号已注册过,请使用其他手机号注册或使用忘记密码功能找回密码登陆");
        }
        //根据姓名，手机号，入学年份检测此用户是否已注册过
//        if(campusStudentService.isRegistered(name,mobile,rxnf)){
//            return WebResult.error("该用户已注册过");
//        }
        //到学籍表中查找此人
        List<CampusXjb> campusXjbList = campusXjbService.list(Restrictions.eq("xm",name),Restrictions.eq("rxnj",rxnf),Restrictions.eq("zyh",zyh));
        AlumniUser alumniUser = null;
        CampusStudent campusStudent = null;
        String currentTime = super.getCurrentTime();
        if(campusXjbList.isEmpty()){//学籍中找不到此人
            alumniUser = new AlumniUser();
            alumniUser.setModifyTime(getCurrentTime());
            //根据姓名，手机，邮箱查询用户
            campusStudent = campusStudentService.get(name,mobile,email);
            if(campusStudent == null){//库中没有此用户
                //生成环信账号
                String imusername = AppUtil.makeOrderNum();
                //环信密码
                String impassword = Constants.DEFAULT_PASSWORD;
                campusStudent = new CampusStudent(name,mobile,email,password,imusername,impassword,currentTime);
                //注册环信
                boolean flag = ImUtil.createUser(imusername,impassword,name);
                if(!flag){
                    return WebResult.error("注册环信失败");
                }
                //根据姓手机号检测此用户是否已注册过
                if(campusStudentService.isRegistered(mobile)){
                    return WebResult.error("该手机号已注册过,请使用其他手机号注册或使用忘记密码功能找回密码登陆");
                }
                campusStudentService.save(campusStudent);
            }else{//库中存在此用户
                campusStudent.setRegistertime(currentTime);
                campusStudentService.update(campusStudent);
                if(!campusStudent.getPassword().equals(password)){
                    campusStudent.setPassword(password);
                    campusStudentService.update(campusStudent);
                }
            }
            alumniUser.setCampusStudent(campusStudent);
        }else{//此人存在于学籍表中
            alumniUser = copyXj2User(campusXjbList.get(0));//取第一个复制
            campusStudent = alumniUser.getCampusStudent();
            //根据学号查询用户
            CampusStudent campusStudentEq = campusStudentService.getByProperties("code",campusStudent.getCode());
            if(campusStudentEq == null) {//库中没有此用户
                campusStudent.setPassword(password);
//		campusStudent.setPassword(Md5Util.encode(password));
                campusStudent.setMobile(mobile);
                campusStudent.setEmail(email);
                campusStudent.setRegistertime(currentTime);
                //注册环信
                boolean flag = ImUtil.createUser(campusStudent.getImusername(),campusStudent.getImpassword(),campusStudent.getName());
                if(!flag){
                    return WebResult.error("注册环信失败");
                }
                //根据姓手机号检测此用户是否已注册过
                if(campusStudentService.isRegistered(mobile)){
                    return WebResult.error("该手机号已注册过,请使用其他手机号注册或使用忘记密码功能找回密码登陆");
                }
                campusStudentService.save(campusStudent);
                alumniUser.setCampusStudent(campusStudent);
            }else{//库中存在此用户
                campusStudentEq.setMobile(mobile);
                campusStudentEq.setEmail(email);
                campusStudentEq.setPassword(password);
//		campusStudentEq.setPassword(Md5Util.encode(password));
                campusStudentEq.setRegistertime(currentTime);
                campusStudentService.update(campusStudentEq);
                alumniUser.setCampusStudent(campusStudentEq);
            }
        }
        alumniUser.setRxnf(rxnf);
        alumniUser.setZyh(zyh);
        alumniUser.setZyfx(zyfx);
        super.save(alumniUser);
        return WebResult.success();
    }

    /**
     * 登录
     * @param campusStudent
     * @return
     */
    public JSONObject executeLogin(CampusStudent campusStudent){
        //此处针对于Excel导入的用户，登录时生成环信账号
        if(Constants.DEFAULT_IMUSERNAME.equals(campusStudent.getImusername()) &&
                Constants.DEFAULT_PASSWORD.equals(campusStudent.getImpassword())){
            AlumniUser alumniUser = super.getByProperties("campusStudent.id",campusStudent.getId());
            //到学籍表中查找此人
            List<CampusXjb> campusXjbList = campusXjbService.list(Restrictions.eq("xm",campusStudent.getName()),
                    Restrictions.eq("rxnj",alumniUser.getRxnf()),Restrictions.eq("zyh",alumniUser.getZyh()));
            //注册环信
            String imusername = AppUtil.makeOrderNum();
            String impassword = "";
            if(campusXjbList == null || campusXjbList.isEmpty()){//学籍中不存在此人
                impassword = Constants.DEFAULT_PASSWORD;
            }else{//取第一个
                String idcard = campusXjbList.get(0).getSfzh();
                impassword = StringUtil.isEmpty(idcard)?Constants.DEFAULT_PASSWORD:idcard.substring(idcard.length()-6);
            }
            boolean flag = ImUtil.createUser(imusername,impassword,campusStudent.getName());
            if(!flag){
                return WebResult.error("注册环信失败");
            }
            //更新环信账号
            campusStudent.setImusername(imusername);
            campusStudent.setImpassword(impassword);
        }
        campusStudentService.update(campusStudent);
        AlumniUser alumniUser = super.get(Restrictions.eq("campusStudent",campusStudent));
        List<String> tagList = new LinkedList<String>();//用户的标签，用于推送
        if(StringUtil.isNotEmpty(alumniUser.getRxnf())){
            tagList.add("rxnf_"+alumniUser.getRxnf());//入学年份标签
        }
        if(StringUtil.isNotEmpty(alumniUser.getZyh())){
            tagList.add("major_"+alumniUser.getZyh());//专业标签
        }
        if(StringUtil.isNotEmpty(alumniUser.getIndustry())){
            CodeIndustry industry = commonCodeService.findIndustryByMc(alumniUser.getIndustry());
            if(industry != null){
                tagList.add("industry_"+industry.getId());//行业标签
            }
        }
        if(StringUtil.isNotEmpty(alumniUser.getRegion())){
            CodeDm codeDm = commonCodeService.findRegionByQc(alumniUser.getRegion());
            if(codeDm != null){
                tagList.add("region_"+codeDm.getId());//辖区标签
            }
        }
        JSONObject userInfo = new JSONObject();
        userInfo.element("token",campusStudent.getId()).element("name",campusStudent.getName())
                .element("imusername",campusStudent.getImusername())
                .element("impassword",campusStudent.getImpassword())
                .element("photo",StringUtil.null2String(campusStudent.getPhoto()))
                .element("backgroundimage",StringUtil.null2String(alumniUser.getBackgroundImage()))
                .element("signature",StringUtil.null2String(alumniUser.getSignature()))
                .element("nickname",StringUtil.null2String(campusStudent.getNickname()))
                .element("tags",tagList);
        return WebResult.success().element("userInfo",userInfo);
    }

}
