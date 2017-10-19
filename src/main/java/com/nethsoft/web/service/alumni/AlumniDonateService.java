package com.nethsoft.web.service.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.AlumniDonate;
import com.nethsoft.web.entity.alumni.AlumniDonateProject;
import com.nethsoft.web.entity.alumni.AlumniUser;
import com.nethsoft.web.entity.alumni.CodeXsb;
import com.nethsoft.web.entity.campus.CampusStudent;
import com.nethsoft.web.service.BaseService;
import com.nethsoft.web.service.campus.CommonCodeService;
import com.nethsoft.web.support.alumni.ExtractUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AlumniDonateService extends BaseService<AlumniDonate> {

    @Autowired
    private AlumniUserService alumniUserService;
    @Autowired
    private CommonCodeService commonCodeService;

    public List<AlumniDonate> find(PageBean pageBean){
        List<AlumniDonate> list = super.listByPage(pageBean);
        if(list != null && list.size()>0){
            for(AlumniDonate alumniDonate : list){
                Hibernate.initialize(alumniDonate.getStudent());
                Hibernate.initialize(alumniDonate.getDonateProject());
            }
        }
        return list;
    }

    public JSONObject getDonateInfo(String id){
        AlumniDonate alumniDonate = super.getById(id);
        JSONObject jsonObject = new JSONObject();
        if (alumniDonate != null) {
            CampusStudent campusStudent = alumniDonate.getStudent();
            Hibernate.initialize(campusStudent);
            AlumniDonateProject donateProject = alumniDonate.getDonateProject();
            Hibernate.initialize(donateProject);
            AlumniUser alumniUser = alumniUserService.getByProperties("campusStudent.id", campusStudent.getId());
            jsonObject.element("date",alumniDonate.getCreateTime()).element("name",campusStudent.getName())
                    .element("project",donateProject.getName()).element("bynf", StringUtil.isEmpty(alumniUser.getBynf())?"":alumniUser.getBynf())
                    .element("xymc",StringUtil.isEmpty(alumniUser.getXymc())?"":alumniUser.getXymc())
                    .element("address",StringUtil.isEmpty(alumniUser.getRegion())?"":alumniUser.getRegion())
                    .element("money",alumniDonate.getMoney()).element("way",alumniDonate.getWay())
                    .element("teamName",StringUtil.isEmpty(alumniDonate.getTeamName())?"":alumniDonate.getTeamName());
        }
        return jsonObject;
    }

    private JSONArray findBySql(String sql, int page, int rows,Map<String,Object> param){
        if (page <= 1){
            page = 0;
        }else{
            page = (page-1)*rows;
        }
        Query query = super.getBaseDao().getSQLQuery(sql).setFirstResult(page).setMaxResults(rows);
        if(!param.isEmpty()){
           for(String key : param.keySet()){
               if(key.equals("xshArr")){
                   if(param.get(key).getClass().isArray()){
                       query.setParameterList(key,(String[])param.get(key));
                   }
               }else{
                   query.setString(key,param.get(key).toString());
               }
           }
        }
        JSONArray jsonArray = new JSONArray();
        List<Object[]> list = query.list();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            for(Object[] objects :list){
                jsonObject = new JSONObject();
                jsonObject.element("id",objects[0]);
                jsonObject.element("date",objects[1]);
                jsonObject.element("name",objects[2]);
                jsonObject.element("money",objects[3]);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 捐赠动态
     * @param pageBean
     * @param year  入学年份
     * @param field  查询的属性名称
     * @param value  查询的名称对应的值
     */
    public JSONArray extractDynamics(PageBean pageBean,String year,String field, String value) throws Exception{
        String[] year_name = {"2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016"};
        String[] year_value = {"81","88","82","90","86","85","84","83","87","89","5780","5820","5860","5880"};
        Map<String,String> param = new HashMap<String, String>();
        if(StringUtil.isNotEmpty(year)){
            int index = ArrayUtils.indexOf(year_name,year);
            param.put("year",year_value[index]);
        }
        if(StringUtil.isNotEmpty(field) && StringUtil.isNotEmpty(value)){
            param.put("field",field);
            param.put("value",value);
        }
        param.put("pagingNumberPer",String.valueOf(pageBean.getRows()));
        param.put("pagingPage",String.valueOf(pageBean.getPage()));
        return ExtractUtil.listDynamics("http://alumni.ustb.edu.cn/listPersonalDonate.do",param);
    }


    /**
     * 捐赠动态
     * @param pageBean
     * @param rxnf  入学年份
     * @param name  查询的属性名称
     * @param value  查询的名称对应的值
     */
    public JSONArray dynamics(PageBean pageBean,String rxnf,String name, String value) throws Exception{
        String sql = "select a.id,a.create_time,b.name,a.money from alumni_donate a,campus_student b,alumni_user c " +
                "where a.student_id = b.id and b.id = c.student_id ";
        Map<String,Object> param = new HashMap<String,Object>();
        if(StringUtil.isNotEmpty(rxnf)){
            sql += "and c.rxnf=:rxnf ";
            param.put("rxnf",rxnf);
        }
        if(StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(value)){
            switch (name){
                case "name"://姓名
                    sql += "and b.name like :name ";
                    param.put(name,"%"+value+"%");
                    break;
                case "region"://地区
                    sql += "and c.region like :region ";
                    param.put(name,"%"+value+"%");
                    break;
                case "rxrq"://入学日期
                    sql += "and c.rxrq = :rxrq ";
                    param.put(name,value);
                    break;
                case "byrq"://毕业日期
                    sql += "and c.byrq = :byrq ";
                    param.put(name,value);
                    break;
                case "xsm"://毕业院系
                    List<CodeXsb> list = commonCodeService.listCodeXsb(value);
                    if(list != null && list.size()>0){
                        String[] arr = new String[list.size()];
                        for(int i = 0;i<list.size();i++){
                            arr[i] = list.get(i).getXsh();
                        }
                        sql += "and c.xsh in (:xshArr) ";
                        param.put("xshArr",arr);
                    }
                    break;
                case "money"://金额
                    sql += "and a.money = :money ";
                    param.put(name,value);
                    break;
                case "teamName"://集体名称
                    sql += "and a.team_name like :teamName ";
                    param.put(name,"%"+value+"%");
                    break;
                case "way"://捐款方式
                    sql += "and a.way = :way ";
                    param.put(name,value);
                    break;
            }
        }
        return findBySql(sql,pageBean.getPage(),pageBean.getRows(),param);
    }

    private JSONArray findByHql(String hql,int page,int rows){
        if (page <= 1){
            page = 0;
        }else{
            page = (page-1)*rows;
        }
        Query query = super.getBaseDao().getQuery(hql).setFirstResult(page).setMaxResults(rows);
        JSONArray jsonArray = new JSONArray();
        List<Object[]> list = query.list();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            for(Object[] objects :list){
                jsonObject = new JSONObject();
                jsonObject.element("name",objects[0]);
                jsonObject.element("money",objects[1]);
                jsonObject.element("date",objects[2]);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 捐赠统计，按姓名
     * @param pageBean
     * @param startDate  开始日期
     * @param endDate  结束日期
     * @return
     */
    public Map<String,Object> statistic(PageBean pageBean,String startDate, String endDate){
        Map<String,Object> result = new HashMap<String,Object>();
        String countSql = "";
        String hql = "select student.name,sum(money),max(createTime) from AlumniDonate where 1=1 ";
        countSql = "select count(*) from (select student_id from alumni_donate  where 1=1 ";
        String sumHql = "select sum(money) from AlumniDonate where 1=1 ";
        if (StringUtil.isNotEmpty(startDate)) {
            startDate = startDate +" 00:00:00";
            hql += " and createTime>='" + startDate + "'";
            countSql += " and create_time>='" + startDate + "'";
            sumHql += " and createTime>='" + startDate + "'";
        }
        if (StringUtil.isNotEmpty(endDate)) {
            endDate = endDate +" 23:59:59";
            hql += " and createTime<='" + endDate + "'";
            countSql += " and create_time<='" + endDate + "'";
            sumHql += " and createTime<='" + endDate + "'";
        }
        hql += " group by student.name";
        countSql += " group by student_id ) a";
        JSONArray jsonArray = findByHql(hql, pageBean.getPage(), pageBean.getRows());
        int total = super.queryForInt(countSql);
        Object sum = super.queryHQL(sumHql);
        pageBean.setTotalCount(total);
        result.put("list",jsonArray);
        result.put("sum",sum);
        return result;
    }

    public AlumniDonate get(String id){
        AlumniDonate alumniDonate = super.getById(id);
        if(alumniDonate != null){
            Hibernate.initialize(alumniDonate.getStudent());
        }
        return alumniDonate;
    }

}
