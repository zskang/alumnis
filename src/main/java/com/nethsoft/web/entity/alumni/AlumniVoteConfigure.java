package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 投票选项
 */
@Entity
@Table(name = "alumni_vote_configure")
@DynamicUpdate(true)
public class AlumniVoteConfigure implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEWS_ID")
    @NotNull
    private AlumniNews news;//新闻
    @Column
    @NotNull
    private String name; //选项名称

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
    * 获取 name
    */
    public String getName() {
        return this.name;
    }

    /**
     * 设置 name
     */
    public void setName(String name) {
        this.name = name;
    }

}
