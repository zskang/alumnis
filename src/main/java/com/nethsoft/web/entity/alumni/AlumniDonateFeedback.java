package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 捐赠反馈
 */
@Entity
@Table(name = "alumni_donate_feedback")
@DynamicUpdate(true)
public class AlumniDonateFeedback implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DONATE_ID")
    private AlumniDonate donate;//捐赠记录
    @Column @NotNull
    private String title;//标题
    @Column @NotNull
    private String content;//内容
    @Column @NotNull
    private String path;//图片路径
    @Column(name="CREATE_TIME") @NotNull
    private String createTime;//时间


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
    * 获取 donateId
    */
    public AlumniDonate getDonate() {
        return this.donate;
    }

    /**
     * 设置 donateId
     */
    public void setDonate(AlumniDonate donate) {
        this.donate = donate;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
