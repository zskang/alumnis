package com.nethsoft.web.entity.alumni;

import com.nethsoft.web.entity.campus.CampusStudent;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 校友用户
 */
@Entity
@Table(name = "alumni_user")
@DynamicUpdate(true)
public class AlumniUser implements Serializable {
    private static final long serialVersionUID = 3509941384851901401L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;
    @Column
    private String ywxm; //英文姓名
    @Column
    private String xb; //性别
    @Column
    private String mz; //民族
    @Column
    private String jg; //籍贯
    @Column
    private String csrq; //出生日期
    @Column
    private String zzmm; //政治面貌
    @Column
    private String xsh; //系所号
    @Column
    private String zyh; //专业号
    @Column
    private String zyfx; //专业方向
    @Column
    private String bm; //班名
    @Column
    private String rxnj; //入学年级
    @Column
    private String ssnj; //所属年级
    @Column
    private String xslb; //学生类别
    @Column
    private String sflb; //收费类别
    @Column
    private String kq; //考区
    @Column
    private String byzx; //毕业中学
    @Column
    private String gkzf; //高考总分
    @Column
    private String dexwxsh; //第二学位系所号
    @Column
    private String dexwzyh; //第二学位专业号
    @Column
    private String dexwbm; //第二学位班名
    @Column
    private String bylb; //毕业类别
    @Column
    private String flfx; //分类方向
    @Column
    private String byrq; //毕业日期
    @Column
    private String byzsbh; //毕业证编号
    @Column
    private String xwzsbh; //学位证书编号
    @Column
    private String bz; //备注
    @Column
    private String lqh; //lqh
    @Column
    private String xw; //学位
    @Column
    private String ycsj; //因材施教
    @Column
    private String pyfs; //培养方向
    @Column
    private String xmpy; //姓名拼音
    @Column
    private String rxrq; //入学日期
    @Column
    private String xklb; //学科类别
    @Column
    private String xz; //学制
    @Column
    private String ybm; //原班名
    @Column
    private String dexwzsh; //第二学位证书号
    @Column
    private String skbm; //上课班名
    @Column
    private String tsbm; //特殊班名
    @Column
    private String dexwbyzsh; //第二学位毕业证书号
    @Column
    private String qq; //qq号
    @Column
    private String wechat; //微信号
    @Column
    private String backgroundImage; //背景图片
    @Column
    private String industry; //行业
    @Column
    private String companyName; //单位名称
    @Column
    private String region;//辖区
    @Column
    private String address; //通信地址
    @Column
    private String position; //校友会职位
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    private CampusStudent campusStudent;
    @Formula("(select t.xsm from code_xsb t where t.xsh=xsh)")
    private String xymc;
    @Column
    private String rxnf; //入学年份
    @Column
    private String bynf; //毕业年份
    @Column
    private String signature; //签名
    @Column
    private String modifyTime;//时间


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYwxm() {
        return ywxm;
    }

