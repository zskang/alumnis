package com.nethsoft.web.entity.system;

import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 通知
 * @author zengchao
 *
 */
@Entity
@Table(name="sys_notify")
@DynamicUpdate(true)
public class Notify implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5688076092018314963L;
	@Id
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@GeneratedValue(generator="idGenerator")
	private String id;
	@Lob
	private Blob content;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Blob getContent() {
		return content;
	}
	public void setContent(Blob content) {
		this.content = content;
	}
}
