package ${pack};

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
#if($!hasBigDecimal)
import java.math.BigDecimal;
#end
#if($!forgetColumns.size()>0)
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
#foreach($!forget in $!forgetColumns.entrySet())
import ${entity_pack}.$!forget.value.get(0);
#end
#end

/**
 * @author nethsoft
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "${tableName}")
@DynamicUpdate(true)
public class ${className} implements Serializable{
	
	private static final long serialVersionUID = 3509941384851901401L;
	
	#foreach($!column in $!columns.entrySet())
		#set($v = $!column.value)
		#if($!v.get("primary") == true)
			@Id
			@GenericGenerator(name="idGenerator", strategy="uuid")
			@GeneratedValue(generator="idGenerator")
			private ${v.get("type")} id; //${v.get("displayName")}
		#else
			#if($!forgetColumns.get($!column.key).size()>0)@ManyToOne(fetch = FetchType.LAZY)
			@JoinColumn(name = "$!column.key")
			private $!forgetColumns.get($!column.key).get(0) $!forgetColumns.get($!column.key).get(1);
			#else
			@Column
			#if($!v.get("nullable") == false)
				@NotNull
			#end
				private ${v.get("type")} ${v.get("lowerName")};//${v.get("displayName")}
			#end
		#end
	#end
	
	#foreach($!column in $!columns.entrySet())
	#set($v = $!column.value)
	#if($!forgetColumns.get($!column.key).size()>0)
		/**
		 * 获取 ${v.get("displayName")}
		 */
		public $!forgetColumns.get($!column.key).get(0) get$!forgetColumns.get($!column.key).get(2)(){
			return this.$!forgetColumns.get($!column.key).get(1);
		}
		/**
		 * 设置 ${v.get("displayName")}
		 */
		public void set$!forgetColumns.get($!column.key).get(2)($!forgetColumns.get($!column.key).get(0) $!forgetColumns.get($!column.key).get(1)){
			this.$!forgetColumns.get($!column.key).get(1) = $!forgetColumns.get($!column.key).get(1);
		}
	#else
	/**
	 * 获取 ${v.get("displayName")}
	 */
	public ${v.get("type")} get${v.get("name")}(){
		return this.${v.get("lowerName")};
	}
	/**
	 * 设置 ${v.get("displayName")}
	 */
	public void set${v.get("name")}(${v.get("type")} ${v.get("lowerName")}){
		this.${v.get("lowerName")} = ${v.get("lowerName")};
	}
	#end
	#end
}
