
package com.nethsoft.web.entity.campus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;


/**   
 * @Title: Entity
 * @Description: 学籍表  来源学校的xj_xjb视图
 * @author cf
 * @date 2016-06-23 11:05:24
 * @version V1.0   
 *
 */
@Entity
@Table(name = "CAMPUS_XJB", schema = "")
@SuppressWarnings("serial")
public class CampusXjb {
	private static final long serialVersionUID = 3509941384851901401L;

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid")
	@GeneratedValue(generator = "idGenerator")
	private String xh;
	@Column
	private String xm;
	@Column
	private String ywxm;
	@Column
	private String sfzh;
	@Column
	private String xb;
	@Column
	private String mz;
	@Column
	private String jg;
	@Column
	private String csrq;
	@Column
	private String zzmm;
	@Column
	private String xsh;
	@Column
	private String zyh;
	@Column
	private String zyfx;
	@Column
	private String bm;
	@Column
	private String rxnj;
	@Column
	private String ssnj;
	@Column
	private String xslb;
	@Column
	private String sflb;
	@Column
	private String sfyxj;
	@Column
	private String kq;
	@Column
	private String byzx;
	@Column
	private String wyyz;
	@Column
	private String gkzf;
	@Column
	private String ssdz;
	@Column
	private String dexwxsh;
	@Column
	private String dexwzyh;
	@Column
	private String dexwbm;
	@Column
	private String bylb;
	@Column
	private String flfx;
	@Column
	private String byrq;
	@Column
	private String byzsbh;
	@Column
	private String xwzsbh;
	@Column
	private String bz;
	@Column
	private String lqh;
	@Column
	private String gkksh;
	@Column
	private String xw;
	@Column
	private String ycsj;
	@Column
	private String pyfs;
	@Column
	private String xmpy;
	@Column
	private String rxrq;
	@Column
	private String xklb;
	@Column
	private String xz;
	@Column
	private String ydf;
	@Column
	private String ympy;
	@Column
	private String xq;
	@Column
	private String rxksyz;
	@Column
	private String xqdm;
	@Column
	private String ybm;
	@Column
	private String ydlb;
	@Column
	private String ydrq;
	@Column
	private String yzyh;
	@Column
	private String ydyy;
	@Column
	private String jtzz;
	@Column
	private String yzbm;
	@Column
	private String dhhm;
	@Column
	private String dexwzsh;
	@Column
	private String sftj;
	@Column
	private String skbm;
	@Column
	private String jhnj;
	@Column
	private String xjzt;
	@Column
	private String sefsfyxj;
	@Column
	private String sefzt;
	@Column
	private String tsbm;


	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getYwxm() {
		return ywxm;
	}

	public void setYwxm(String ywxm) {
		this.ywxm = ywxm;
	}

	public String getSfzh() {
		return sfzh;
	}

	public void setSfzh(String sfzh) {
		this.sfzh = sfzh;
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

	public String getSfyxj() {
		return sfyxj;
	}

	public void setSfyxj(String sfyxj) {
		this.sfyxj = sfyxj;
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

	public String getSsdz() {
		return ssdz;
	}

	public void setSsdz(String ssdz) {
		this.ssdz = ssdz;
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

	public String getGkksh() {
		return gkksh;
	}

	public void setGkksh(String gkksh) {
		this.gkksh = gkksh;
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

	public String getYdf() {
		return ydf;
	}

	public void setYdf(String ydf) {
		this.ydf = ydf;
	}

	public String getYmpy() {
		return ympy;
	}

	public void setYmpy(String ympy) {
		this.ympy = ympy;
	}

	public String getXq() {
		return xq;
	}

	public void setXq(String xq) {
		this.xq = xq;
	}

	public String getRxksyz() {
		return rxksyz;
	}

	public void setRxksyz(String rxksyz) {
		this.rxksyz = rxksyz;
	}

	public String getXqdm() {
		return xqdm;
	}

	public void setXqdm(String xqdm) {
		this.xqdm = xqdm;
	}

	public String getYbm() {
		return ybm;
	}

	public void setYbm(String ybm) {
		this.ybm = ybm;
	}

	public String getYdlb() {
		return ydlb;
	}

	public void setYdlb(String ydlb) {
		this.ydlb = ydlb;
	}

	public String getYdrq() {
		return ydrq;
	}

	public void setYdrq(String ydrq) {
		this.ydrq = ydrq;
	}

	public String getYdyy() {
		return ydyy;
	}

	public void setYdyy(String ydyy) {
		this.ydyy = ydyy;
	}

	public String getJtzz() {
		return jtzz;
	}

	public void setJtzz(String jtzz) {
		this.jtzz = jtzz;
	}

	public String getYzbm() {
		return yzbm;
	}

	public void setYzbm(String yzbm) {
		this.yzbm = yzbm;
	}

	public String getDhhm() {
		return dhhm;
	}

	public void setDhhm(String dhhm) {
		this.dhhm = dhhm;
	}

	public String getDexwzxh() {
		return dexwzsh;
	}

	public void setDexwzxh(String dexwzxh) {
		this.dexwzsh = dexwzxh;
	}

	public String getSftj() {
		return sftj;
	}

	public void setSftj(String sftj) {
		this.sftj = sftj;
	}

	public String getSkbm() {
		return skbm;
	}

	public void setSkbm(String skbm) {
		this.skbm = skbm;
	}

	public String getJhnj() {
		return jhnj;
	}

	public void setJhnj(String jhnj) {
		this.jhnj = jhnj;
	}

	public String getXjzt() {
		return xjzt;
	}

	public void setXjzt(String xjzt) {
		this.xjzt = xjzt;
	}

	public String getSefsfyxj() {
		return sefsfyxj;
	}

	public void setSefsfyxj(String sefsfyxj) {
		this.sefsfyxj = sefsfyxj;
	}

	public String getSefzt() {
		return sefzt;
	}

	public void setSefzt(String sefzt) {
		this.sefzt = sefzt;
	}

	public String getTsbm() {
		return tsbm;
	}

	public void setTsbm(String tsbm) {
		this.tsbm = tsbm;
	}

	

	public String getWyyz() {
		return wyyz;
	}

	public void setWyyz(String wyyz) {
		this.wyyz = wyyz;
	}

	public String getBylb() {
		return bylb;
	}

	public void setBylb(String bylb) {
		this.bylb = bylb;
	}

	public String getYzyh() {
		return yzyh;
	}

	public void setYzyh(String yzyh) {
		this.yzyh = yzyh;
	}

	public String getDexwzsh() {
		return dexwzsh;
	}

	public void setDexwzsh(String dexwzsh) {
		this.dexwzsh = dexwzsh;
	}
}
	
