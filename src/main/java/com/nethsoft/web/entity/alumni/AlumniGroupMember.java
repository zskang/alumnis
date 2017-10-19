package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 群组成员
 */
@Entity
@Table(name = "alumni_group_member")
@DynamicUpdate(true)
public class AlumniGroupMember implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @Column
    @NotNull
    private String groupId;//群组ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @NotNull
    private CampusStudent student;//成员
    @Column
    @NotNull
    private String position; //职位
    @Column(name="create_time")
    @NotNull
    private String createTime;//时间

    public AlumniGroupMember(){}

    public AlumniGroupMember(String groupId, CampusStudent student, String position, String createTime) {
        this.groupId = groupId;
        this.student = student;
        this.position = position;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public CampusStudent getStudent() {
        return student;
    }

    public void setStudent(CampusStudent student) {
        this.student = student;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
