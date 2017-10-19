package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 轮播图
 */
@Entity
@Table(name = "alumni_banner")
@DynamicUpdate(true)
public class AlumniBanner implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "newsId")
    @NotNull
    private AlumniNews news;//新闻
    @Column
    @NotNull
    private String path; //图片路径

    public AlumniBanner(){}

    public AlumniBanner(AlumniNews news, String path) {
        this.news = news;
        this.path = path;
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
    * 获取 newsId
    */
    public AlumniNews getNews() {
        return this.news;
    }

    /**
     * 设置 newsId
     */
    public void setNews(AlumniNews news) {
        this.news = news;
    }

    /**
    * 获取 path
    */
    public String getPath() {
        return this.path;
    }

    /**
     * 设置 path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
