package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 通知
 */
@Entity
@Table(name = "alumni_notice")
public class AlumniNotice implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;
    @Column @NotNull
    private String title;//标题
    @Column @NotNull
    private String content;//内容
    @Column(name="create_time") @NotNull
    private String createTime;//时间
    @Column
    private String tags;//推送的标签，为空则是广播推送

    public AlumniNotice(){}

    public AlumniNotice(String title, String content, String createTime,String tags) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.tags = tags;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
