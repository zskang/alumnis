package com.nethsoft.web.service.system;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.service.BaseService;

@Service
public class ResourceService extends BaseService<Resource>{

	/**
	 * 保存并更新父节点的是否有子节点状态
	 * @param res
	 */
	public void saveAndUpdateParent(Resource res) {
		//计算当前资源的位置
		int count = this.count(Restrictions.eq("parentId", res.getParentId()));
		res.setLocation(count+1);
		this.save(res);
		if(!res.getParentId().equals("-1")){
			this.executeHQL("update Resource set hasChildren=true where id='"+res.getParentId()+"'");
		}
	}

	public void deleteAndRelated(String id) {
		
		//获取当前菜单的父级菜单
		String pid = this.getById(id).getParentId();
		//删除关系
		this.executeSQL("delete from sys_role_res_btn where resId='"+id+"'");
		this.executeSQL("delete from sys_role_res_btn where resid in (select id from sys_res where parentId='"+id+"')");
		this.executeSQL("delete from sys_role_res where resid = '"+id+"'");
		this.executeSQL("delete from sys_role_res where resid in (select id from sys_res where parentId='"+id+"')");
		//删除此菜单下的一级菜单
		List<Resource> list = this.listForEntity("select id from Resource where parentId in (select id from Resource where parentId = ?)", id);
		for(Resource res : list)
			this.executeHQL("delete from Resource where id = ?", res.getId());
		this.executeHQL("delete from Resource where parentId = ?", id);
		this.executeHQL("delete from Resource where id = ?", id);
		//判断
		int count = this.count(Restrictions.eq("parentId", pid));
		if(count == 0){
			this.executeHQL("update Resource set hasChildren=false where id = ?", pid);
		}
	}

	/**
	 * 执行排序
	 * @param data
	 */
	public void executeSort(String data) {
		JSONArray resList = JSONArray.fromObject(data);
		for(int i=0;i<resList.size();i++){
			JSONObject res1 = resList.getJSONObject(i);
			this.executeHQL("update Resource set location="+(i+1)+",parentId='-1' where id='"+res1.getString("id")+"'");
			if(res1.has("children")){//更新二级菜单
				JSONArray res2List = res1.getJSONArray("children");
				for(int j=0;j<res2List.size();j++){
					JSONObject res2 = res2List.getJSONObject(j); 
					this.executeHQL("update Resource set location="+(j+1)+", parentId='"+res1.getString("id")+"' where id='"+res2.getString("id")+"'");
					if(res2.has("children")){//更新三级菜单
						JSONArray res3List = res2.getJSONArray("children");
						for(int p=0;p<res3List.size();p++){
							JSONObject res3 = res3List.getJSONObject(p); 
							this.executeHQL("update Resource set location="+(p+1)+", parentId='"+res2.getString("id")+"' where id='"+res3.getString("id")+"'");
						}
					}
				}
			}
		}
	}
}
