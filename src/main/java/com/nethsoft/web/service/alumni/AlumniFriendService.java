package com.nethsoft.web.service.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.web.entity.alumni.AlumniFriend;
import com.nethsoft.web.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AlumniFriendService extends BaseService<AlumniFriend> {

    /**
     * 好友列表
     * @param token  用户ID
     * @param name  姓名，搜索时传
     * @return
     */
    public JSONArray list(String token,String name){
        Map<String,Object> param = new HashMap<String,Object>();
        String sql1 = "select a.student_friend id,b.name,b.nickname,b.im_username,b.photo,c.rxnf,c.zyfx,c.signature " +
                "from alumni_friend a,campus_student b,alumni_user c where a.student_friend = b.id and b.id = c.student_id and a.student_self =:id";
        String sql2 = "select a.student_self id,b.name,b.nickname,b.im_username,b.photo,c.rxnf,c.zyfx,c.signature " +
                "from alumni_friend a,campus_student b,alumni_user c where a.student_self = b.id and b.id = c.student_id and a.student_friend =:id";
        param.put("id",token);

        String selfSql = "";
        if(StringUtil.isNotEmpty(name)){
            sql1 += " and b.name like :name";
            sql2 += " and b.name like :name";
            param.put("name","%"+name.trim()+"%");
        }else{
            //不是搜索时好友列表中加上自己
            selfSql = " union select a.id,a.name,a.nickname,a.im_username,a.photo,b.rxnf,b.zyfx,b.signature" +
                    " from campus_student a,alumni_user b where a.id=b.student_id and a.id=:id";
        }
        String sql = sql1 +" union "+sql2+selfSql;
        return findBySql(sql,param);
    }

    private JSONArray findBySql(String sql,Map<String,Object> param){
        Query query = super.getBaseDao().getSQLQuery(sql);
        if(!param.isEmpty()){
            for(String key : param.keySet()){
                query.setString(key,param.get(key).toString());
            }
        }
        JSONArray jsonArray = new JSONArray();
        List<Object[]> list = query.list();
        if(list != null && list.size()>0){
            JSONObject jsonObject = null;
            String name = "";
            for(Object[] objects :list){
                jsonObject = new JSONObject();
                name = objects[1]==null?"":objects[1].toString();
                if(objects[5] != null){//入学年份
                    name += "("+objects[5]+"级";
                    if(objects[6] != null){//专业
                        name += "-"+objects[6];
                    }
                    name += ")";
                }
                jsonObject.element("id",objects[0])//好友ID
                         .element("name",name).element("imusername",objects[3].toString())
                .element("nickname",objects[2]==null?"":objects[2])//昵称
                .element("photo",objects[4]==null?"":objects[4])//头像
                .element("signature",objects[7]==null?"":objects[7]);//签名
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
}
