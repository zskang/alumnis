package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 投票
 */
@Entity
@Table(name = "alumni_vote")
@DynamicUpdate(true)
public class AlumniVote implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEWS_ID")
    @NotNull
    private AlumniNews news;//新闻
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    @NotNull
    private CampusStudent student;//投票人
    @Column(name="CREATE_TIME")
    @NotNull
    private String createTime;//时间
    @Column
    @NotNull
    private String value; //
    @Formula("(select t.name from alumni_vote_configure t where t.id=value)")
    private String option;//投票的选项

    public AlumniVote(){}

    public AlumniVote(AlumniNews news, CampusStudent student, String createTime, String value) {
        this.news = news;
        this.student = student;
        this.createTime = createTime;
        this.value = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AlumniNews getNews() {
        return this.news;
    }
    public void setNews(AlumniNews news) {
        this.news = news;
    }

    public CampusStudent getStudent() {
        return student;
    }

    public void setStudent(CampusStudent student) {
        this.student = student;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
