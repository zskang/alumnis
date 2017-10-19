package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 校友分会
 */
@Entity
@Table(name = "alumni_association")
@DynamicUpdate(true)
public class AlumniAssociation implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @Column
    @NotNull
    private String name; //分会名称
    @Column
    private String summary; //描述
    @Column(name="MODIFY_TIME")
    @NotNull
    private String modifyTime;//修改时间
    @Column
    private String groupId;//群组ID

    public AlumniAssociation(){}

    public AlumniAssociation(String name, String summary, String modifyTime, String groupId) {
        this.name = name;
        this.summary = summary;
        this.modifyTime = modifyTime;
        this.groupId = groupId;
    }

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
