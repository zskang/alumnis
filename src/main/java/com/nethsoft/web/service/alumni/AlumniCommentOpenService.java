package com.nethsoft.web.service.alumni;

import com.nethsoft.web.service.BaseService;
import org.springframework.stereotype.Service;


@Service
public class AlumniCommentOpenService extends BaseService {

    /**
     * 根据key判断知否存在此key值
     * @param key
     * @return
     */
    public boolean exists(String key){
       Object obj =  queryForObject("select key from alumni_comment_open where key='"+key+"'");
        if(obj == null){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 根据key获取对应的value
     * @param key
     * @return
     */
    public Object getValueByKey(String key){
        return queryForObject("select value from alumni_comment_open where key='"+key+"'");
    }

    /**
     * 保存
     * @param key
     * @param value
     */
    public void save(String key,String value){
        if(exists(key)){//存在
            executeSQL("update alumni_comment_open set value=? where key=?",value,key);
        }else{//不存在此key
            executeSQL("insert into alumni_comment_open values(?,?)",key,value);
        }
    }

}
