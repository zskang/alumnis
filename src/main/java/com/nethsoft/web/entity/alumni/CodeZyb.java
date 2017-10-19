package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * 字典表——专业
 */
@Entity
@Table(name = "code_zyb")
@DynamicUpdate(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CodeZyb implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    private String zyh; //专业号
    @Column
    private String zyjc; //专业简称
    @Column
    private String zym; //专业名
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "xsh")
    private CodeXsb xsb;//对应的学院

    public String getZyh() {
        return zyh;
    }

    public void setZyh(String zyh) {
        this.zyh = zyh;
    }

    public String getZyjc() {
        return zyjc;
    }

    public void setZyjc(String zyjc) {
        this.zyjc = zyjc;
    }

    public String getZym() {
        return zym;
    }

    public void setZym(String zym) {
        this.zym = zym;
    }

    public CodeXsb getXsb() {
        return xsb;
    }

    public void setXsb(CodeXsb xsb) {
        this.xsb = xsb;
    }
}
