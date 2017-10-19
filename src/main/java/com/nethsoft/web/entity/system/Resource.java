package com.nethsoft.web.entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 菜单
 * @author zengchao
 *
 */
@Entity
@Table(name="sys_res")
@DynamicUpdate(true)
public class Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3364043806717534304L;
	
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@Column
	private String name;
	@Column
	private String url;
	@Column
	private String icon;
	@Column
	private int location;
	@Column
	private boolean enabled;
	@Column
	private String descript;
	@Column
	private boolean hasChildren;
	@Column
	private String parentId;
	@Column
	private boolean beta;//是否是beta版本
	@Column
	private boolean display;//是否显示此菜单
	@Column
	private boolean newWindow;//是否新窗口打开
	@Column
	private String createTime;
	@ManyToMany(mappedBy="resources",cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch=FetchType.LAZY)
	private List<Role> roles = new ArrayList<Role>();
	@Transient
	private List<Button> buttons = new ArrayList<Button>();
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public boolean isHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public boolean isBeta() {
		return beta;
	}
	public void setBeta(boolean beta) {
		this.beta = beta;
	}
	public boolean isDisplay() {
		return display;
	}
	public void setDisplay(boolean display) {
		this.display = display;
	}
	public boolean isNewWindow() {
		return newWindow;
	}
	public void setNewWindow(boolean newWindow) {
		this.newWindow = newWindow;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public List<Button> getButtons() {
		return buttons;
	}
	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
