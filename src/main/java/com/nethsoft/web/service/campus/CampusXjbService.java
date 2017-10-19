package com.nethsoft.web.service.campus;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.campus.CampusXjb;
import com.nethsoft.web.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampusXjbService extends BaseService<CampusXjb> {

    /**
     * 查询学籍信息
     * @param pageBean
     * @param xm  姓名
     * @param csrq  出生日期
     * @param rxnj 入学年级
     * @param zyfx  专业方向
     * @param bm  班名
     * @return
     */
    public JSONArray find(PageBean pageBean,String xm, String csrq, String rxnj, String zyfx, String bm){
        String countSql = "select count(*) from campus_xjb where 1=1 ";
        String hql = "select xm,xb,csrq,sfzh,rxnj,zyfx,bm from CampusXjb where 1=1 ";
        if(StringUtil.isNotEmpty(xm)){
            hql += " and xm like '%"+xm+"%'";
            countSql += " and xm like '%"+xm+"%'";
        }
        if(StringUtil.isNotEmpty(csrq)){
            csrq = csrq.replace("-","");
            hql += " and csrq = '"+csrq+"'";
            countSql += " and csrq = '"+csrq+"'";
        }
        if(StringUtil.isNotEmpty(rxnj)){
            hql += " and rxnj = '"+rxnj+"'";
            countSql += " and rxnj = '"+rxnj+"'";
        }
        if(StringUtil.isNotEmpty(zyfx)){
            hql += " and zyfx = '"+zyfx+"'";
            countSql += " and zyfx = '"+zyfx+"'";
        }
        if(StringUtil.isNotEmpty(bm)){
            hql += " and bm = '"+bm+"'";
            countSql += " and bm = '"+bm+"'";
        }
        int page = pageBean.getPage();
        int rows = pageBean.getRows();
        if (page <= 1){
            page = 0;
        }else{
            page = (page-1)*rows;
        }
        int count = super.queryForInt(countSql);
        pageBean.setTotalCount(count);
        List<Object[]> list = super.baseDao.getQuery(hql).setFirstResult(page).setMaxResults(rows).list();
        JSONArray jsonArray = new JSONArray();
        if(!list.isEmpty()){
            JSONObject jsonObject = new JSONObject();
            for(Object[] objects : list){
                jsonObject.element("xm",objects[0]).element("xb",objects[1]).element("csrq",objects[2])
                        .element("sfzh",objects[3]).element("rxnj",objects[4]).element("zyfx",objects[5])
                        .element("bm",objects[6]);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

}
