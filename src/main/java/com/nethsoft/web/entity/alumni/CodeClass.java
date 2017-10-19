package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 字典表——班级
 */
@Entity
@Table(name = "code_class")
@DynamicUpdate(true)
public class CodeClass implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    private Integer id;
    @Column
    private String name; //名称

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
