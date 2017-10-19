package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 捐赠
 */
@Entity
@Table(name = "alumni_donate")
@DynamicUpdate(true)
public class AlumniDonate implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @NotNull
    private CampusStudent student;//用户，捐赠人
    @Column(name="create_time")
    @NotNull
    private String createTime;//和时间
    @Column
    @NotNull
    private java.math.BigDecimal money;//金额
    @Column
    @NotNull
    private String type;//类型，0：个人捐款 1：团体捐款
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @NotNull
    private AlumniDonateProject donateProject;//捐赠项目
    @Column
    private String message;//留言
    @Column
    @NotNull
    private String state;//状态，0：未支付，1：已支付
    @Column(name="team_name")
    private String teamName;//留言
    @Column
    @NotNull
    private String way;//方式，0：支付宝 1：微信，2：现金捐款
    @Column
    @NotNull
    private String orderNum;//订单号

    public AlumniDonate(){}

    public AlumniDonate(CampusStudent student, String createTime, BigDecimal money, String type, AlumniDonateProject donateProject,
                        String message, String state,String teamName,String way,String orderNum) {
        this.student = student;
        this.createTime = createTime;
        this.money = money;
        this.type = type;
        this.donateProject = donateProject;
        this.message = message;
        this.state = state;
        this.teamName = teamName;
        this.way = way;
        this.orderNum = orderNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CampusStudent getStudent() {
        return student;
    }

    public void setStudent(CampusStudent student) {
        this.student = student;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AlumniDonateProject getDonateProject() {
        return donateProject;
    }

    public void setDonateProject(AlumniDonateProject donateProject) {
        this.donateProject = donateProject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
}
