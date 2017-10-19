package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.web.service.alumni.AlumniBannerService;
import com.nethsoft.web.support.BaseObject;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页轮播图
 */
@RestController
@RequestMapping(value = "/open/alumni/banner")
public class OpenAlumniBannerController extends BaseObject {
    private Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private AlumniBannerService alumniBannerService;

    @RequestMapping()
    public JSONObject index() {
        try{
            return WebResult.success().element("data",alumniBannerService.listJSONArray());
        }catch(Exception e){
            logger.error(e);
            return WebResult.error(e);
        }
    }

}