    public void setYwxm(String ywxm) {
        this.ywxm = ywxm;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public String getMz() {
        return mz;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public String getJg() {
        return jg;
    }

    public void setJg(String jg) {
        this.jg = jg;
    }

    public String getCsrq() {
        return csrq;
    }

    public void setCsrq(String csrq) {
        this.csrq = csrq;
    }

    public String getZzmm() {
        return zzmm;
    }

    public void setZzmm(String zzmm) {
        this.zzmm = zzmm;
    }

    public String getXsh() {
        return xsh;
    }

    public void setXsh(String xsh) {
        this.xsh = xsh;
    }

    public String getZyh() {
        return zyh;
    }

    public void setZyh(String zyh) {
        this.zyh = zyh;
    }

    public String getZyfx() {
        return zyfx;
    }

    public void setZyfx(String zyfx) {
        this.zyfx = zyfx;
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public String getRxnj() {
        return rxnj;
    }

    public void setRxnj(String rxnj) {
        this.rxnj = rxnj;
    }

    public String getSsnj() {
        return ssnj;
    }

    public void setSsnj(String ssnj) {
        this.ssnj = ssnj;
    }

    public String getXslb() {
        return xslb;
    }

    public void setXslb(String xslb) {
        this.xslb = xslb;
    }

    public String getSflb() {
        return sflb;
    }

    public void setSflb(String sflb) {
        this.sflb = sflb;
    }

    public String getKq() {
        return kq;
    }

    public void setKq(String kq) {
        this.kq = kq;
    }

    public String getByzx() {
        return byzx;
    }

    public void setByzx(String byzx) {
        this.byzx = byzx;
    }

    public String getGkzf() {
        return gkzf;
    }

    public void setGkzf(String gkzf) {
        this.gkzf = gkzf;
    }

    public String getDexwxsh() {
        return dexwxsh;
    }

    public void setDexwxsh(String dexwxsh) {
        this.dexwxsh = dexwxsh;
    }

    public String getDexwzyh() {
        return dexwzyh;
    }

    public void setDexwzyh(String dexwzyh) {
        this.dexwzyh = dexwzyh;
    }

    public String getDexwbm() {
        return dexwbm;
    }

    public void setDexwbm(String dexwbm) {
        this.dexwbm = dexwbm;
    }

    public String getBylb() {
        return bylb;
    }

    public void setBylb(String bylb) {
        this.bylb = bylb;
    }

    public String getFlfx() {
        return flfx;
    }

    public void setFlfx(String flfx) {
        this.flfx = flfx;
    }

    public String getByrq() {
        return byrq;
    }

    public void setByrq(String byrq) {
        this.byrq = byrq;
    }

    public String getByzsbh() {
        return byzsbh;
    }

    public void setByzsbh(String byzsbh) {
        this.byzsbh = byzsbh;
    }

    public String getXwzsbh() {
        return xwzsbh;
    }

    public void setXwzsbh(String xwzsbh) {
        this.xwzsbh = xwzsbh;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getLqh() {
        return lqh;
    }

    public void setLqh(String lqh) {
        this.lqh = lqh;
    }

    public String getXw() {
        return xw;
    }

    public void setXw(String xw) {
        this.xw = xw;
    }

    public String getYcsj() {
        return ycsj;
    }

    public void setYcsj(String ycsj) {
        this.ycsj = ycsj;
    }

    public String getPyfs() {
        return pyfs;
    }

    public void setPyfs(String pyfs) {
        this.pyfs = pyfs;
    }

    public String getXmpy() {
        return xmpy;
    }

    public void setXmpy(String xmpy) {
        this.xmpy = xmpy;
    }

    public String getRxrq() {
        return rxrq;
    }

    public void setRxrq(String rxrq) {
        this.rxrq = rxrq;
    }

    public String getXklb() {
        return xklb;
    }

    public void setXklb(String xklb) {
        this.xklb = xklb;
    }

    public String getXz() {
        return xz;
    }

    public void setXz(String xz) {
        this.xz = xz;
    }

    public String getYbm() {
        return ybm;
    }

    public void setYbm(String ybm) {
        this.ybm = ybm;
    }

    public String getDexwzsh() {
        return dexwzsh;
    }

    public void setDexwzsh(String dexwzsh) {
        this.dexwzsh = dexwzsh;
    }

    public String getSkbm() {
        return skbm;
    }

    public void setSkbm(String skbm) {
        this.skbm = skbm;
    }

    public String getTsbm() {
        return tsbm;
    }

    public void setTsbm(String tsbm) {
        this.tsbm = tsbm;
    }

    public String getDexwbyzsh() {
        return dexwbyzsh;
    }

    public void setDexwbyzsh(String dexwbyzsh) {
        this.dexwbyzsh = dexwbyzsh;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public CampusStudent getCampusStudent() {
        return campusStudent;
    }

    public void setCampusStudent(CampusStudent campusStudent) {
        this.campusStudent = campusStudent;
    }

    public String getXymc() {
        return xymc;
    }

    public void setXymc(String xymc) {
        this.xymc = xymc;
    }

    public String getRxnf() {
        return rxnf;
    }

    public void setRxnf(String rxnf) {
        this.rxnf = rxnf;
    }

    public String getBynf() {
        return bynf;
    }

    public void setBynf(String bynf) {
        this.bynf = bynf;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}
