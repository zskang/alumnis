package com.nethsoft.web.support.alumni;

import com.nethsoft.web.service.alumni.AlumniNewsService;
import com.nethsoft.web.support.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 爬取学校新闻任务
 */
@Component
public class ExtractTask {

    @Autowired
    private AlumniNewsService alumniNewsService;

    @Scheduled(cron="0 0 23 * * ?")
    public void save() {
        alumniNewsService.save4Extract("http://alumni.ustb.edu.cn/infoArticleList.do;?columnId=119",Constant.NEWS_TYPE_ALMA_MATER);
        alumniNewsService.save4Extract("http://alumni.ustb.edu.cn/infoArticleList.do;?columnId=5460",Constant.NEWS_TYPE_ALUMNI_DYNAMICS);
    }

}
