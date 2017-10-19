package com.nethsoft.web.service.alumni;

import com.nethsoft.web.entity.alumni.AlumniDonateFeedback;
import com.nethsoft.web.service.BaseService;

import com.nethsoft.web.support.alumni.AlumniPushUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AlumniDonateFeedbackService extends BaseService<AlumniDonateFeedback> {

    /**
     * 插入记录并推送消息
     * @param feedback  捐赠反馈
     * @param alias 别名
     * @param path  客户端访问此消息的地址
     * @throws RuntimeException
     */
    public void insertAndPush(AlumniDonateFeedback feedback,String alias,String path) throws RuntimeException{
        super.save(feedback);
        path += feedback.getId();
        Map<String,String> extra = new HashMap<>(1);
        extra.put("path",path);
        String result = AlumniPushUtil.pushWithAlias(feedback.getTitle(),feedback.getContent(),alias,extra);
        if(result.equals("true")){
        }else if(result.equals("false")){
            throw new RuntimeException("推送失败");
        }else{
            throw new RuntimeException(result);
        }
    }
}
