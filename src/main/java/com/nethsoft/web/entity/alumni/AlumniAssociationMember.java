package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 分会成员
 */
@Entity
@Table(name = "alumni_association_member")
@DynamicUpdate(true)
public class AlumniAssociationMember implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSOCIATION_ID")
    @NotNull
    private AlumniAssociation association;//分会
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    @NotNull
    private CampusStudent campusStudent;//用户
    @Column
    private String position; //职位
    @Column(name="CREATE_TIME")
    @NotNull
    private String createTime;

    public AlumniAssociationMember(){}

    public AlumniAssociationMember(AlumniAssociation association, CampusStudent campusStudent, String position, String createTime) {
        this.association = association;
        this.campusStudent = campusStudent;
        this.position = position;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AlumniAssociation getAssociation() {
        return association;
    }

    public void setAssociation(AlumniAssociation association) {
        this.association = association;
    }

    public CampusStudent getCampusStudent() {
        return campusStudent;
    }

    public void setCampusStudent(CampusStudent campusStudent) {
        this.campusStudent = campusStudent;
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
