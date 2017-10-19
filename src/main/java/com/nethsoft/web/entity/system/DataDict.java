package com.nethsoft.web.entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="sys_datadict")
@DynamicUpdate(true)
public class DataDict implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5529839468467380809L;
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@Column
	private String category;
	@Column
	@NotNull
	private String name;
	@Column
	private String remarks;
	@Column
	private String createUser;
	@Column
	private String createTime;
	
	@OneToMany(mappedBy="dataDict", fetch=FetchType.LAZY , orphanRemoval=true)
	@OrderBy("location asc")
	private List<DataDictItem> items = new ArrayList<DataDictItem>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public List<DataDictItem> getItems() {
		return items;
	}
	public void setItems(List<DataDictItem> items) {
		this.items = items;
	}
}
