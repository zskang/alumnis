package com.nethsoft.web.entity.alumni;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * 字典表——行业
 */
@Entity
@Table(name = "code_industry")
public class CodeIndustry implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    private String id;
    @Column
    private String mc; //名称

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
}
