package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 系统反馈
 */
@Entity
@Table(name = "alumni_feedback")
@DynamicUpdate(true)
public class AlumniFeedback implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    @NotNull
    private CampusStudent campusStudent;//反馈人
    @Column
    @NotNull
    private String content;//反馈内容
    @Column(name="CREATE_TIME")
    @NotNull
    private String createTime;//时间

    public AlumniFeedback(){}

    public AlumniFeedback(CampusStudent campusStudent, String content, String createTime) {
        this.campusStudent = campusStudent;
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

    public CampusStudent getCampusStudent() {
        return campusStudent;
    }

    public void setCampusStudent(CampusStudent campusStudent) {
        this.campusStudent = campusStudent;
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
