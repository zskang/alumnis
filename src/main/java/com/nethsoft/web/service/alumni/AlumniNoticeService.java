package com.nethsoft.web.service.alumni;

import com.nethsoft.core.util.StringUtil;
import com.nethsoft.web.entity.alumni.AlumniNotice;
import com.nethsoft.web.service.BaseService;
import com.nethsoft.web.support.alumni.AlumniPushUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AlumniNoticeService extends BaseService<AlumniNotice> {

    /**
     * 保存并推送通知
     * @param alumniNotice  通知
     * @param path  通知对应的webview的访问地址，推送使用的
     * @throws RuntimeException
     */
    public void insertAndPush(AlumniNotice alumniNotice,String path) throws RuntimeException{
        super.save(alumniNotice);
        path += alumniNotice.getId();
        Map<String,String> extra = new HashMap<>(1);
        extra.put("path",path);
        String result = "";
        //标签为空则为广播式推送
        if(StringUtil.isEmpty(alumniNotice.getTags())){
            result = AlumniPushUtil.pushAll(alumniNotice.getTitle(),alumniNotice.getContent(),extra);
        }else{//按标签推送
            result = AlumniPushUtil.pushWithTag(alumniNotice.getTitle(),alumniNotice.getContent(),extra,alumniNotice.getTags().split(","));
        }
        if(result.equals("true")){
        }else if(result.equals("false")){
            throw new RuntimeException("推送失败");
        }else{
            throw new RuntimeException(result);
        }
    }

}
