package com.nethsoft.web.controller.alumni;

import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.controller.common.UploadController;
import com.nethsoft.web.entity.alumni.AlumniBanner;
import com.nethsoft.web.entity.alumni.AlumniNews;
import com.nethsoft.web.service.alumni.AlumniBannerService;
import com.nethsoft.web.service.alumni.AlumniNewsService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 首页轮播图
 */
@Controller
@RequestMapping(value = "/alumni/banner")
public class AlumniBannerController extends BaseController<AlumniBanner> {
    private Logger logger = Logger.getLogger(this.getClass());
    private final String TPL = "/alumni/banner/";
    @Autowired
    private AlumniBannerService alumniBannerService;
    @Autowired
    private AlumniNewsService alumniNewsService;

    /**
     * 首页
     */
    @RequestMapping(method =  {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, PageBean pageBean) {
        List<AlumniBanner> list = alumniBannerService.find(pageBean);
        model.addAttribute("list", list);
        return TPL + "index";
    }

    /**
     * 统计轮播图的数量
     * @return
     */
    @RequestMapping("/count")
    public @ResponseBody JSONObject count() {
        try {
            int count = alumniBannerService.count();
            return WebResult.success().element("count",count);
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }

    /**
     * 添加轮播图
     * @param file  图片
     * @param newsId  新闻ID,轮播图是从新闻中来的
     * @return
     */
    @RequestMapping("/save")
    public @ResponseBody JSONObject save(@RequestParam("file") MultipartFile file,@RequestParam("newsId") String newsId) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            double scale_img = bufferedImage.getHeight()*1.0/bufferedImage.getWidth();
            //宽高比16:7
            double scale = 7*1.0/16;
            double abs = Math.abs(scale_img - scale);
            //误差在0.1以内的都可以了
            if(abs<0.1){
                AlumniNews alumniNews = alumniNewsService.getById(newsId);
                String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
                String path = UploadController.upload(file.getBytes(),ext);
                AlumniBanner alumniBanner = new AlumniBanner(alumniNews,path);
                alumniBannerService.save(alumniBanner);
                return WebResult.success();
            }else{
                return WebResult.error("请上传16:7比例的图片");
            }
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }


    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method =  {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject delete(@PathVariable String id) {
        try {
            alumniBannerService.delete(id);
            return WebResult.success();
        } catch (Exception e) {
            logger.error(e);
            return WebResult.error(e.toString());
        }
    }
}
