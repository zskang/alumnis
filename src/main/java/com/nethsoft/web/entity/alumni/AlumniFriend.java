package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 好友
 */
@Entity
@Table(name = "alumni_friend")
@DynamicUpdate(true)
public class AlumniFriend implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_self")
    @NotNull
    private CampusStudent self;//自己
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_friend")
    @NotNull
    private CampusStudent friend;//好友
    @Column(name="create_time")
    @NotNull
    private String createTime;//时间

    public AlumniFriend(){}

    public AlumniFriend(CampusStudent self, CampusStudent friend, String createTime) {
        this.self = self;
        this.friend = friend;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CampusStudent getSelf() {
        return self;
    }

    public void setSelf(CampusStudent self) {
        this.self = self;
    }

    public CampusStudent getFriend() {
        return friend;
    }

    public void setFriend(CampusStudent friend) {
        this.friend = friend;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
