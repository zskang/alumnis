package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Clob;
import java.util.Date;


/**
 * 校友会简介
 */
@Entity
@Table(name = "alumni_introduction")
public class AlumniIntroduction implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @Column
    @NotNull
    private Clob content; //内容
    @Column(name="MODIFY_TIME")
    @NotNull
    private Date modifyTime;//时间
    @Transient
    private String contentStr;//内容字符串

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Clob getContent() {
        return content;
    }

    public void setContent(Clob content) {
        this.content = content;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
