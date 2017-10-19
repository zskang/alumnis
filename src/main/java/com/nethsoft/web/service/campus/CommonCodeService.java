package com.nethsoft.web.service.campus;

import com.nethsoft.core.support.ApplicationCommon;
import com.nethsoft.core.util.DateUtil;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.entity.alumni.*;
import com.nethsoft.web.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.collections.CollectionUtils.select;


@Service
public class CommonCodeService extends BaseService {

    /**
     * 根据名称查找行业
     * @param mc
     * @return
     */
    public CodeIndustry findIndustryByMc(String mc){
        return super.getBaseDao().getBy(CodeIndustry.class,Restrictions.eq("mc",mc));
    }

    /**
     * 根据全称查找辖区
     * @param qc
     * @return
     */
    public CodeDm findRegionByQc(String qc){
        return super.getBaseDao().getBy(CodeDm.class,Restrictions.eq("qc",qc));
    }

    /**
     * 行业树
     * @return
     */
    public JSONArray industryTree() {
        List<CodeIndustry> list = super.getBaseDao().list(CodeIndustry.class,"select a from CodeIndustry a order by length(a.id) desc,a.id asc");
        JSONArray data = new JSONArray();
        List<JSONObject> children = null;
        JSONObject jsonObject = null;
        for(CodeIndustry codeIndustry : list){
            jsonObject = new JSONObject();
            final String id = codeIndustry.getId();
            jsonObject.element("id",id);
            jsonObject.element("text",codeIndustry.getMc());
            if(id.length()==2){
                children = (List<JSONObject>)select(data, new Predicate() {
                    @Override
                    public boolean evaluate(Object o) {
                        JSONObject obj = (JSONObject)o;
                        String _id = obj.get("id").toString();
                        return _id.startsWith(id) && _id.length()>id.length();
                    }
                });
                if(children != null && children.size()>0){
                    jsonObject.element("children",children);
                    data.removeAll(children);
                }
            }else{
                jsonObject.element("icon","fa fa-file");
            }
            data.add(jsonObject);
        }
        return data;
    }

    /**
     * 获取专业数据
     * @return
     */
    public CodeZyb getMajor(String zyh){
        if(StringUtil.isEmpty(zyh)){
            return null;
        }
        return super.getBaseDao().get(CodeZyb.class,zyh);
    }

    /**
     * 根据系所名查询
     * @param xsm  系所名
     * @return
     */
    public List<CodeXsb> listCodeXsb(String xsm){
        if(StringUtil.isEmpty(xsm)){
            return null;
        }
        return super.getBaseDao().list(CodeXsb.class, Restrictions.like("xsm",xsm.trim(), MatchMode.ANYWHERE));
    }


    /**
     * 获取专业数据
     * @return
     */
    public List<CodeZyb> listMajor(){
        List<CodeZyb> list = super.getBaseDao().list(CodeZyb.class,Order.asc("zyh"));
        for(CodeZyb codeZyb : list){
            Hibernate.initialize(codeZyb.getXsb());
        }
        return list;
    }

    /**
     * 构建学院树
     * @return
     */
    public JSONArray collageTree() {
        List<CodeXsb> xsbList = super.getBaseDao().list(CodeXsb.class,Order.asc("xsh"));
        JSONArray data = new JSONArray();
        JSONObject root;
        for(CodeXsb codeXsb : xsbList){
            root = new JSONObject();
            root.element("id",codeXsb.getXsh());
            root.element("text",codeXsb.getXsm());
            data.add(root);
        }
        return data;
    }


