package com.nethsoft.web.entity.alumni;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * 字典表——地名
 */
@Entity
@Table(name = "code_dm")
public class CodeDm implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    private String id;
    @Column
    private String mc; //名称
    @Column
    private String xh; //序号
    @Column
    private String qc; //全称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getQc() {
        return qc;
    }

    public void setQc(String qc) {
        this.qc = qc;
    }
}
