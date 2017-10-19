package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 报名参加
 */
@Entity
@Table(name = "alumni_enter")
@DynamicUpdate(true)
public class AlumniEnter implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEWS_ID")
    @NotNull
    private AlumniNews news;//报名的新闻
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    @NotNull
    private CampusStudent student;//报名人
    @Column(name="ENTER_TIME")
    @NotNull
    private String enterTime;//时间

    public AlumniEnter(){}

    public AlumniEnter(AlumniNews news, CampusStudent student, String enterTime) {
        this.news = news;
        this.student = student;
        this.enterTime = enterTime;
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

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }
}