    /**
     * 构建专业树
     * @return
     */
    public JSONArray majorTree() {
        List<CodeXsb> xsbList = super.getBaseDao().list(CodeXsb.class,Order.asc("xsh"));
        List<CodeZyb> zybList = listMajor();
        JSONArray data = new JSONArray();
        JSONObject root;
        JSONObject leaf;
        JSONArray children;
        List<CodeZyb> tempList = null;
        for(final CodeXsb codeXsb : xsbList){
            root = new JSONObject();
            root.element("id",codeXsb.getXsh());
            root.element("text",codeXsb.getXsm());
            tempList = (List<CodeZyb>) select(zybList, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    CodeZyb zyb = (CodeZyb)o;
                    return zyb.getXsb().getXsh().equals(codeXsb.getXsh());
                }
            });
            if(tempList != null && tempList.size()>0){
                children = new JSONArray();
                for(CodeZyb codeZyb : tempList){
                    leaf = new JSONObject();
                    leaf.element("id",codeZyb.getZyh());
                    leaf.element("text",codeZyb.getZym());
                    leaf.element("icon","fa fa-file");
                    children.add(leaf);
                }
                root.element("children",children);
            }
            data.add(root);
        }
        return data;
    }

    /**
     * 获取民族数据
     * @return
     */
    public List<CodeNation> listNation(){
        return super.getBaseDao().list(CodeNation.class);
    }

    /**
     * 获取学生类型
     * @return
     */
    public List<CodeXslb> listXslb(){
        return super.getBaseDao().list(CodeXslb.class);
    }

    /**
     * 查询班级数据
     * @param pageBean
     * @return
     */
    public List<CodeClass> listClass(PageBean pageBean){
        List<CodeClass> list = (List<CodeClass>) ApplicationCommon.CACHEPROVIDER.get("class");
        if(list == null){
            setTotalCount(pageBean,CodeClass.class);
            list = baseDao.list(CodeClass.class,pageBean);
            ApplicationCommon.CACHEPROVIDER.put("class",list);
        }
        return list;
    }

    protected void setTotalCount(PageBean pageBean,Class clazz) {
        if(ObjectUtil.isNotNull(pageBean.getCriterions()) && ObjectUtil.isNotEmpty(pageBean.getCriterions()))
            pageBean.setTotalCount(baseDao.count(clazz,pageBean.getCriterions().toArray(new Criterion[]{})));
        else
            pageBean.setTotalCount(baseDao.count(clazz));
    }

    /**
     * 辖区树
     * @return
     */
    public List<JSONObject> regionTree() {
        List<JSONObject> result = (List<JSONObject>) ApplicationCommon.CACHEPROVIDER.get("region");
        if(result == null){
            List<CodeDm> list = super.getBaseDao().list(CodeDm.class,Order.asc("id"));
            JSONObject jsonObject;
            result = new LinkedList<JSONObject>();
            List<JSONObject> secondlevel = new LinkedList<JSONObject>();
            List<JSONObject> thirdlevel = new LinkedList<JSONObject>();
            List<JSONObject> children = null;
            for(final CodeDm codeDm : list){
                jsonObject = new JSONObject();
                jsonObject.element("id",codeDm.getId());
                jsonObject.element("text",codeDm.getMc());
                jsonObject.element("qc",codeDm.getQc());
                jsonObject.element("xh",codeDm.getXh());
                if(codeDm.getXh().length()==6){
                    jsonObject.element("icon","fa fa-file");
                    thirdlevel.add(jsonObject);
                }else if(codeDm.getXh().length()==4){
                    secondlevel.add(jsonObject);
                }else{
                    result.add(jsonObject);
                }
            }
            for(final JSONObject secondObj : secondlevel){
                children = (List<JSONObject>)select(thirdlevel, new Predicate() {
                    @Override
                    public boolean evaluate(Object o) {
                        String xh = ((JSONObject)o).get("xh").toString();
                        return xh.substring(0,4).equals(secondObj.getString("xh"));
                    }
                });
                if(children != null && children.size()>0){
                    secondObj.element("children",children);
                }
            }
            thirdlevel = null;
            for(final JSONObject firstObj : result){
                children = (List<JSONObject>)select(secondlevel, new Predicate() {
                    @Override
                    public boolean evaluate(Object o) {
                        String xh = ((JSONObject)o).get("xh").toString();
                        return xh.substring(0,2).equals(firstObj.getString("xh"));
                    }
                });
                if(children != null && children.size()>0){
                    firstObj.element("children",children);
                }
            }
            secondlevel = null;
            ApplicationCommon.CACHEPROVIDER.put("region",result);
        }
        return result;
    }

    /**
     * IOS端辖区数据
     * @return
     */
    public List<JSONObject> regionTree4IOS() {
        List<JSONObject> result = (List<JSONObject>) ApplicationCommon.CACHEPROVIDER.get("regionTree4IOS");
        if(result == null){
            List<CodeDm> list = super.getBaseDao().list(CodeDm.class,Order.asc("id"));
            JSONObject jsonObject;
            result = new LinkedList<JSONObject>();
            List<JSONObject> secondlevel = new LinkedList<JSONObject>();
            List<JSONObject> thirdlevel = new LinkedList<JSONObject>();
            //四大直辖市，市辖区和县序号
            String[] xhArr = {"1101","1102","1201","1202","3101","3102","5001","5002"};
            String xh = "";
            for(final CodeDm codeDm : list){
                jsonObject = new JSONObject();
                xh = codeDm.getXh();
                jsonObject.element("xh",xh);
                if(xh.length()==6){
                    jsonObject.element("city",codeDm.getMc());
                    thirdlevel.add(jsonObject);
                }else if(xh.length()==4){
                    if(ArrayUtils.contains(xhArr,xh)){//跳过四大直辖市的市辖区和县，二级中去掉
                        continue;
                    }
                    jsonObject.element("city",codeDm.getMc());
                    secondlevel.add(jsonObject);
                }else{
                    jsonObject.element("state",codeDm.getMc());
                    result.add(jsonObject);
                }
            }

            List<JSONObject> children = null;
            for(final JSONObject secondObj : secondlevel){
                children = (List<JSONObject>)select(thirdlevel,new Predicate(){
                    @Override
                    public boolean evaluate(Object o) {
                        String xh = ((JSONObject)o).get("xh").toString();
                        return xh.substring(0,4).equals(secondObj.getString("xh"));
                    }
                });
                if(children != null && children.size()>0){
                    String[] arr = new String[children.size()];
                    for(int i = 0;i<children.size();i++){
                        arr[i] = children.get(i).getString("city");
                    }
                    secondObj.element("areas",arr);
                }
            }

            List<JSONObject> emptyList = null;
            for(final JSONObject firstObj : result){
                final String firstXh = firstObj.getString("xh");
                //四大直辖市特殊处理
                if(firstXh.equals("11") || firstXh.equals("12") || firstXh.equals("31") || firstXh.equals("50")){
                    children = (List<JSONObject>)select(thirdlevel, new Predicate() {
                        @Override
                        public boolean evaluate(Object o) {
                            String xh = ((JSONObject)o).get("xh").toString();
                            return xh.substring(0,2).equals(firstXh);
                        }
                    });
                    if(children != null && children.size()>0){
                        for(JSONObject jsonObject1 : children){
                            jsonObject1.element("areas","[]");
                        }
                        firstObj.element("cities",children);
                    }
                }else if(firstXh.equals("71") || firstXh.equals("81") || firstXh.equals("82")){//台湾，香港，澳门
                    emptyList = new ArrayList<JSONObject>(1);
                    jsonObject = new JSONObject();
                    jsonObject.element("city",firstObj.getString("state")).element("areas","[]");
                    emptyList.add(jsonObject);
                    firstObj.element("cities",emptyList);
                }else{
                    children = (List<JSONObject>)select(secondlevel, new Predicate() {
                        @Override
                        public boolean evaluate(Object o) {
                            String xh = ((JSONObject)o).get("xh").toString();
                            return xh.substring(0,2).equals(firstXh);
                        }
                    });
                    if(children != null && children.size()>0){
                        firstObj.element("cities",children);
                    }
                }
            }
            ApplicationCommon.CACHEPROVIDER.put("regionTree4IOS",result);
        }
        return result;
    }

    /**
     * 获取年份范围
     * @return
     */
    public static List<JSONObject> getYearRange(){
        List<JSONObject> list = new LinkedList<JSONObject>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.element("value","").element("name","请选择");
        list.add(jsonObject);
        for(int i = 1953;i<=2050;i++){
            jsonObject = new JSONObject();
            jsonObject.element("value",i).element("name",i);
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * 获取年份范围
     * @return
     */
    public static Map<String,Object> getYearRangeOpen(){
        Map<String,Object> map = new HashMap<>(2);
        List<String> list = new LinkedList<String>();
        String year = String.valueOf(DateUtil.getYear(new Date()));
        int index_current = 0;
        for(int i = 1953;i<=2050;i++){
            list.add(String.valueOf(i));
        }
        for(int i = 0;i<list.size();i++){
            if(list.get(i).equals(year)){
                index_current = i;
            }
        }
        map.put("yearrange",list);
        map.put("index_current",index_current);
        return map;
    }



}
