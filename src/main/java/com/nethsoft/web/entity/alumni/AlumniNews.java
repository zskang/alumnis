package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Clob;


/**
 * 新闻
 */
@Entity
@Table(name = "alumni_news")
@DynamicUpdate(true)
public class AlumniNews implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @Column
    @NotNull
    private String title; //标题
    @Column
    private Clob content; //内容
    @Transient
    private String contentStr; //内容
    @Formula("(select count(*) from alumni_news_agree t where t.news_id=id)")
    private Integer agree; //点赞数
    @Column
    private Integer browse; //浏览次数
    @Column(name="NEWS_TIME")
    @NotNull
    private String newsTime;//新闻时间
    @Column
    @NotNull
    private String type; //类型  0:母校新闻,1:校友动态,2:校友活动,3:校友捐赠
    @Column
    private String top; //置顶;  0:置顶，1:不置顶
    @Column
    private Integer topLimit; //投票上限，校友活动专有
    @Column
    private String preview;//预览图路径
    @Column
    private String voteType;//0：单选，1：多选
    @Formula("(select count(*) from alumni_banner banner where banner.newsid=id)")
    private Integer countBanner; //当前新闻在banner表中的数量

    public AlumniNews(){}

    public AlumniNews(String title, Clob content, String newsTime, String type,String top,String preview) {
        this.title = title;
        this.content = content;
        this.newsTime = newsTime;
        this.type = type;
        this.top = top;
        this.preview = preview;
    }

    /**
    * 获取 ID
    */
    public String getId() {
        return this.id;
    }

    /**
     * 设置 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
    * 获取 标题
    */
    public String getTitle() {
        return this.title;
    }

    /**
     * 设置 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
    * 获取 内容
    */
    public Clob getContent() {
        return this.content;
    }

    /**
     * 设置 内容
     */
    public void setContent(Clob content) {
        this.content = content;
    }

    /**
    * 获取 点赞数
    */
    public Integer getAgree() {
        return this.agree;
    }

    /**
     * 设置 点赞数
     */
    public void setAgree(Integer agree) {
        this.agree = agree;
    }

    /**
    * 获取 浏览此数
    */
    public Integer getBrowse() {
        return this.browse;
    }

    /**
     * 设置 浏览此数
     */
    public void setBrowse(Integer browse) {
        this.browse = browse;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    /**
    * 获取 类型  0:母校新闻,1:校友动态,2:校友活动,3:校友捐赠
    */
    public String getType() {
        return this.type;
    }

    /**
     * 设置 类型  0:母校新闻,1:校友动态,2:校友活动,3:校友捐赠
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
    * 获取 置顶;  0:置顶，1:不置顶
    */
    public String getTop() {
        return this.top;
    }

    /**
     * 设置 置顶;  0:置顶，1:不置顶
     */
    public void setTop(String top) {
        this.top = top;
    }

    /**
    * 获取 投票上限
    */
    public Integer getTopLimit() {
        return this.topLimit;
    }

    /**
     * 设置 投票上限
     */
    public void setTopLimit(Integer topLimit) {
        this.topLimit = topLimit;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getVoteType() {
        return voteType;
    }

    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }

    public Integer getCountBanner() {
        return countBanner;
    }

    public void setCountBanner(Integer countBanner) {
        this.countBanner = countBanner;
    }
}
