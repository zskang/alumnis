package com.nethsoft.web.entity.alumni;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 学生类别
 */
@Entity
@Table(name = "code_xslb")
public class CodeXslb implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    private String id;
    @Column
    @NotNull
    private String name; //名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
