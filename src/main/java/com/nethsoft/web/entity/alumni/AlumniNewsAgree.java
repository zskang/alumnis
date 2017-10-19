package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 新闻点赞
 */
@Entity
@Table(name = "alumni_news_agree")
@DynamicUpdate(true)
public class AlumniNewsAgree implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @Column(name = "NEWS_ID")
    @NotNull
    private String newsId;//新闻ID
    @Column(name = "STUDENT_ID")
    @NotNull
    private String studentId;//点赞人
    @Column(name="CREATE_TIME")
    @NotNull
    private String createTime;//时间

    public AlumniNewsAgree(){}

    public AlumniNewsAgree(String newsId, String studentId, String createTime) {
        this.newsId = newsId;
        this.studentId = studentId;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
