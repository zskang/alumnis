package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 评论
 */
@Entity
@Table(name = "alumni_comment")
@DynamicUpdate(true)
public class AlumniComment implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEWS_ID")
    @NotNull
    private AlumniNews news;//评论对应的新闻
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    @NotNull
    private CampusStudent student;//评论人
    @Column
    @NotNull
    private String content; //内容
    @Column(name="CREATE_TIME")
    @NotNull
    private String createTime;

    public AlumniComment(){}

    public AlumniComment(AlumniNews news, CampusStudent student, String content, String createTime) {
        this.news = news;
        this.student = student;
        this.content = content;
        this.createTime = createTime;
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

    public CampusStudent getStudent() {
        return student;
    }

    public void setStudent(CampusStudent student) {
        this.student = student;
    }

    /**
    * 获取 content
    */
    public String getContent() {
        return this.content;
    }

    /**
     * 设置 content
     */
    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
