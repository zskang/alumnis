package com.nethsoft.web.entity.alumni;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 群组
 */
@Entity
@Table(name = "alumni_group")
@DynamicUpdate(true)
public class AlumniGroup implements Serializable {

    private static final long serialVersionUID = 3509941384851901401L;

    @Id
    private String id; //此为环信群组ID
    @Column
    @NotNull
    private String name;//名称
    @Column
    private String summary;//描述
    @Column
    @NotNull
    private String open;//是否公开  true  false
    @Column
    @NotNull
    private String allowinvites;//是否允许群成员邀请别人加入此群  true  false
    @Column(name="create_time")
    @NotNull
    private String createTime;//时间

    public AlumniGroup(){}

    public AlumniGroup(String id,String name, String summary, String open, String allowinvites, String createTime) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.open = open;
        this.allowinvites = allowinvites;
        this.createTime = createTime;
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

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getAllowinvites() {
        return allowinvites;
    }

    public void setAllowinvites(String allowinvites) {
        this.allowinvites = allowinvites;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}
