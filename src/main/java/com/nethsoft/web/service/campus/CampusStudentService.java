package com.nethsoft.web.service.campus;

import com.easemob.server.example.comm.Constants;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.BaseService;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class CampusStudentService  extends BaseService<CampusStudent>{

    /**
     * 获取注册校友的用户数量
     * @return
     */
    public int countAlumni(){
        return super.count(Restrictions.ne("imusername", Constants.DEFAULT_IMUSERNAME));//剔除掉通过Excel导入的
    }

    /**
     * 根据姓名，手机号，入学年份判断是否注册过,【校友使用】
     * @param name  姓名
     * @param mobile  手机号
     * @param rxnf  入学年份
     * @return
     */
    public boolean isRegistered(String name,String mobile,String rxnf){
        String sql = "select count(*) from campus_student a ,alumni_user b where a.id = b.student_id " +
                " and a.name ='"+name.trim()+"' and a.mobile = '"+mobile.trim()+"' and b.rxnf ='"+rxnf+"'";
        int count = super.queryForInt(sql);
        if(count>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 根据姓名，手机号，邮箱查找
     * @param name  姓名
     * @param mobile  手机号
     * @param email  邮箱
     * @return
     */
    public CampusStudent get(String name,String mobile,String email){
       return super.get(Restrictions.eq("name",name),
                Restrictions.eq("mobile",mobile),Restrictions.eq("email",email));
    }

    /**
     * 根据手机号判断是否已已注册过
     * @param mobile
     * @return
     */
    public boolean isRegistered(String mobile){
        int count =  super.count(Restrictions.eq("mobile",mobile.trim()));
        if(count>0){
            return true;
        }else{
            return false;
        }
    }

}
