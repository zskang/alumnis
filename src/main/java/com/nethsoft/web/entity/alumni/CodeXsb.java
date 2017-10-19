package com.nethsoft.web.entity.alumni;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * 字典表——学院
 */
@Entity
@Table(name = "code_xsb")
public class CodeXsb implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    private String xsh; //系所号
    @Column
    private String xsm; //系所名

    /**
    * 获取 xsh
    */
    public String getXsh() {
        return this.xsh;
    }

    /**
     * 设置 xsh
     */
    public void setXsh(String xsh) {
        this.xsh = xsh;
    }

    /**
    * 获取 xsm
    */
    public String getXsm() {
        return this.xsm;
    }

    /**
     * 设置 xsm
     */
    public void setXsm(String xsm) {
        this.xsm = xsm;
    }
}
