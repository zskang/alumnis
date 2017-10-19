package com.nethsoft.web.controller.system;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.util.StringUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.Condition;
import com.nethsoft.web.controller.BaseController;
import com.nethsoft.web.entity.system.Resource;
import com.nethsoft.web.entity.system.Unit;
import com.nethsoft.web.service.system.LogService;
import com.nethsoft.web.service.system.ResourceService;
import com.nethsoft.web.service.system.UnitService;
import com.nethsoft.web.support.Constant;

@Controller
@RequestMapping("/system/unit")
public class UnitController extends BaseController<Unit> {
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private UnitService unitService;
	@Autowired
	private ResourceService resService;
	@Autowired
	private LogService logService;

	@RequestMapping(method = { RequestMethod.GET })
	public String index(Model model) {
		if (isSystemUnit()) {// 系统机构，查出所有的顶级机构
			List<Unit> unitList = unitService.list(Restrictions.eq("parentId", "-1"), Order.asc("code"));
			model.addAttribute("list", unitList);
			model.addAttribute("count", unitService.count());
		} else {// 查出当前用户用户下属的机构
			List<Unit> unitList = unitService.list(Restrictions.in("id", getCurrentUserUnitIdList()), Order.asc("code"));
			model.addAttribute("list", unitList);
			model.addAttribute("count", unitService.count());
		}
		model.addAttribute("isSystemUnit", isSystemUnit());
		logService.info("机构管理", "访问机构列表", getCurrentUser().getUserName(), getRequestAddr());
		return "system/unit/index";
	}

	@RequestMapping(value = "/tree", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray tree() {
		List<Unit> list = new ArrayList<Unit>();
		if (isSystemUnit())
			list = unitService.listForEntity("select id, name, hasChildren from Unit where parentId='-1' order by code asc");
		else
			list = unitService.listForEntity("select id, name, hasChildren from Unit where id in (" + getCurrentUserUnitIds() + ")  order by code asc");
		JSONArray data = new JSONArray();
		for (Unit unit : list) {
			JSONObject node = new JSONObject();

			node.element("id", unit.getId());
			node.element("text", unit.getName());
			node.element("children", unit.isHasChildren());

			data.add(node);
		}
		return data;
	}
	
	
	@RequestMapping(value = "/grade/tree", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray treeOfGrade() {
		List<Unit> list = new ArrayList<Unit>();

		list = unitService.listForEntity("select id, name, hasChildren from Unit order by code asc");

		JSONArray data = new JSONArray();
		for (Unit unit : list) {
			JSONObject node = new JSONObject();

			node.element("id", unit.getId());
			node.element("text", unit.getName());
			node.element("children", unit.isHasChildren());

			data.add(node);
		}
		return data;
	}
	
	/**
	 * 获取子节点
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/tree/{pid}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray tree(@PathVariable String pid) {
		List<Unit> list = unitService.listForEntity("select id, name, hasChildren from Unit where parentId='" + pid + "'  order by code asc");
		JSONArray data = new JSONArray();
		for (Unit unit : list) {
			JSONObject node = new JSONObject();

			node.element("id", unit.getId());
			node.element("text", unit.getName());
			node.element("children", unit.isHasChildren());

			data.add(node);
		}
		return data;
	}

	@RequestMapping(value = "/add", method = { RequestMethod.GET })
	public String add(Model model, String pid) {
		// 判断是否有机构，没有机构则页面上不可以进行上级机构的选择
		model.addAttribute("noUnit", unitService.count() <= 0);
		if (ObjectUtil.isNotNull(pid)) {
			// 获取父节点信息
			Unit parentUnit = unitService.getByHql("select id,name from Unit where id='" + pid + "'");
			if (ObjectUtil.isNotNull(parentUnit)) {
				model.addAttribute("parentUnit", parentUnit);
			}
		}
		// 判断当前机构是否是系统机构，如果不是系统机构不允许出现系统机构按钮
		model.addAttribute("isSystemUnit", isSystemUnit());
		return "system/unit/add";
	}

	@RequestMapping(value = "/add/do", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject doAdd(Unit unit) {
		try {
			if (StringUtil.isBlank(unit.getName()))
				return WebResult.error("机构名称不能为空！");
			if (StringUtil.isEmpty(unit.getParentId())) {
				if (isSystemUnit()) {
					unit.setParentId("-1");
				} else {
					return WebResult.error("必须选择一个上级机构！");
				}
			}
			unit.setCreateTime(getCurrentTime());
			// 判断此机构名称，在当前父机构下是否存在
			if (unitService.count(Restrictions.eq("parentId", unit.getParentId()), Restrictions.eq("name", unit.getName())) > 0)
				return WebResult.error("'" + unit.getName() + "' 已存在！");
			// 生成编码
			generateCode(unit);
			unitService.save(unit);
			if (!unit.getParentId().equals("-1"))
				unitService.executeHQL("update Unit set hasChildren=true where id='" + unit.getParentId() + "'");

			logger.debug(String.format("新增机构：[Id:%s,Name:%s,ParentId:%s]", unit.getId(), unit.getName(), unit.getParentId()));
			logService.info("机构管理", String.format("新增机构：[Id:%s,Name:%s,ParentId:%s]", unit.getId(), unit.getName(), unit.getParentId()), getCurrentUser().getUserName(),
					getRequestAddr());
			return WebResult.success().element("id", unit.getId());
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 生成编码
	 * 
	 * @param unit
	 */
	private void generateCode(Unit unit) {
		// 生成unit的code
		Unit lastUnit = unitService.getByHql("select code from Unit where parentId='" + unit.getParentId() + "' order by code desc");
		if (ObjectUtil.isNull(lastUnit)) {
			lastUnit = new Unit();
			lastUnit.setCode("000");
		}
		if ("-1".equals(unit.getParentId())) {// 顶级机构
			int count = Integer.parseInt(lastUnit.getCode());
			count = count + 1;
			DecimalFormat df = new DecimalFormat("000");
			unit.setCode(df.format(count));
		} else {// 非顶级机构
			String code = lastUnit.getCode();
			int count = Integer.parseInt(code.substring(code.length() - 3));
			Unit parentUnit = unitService.getByHql("select code from Unit where id='" + unit.getParentId() + "'");
			count = count + 1;
			DecimalFormat df = new DecimalFormat("000");
			unit.setCode(parentUnit.getCode() + df.format(count));
		}
	}

	/**
	 * 首页TreeTable子机构
	 * 
	 * @param model
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/child/{pid}", method = { RequestMethod.GET, RequestMethod.POST })
	public String children(Model model, @PathVariable String pid) {
		List<Unit> list = unitService.listForEntity("select id, name, address, parentId, enabled, hasChildren from Unit where parentId='" + pid + "'  order by code asc");
		model.addAttribute("list", list);
		return "system/unit/children";
	}

	/**
	 * 机构详情
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String detail(Model model, @PathVariable String id) {
		Unit unit = unitService.getById(id);
		if (!ObjectUtil.isNotNull(unit))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		if (StringUtil.isBlank(unit.getAddress()))
			unit.setAddress("未登记地址");
		model.addAttribute("unit", unit);
		// 不等于-1，查询上级菜单
		if (!unit.getParentId().equals("-1")) {
			Unit parentUnit = unitService.getByHql("select name from Unit where id='" + unit.getParentId() + "'");
			if (ObjectUtil.isNotNull(parentUnit)) {
				model.addAttribute("parentUnit", parentUnit.getName());
			}
		}
		logger.debug(String.format("查看机构信息：[Id:%s,Name:%s,ParentId:%s]", unit.getId(), unit.getName(), unit.getParentId()));
		logService.info("机构管理", String.format("查看机构信息：[Id:%s]", unit.getId()), getCurrentUser().getUserName(), getRequestAddr());
		return "system/unit/detail";
	}

	/**
	 * 机构菜单
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/res/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String detailRes(Model model, @PathVariable String id) {
		Unit unit = unitService.getById_noLazyRes(id);
		if (!ObjectUtil.isNotNull(unit))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		if (StringUtil.isBlank(unit.getAddress()))
			unit.setAddress("未登记地址");
		model.addAttribute("unit", unit);
		// 不等于-1，查询上级菜单
		if (!unit.getParentId().equals("-1")) {
			Unit parentUnit = unitService.getByHql("select name from Unit where id='" + unit.getParentId() + "'");
			if (ObjectUtil.isNotNull(parentUnit)) {
				model.addAttribute("parentUnit", parentUnit.getName());
			}
		}
		// 获取用户菜单资源
		List<Resource> resList = unit.getResources();
		List<Resource> firstResList = new ArrayList<Resource>();
		JSONArray jsonResList = new JSONArray();
		for (Resource res : resList) {
			if (res.getParentId().equals("-1")) {
				firstResList.add(res);
			}
			// 防止由于roles延迟加载导致出错
			res.setRoles(null);
			JSONObject jsonRes = JSONObject.fromObject(res);
			jsonResList.add(jsonRes);
		}
		model.addAttribute("allRes", jsonResList.toString());
		model.addAttribute("resList", firstResList);
		logger.debug(String.format("查看机构菜单：[Id:%s,Name:%s,ParentId:%s]", unit.getId(), unit.getName(), unit.getParentId()));
		logService.info("机构管理", String.format("查看机构菜单：[Id:%s]", unit.getId()), getCurrentUser().getUserName(), getRequestAddr());
		return "system/unit/detail_right";
	}

	/**
	 * 编辑机构
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String edit(Model model, @PathVariable String id) {
		Unit unit = unitService.getById(id);
		if (!ObjectUtil.isNotNull(unit))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		model.addAttribute("unit", unit);
		// 不等于-1，查询上级菜单
		if (!unit.getParentId().equals("-1")) {
			Unit parentUnit = unitService.getByHql("select name from Unit where id='" + unit.getParentId() + "'");
			if (ObjectUtil.isNotNull(parentUnit)) {
				model.addAttribute("parentUnit", parentUnit.getName());
			}
		} else {
			model.addAttribute("parentUnit", "顶级机构");
		}
		// 判断当前机构是否是系统机构，如果不是系统机构不允许出现系统机构按钮
		model.addAttribute("isSystemUnit", isSystemUnit());
		return "system/unit/edit";
	}

	@RequestMapping(value = "/edit/do", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject doEdit(Unit unit) {
		try {
			if (StringUtil.isBlank(unit.getId()))
				return WebResult.error("非法操作！");
			if (StringUtil.isBlank(unit.getName()))
				return WebResult.error("机构名称不能为空！");
			if (StringUtil.isBlank(unit.getParentId()))
				unit.setParentId("-1");
			unit.setCreateTime(getCurrentTime());
			// 判断此机构名称，在当前父机构下是否存在
			if (unitService.count(Restrictions.eq("parentId", unit.getParentId()), Restrictions.eq("name", unit.getName()), Restrictions.not(Restrictions.eq("id", unit.getId()))) > 0)
				return WebResult.error("'" + unit.getName() + "' 已存在！");

			Unit eqUnit = unitService.getById(unit.getId());
			String oldPid = eqUnit.getParentId();
			if (ObjectUtil.isNull(eqUnit))
				return WebResult.error("非法操作！");
			eqUnit.setName(unit.getName());
			eqUnit.setAddress(unit.getAddress());
			eqUnit.setEmail(unit.getEmail());
			eqUnit.setEnabled(unit.isEnabled());
			eqUnit.setContact(unit.getContact());
			eqUnit.setMobile(unit.getMobile());
			eqUnit.setWeb(unit.getWeb());
			eqUnit.setSystem(unit.isSystem());
			// 判断父节点是否变化
			if (!eqUnit.getParentId().equals(unit.getParentId())) {// 如果改变了父节点，那么重新生成编号
				eqUnit.setParentId(unit.getParentId());
				generateCode(eqUnit);
			}
			unitService.merge(eqUnit);
			// 更新现在的父节点的子节点状态
			if (!eqUnit.getParentId().equals("-1"))
				unitService.executeHQL("update Unit set hasChildren=true where id='" + unit.getParentId() + "'");
			// 更新之前的父节点的子节点状态
			if (!oldPid.equals("-1") && !oldPid.equals(unit.getParentId()))
				unitService.executeHQL("update Unit set hasChildren=true where id='" + oldPid + "'");
			// 判断是否有子节点
			int count = unitService.count(Restrictions.eq("parentId", unit.getParentId()));
			if (count == 0) {
				unitService.executeHQL("update Unit set hasChildren=false where id='" + unit.getParentId() + "'");
			}
			if (!oldPid.equals(unit.getParentId())) {
				count = unitService.count(Restrictions.eq("parentId", oldPid));
				if (count == 0) {
					unitService.executeHQL("update Unit set hasChildren=false where id='" + oldPid + "'");
				}
			}
			logger.debug(String.format("编辑机构：[Id:%s,Name:%s,ParentId:%s]", unit.getId(), unit.getName(), unit.getParentId()));
			logService.info("机构管理", String.format("编辑机构：[Id:%s,Name:%s,ParentId:%s]", unit.getId(), unit.getName(), unit.getParentId()), getCurrentUser().getUserName(),getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONObject delete(@PathVariable String id) {
		try {
			unitService.deleteAndRelated(id);
			logger.debug(String.format("删除机构：[Id:%s]", id));
			logService.info("机构管理", String.format("删除机构：[Id:%s]", id), getCurrentUser().getUserName(),getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 启用
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/enable/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONObject enable(@PathVariable String id) {
		try {
			unitService.executeHQL("update Unit set enabled=true where id='" + id + "'");
			
			logger.debug(String.format("启用机构：[Id:%s]", id));
			logService.info("机构管理", String.format("启用机构：[Id:%s]", id), getCurrentUser().getUserName(),getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 禁用
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/disable/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONObject disable(@PathVariable String id) {
		try {
			unitService.executeHQL("update Unit set enabled=false where id='" + id + "'");
			
			logger.debug(String.format("禁用机构：[Id:%s]", id));
			logService.info("禁用管理", String.format("禁用机构：[Id:%s]", id), getCurrentUser().getUserName(),getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}

	/**
	 * 机构设置菜单
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/res/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String res(Model model, @PathVariable String id) {
		Unit unit = unitService.getById_noLazyRes(id);
		if (ObjectUtil.isNotNull(unit) && !unit.isSystem()) {
			model.addAttribute("unit", unit);
			List<String> unitIds = getCurrentUserUnitIdList();
			if (unitIds.size() > 0)
				model.addAttribute("currentUserUnitId", getCurrentUserUnitIdList().get(0));
			model.addAttribute("isSystemUnit", isSystemUnit());
			JSONArray array = new JSONArray();
			for (Resource res : unit.getResources()) {
				array.add(res.getId());
			}
			model.addAttribute("data", array);
			return "system/unit/res";
		} else {
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}

	/**
	 * 执行机构设置菜单
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/res/do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONObject doRes(String id, String resIds) {
		try {
			Unit unit = unitService.getById_noLazyRes(id);
			if (ObjectUtil.isNotNull(unit) && !unit.isSystem()) {
				unit.getResources().clear();
				if ("all".equals(resIds)) {
					unit.setResources(resService.list());
				} else {
					String[] ids = resIds.split(",");
					for (String rid : ids) {
						Resource res = new Resource();
						res.setId(rid);
						unit.getResources().add(res);
					}
					unitService.merge(unit);
				}
				logger.debug(String.format("编辑机构菜单：[Id:%s]", id));
				logService.info("机构管理", String.format("编辑机构菜单：[Id:%s,菜单数:%d]", id, unit.getResources().size()), getCurrentUser().getUserName(),getRequestAddr());
				return WebResult.success();
			} else {
				return WebResult.error("非法访问");
			}
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}

	@RequestMapping(value = "/res/tree/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray roleRestree(@PathVariable String id) {
		JSONArray data = new JSONArray();

		Unit unit = unitService.getById_noLazyRes(id);
		if (ObjectUtil.isNotNull(unit)) {
			List<Resource> list = unit.getResources();
			for (Resource res : list) {
				if (!res.getParentId().equals("-1"))
					continue;
				JSONObject node = new JSONObject();

				node.element("id", res.getId());
				node.element("text", res.getName());
				node.element("icon", res.getIcon());
				node.element("data", res.getDescript());
				node.element("children", res.isHasChildren());

				data.add(node);
			}
		}
		return data;
	}

	/**
	 * 获取子节点
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/res/tree/{id}/{pid}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray roleRestreeChild(@PathVariable String id, @PathVariable String pid) {
		JSONArray data = new JSONArray();

		Unit unit = unitService.getById_noLazyRes(id);
		if (ObjectUtil.isNotNull(unit)) {
			List<Resource> list = unit.getResources();
			for (Resource res : list) {
				if (!res.getParentId().equals(pid))
					continue;
				JSONObject node = new JSONObject();

				node.element("id", res.getId());
				node.element("text", res.getName());
				node.element("icon", res.getIcon());
				node.element("data", res.getDescript());
				node.element("children", res.isHasChildren());

				data.add(node);
			}
		}
		return data;
	}
	
	@RequestMapping(value = "/edit/name", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject edit_name(String pk, String name, String value) {
		try {
			unitService.executeHQL("update Unit set name=? where id=?", value, pk);
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/edit/address", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject edit_address(String pk, String name, String value) {
		try {
			unitService.executeHQL("update Unit set address=? where id=?", value, pk);
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.getMessage());
		}
	}
	
	/**
	 * 获取上级机构的菜单，并复制到给本身
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/copyres/parent", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject copyResFromParent(String id) {
		try {
			//获取机构
			Unit unit = unitService.getById_noLazyRes(id);
			if(ObjectUtil.isNull(unit)) return WebResult.error("机构不存在");
			if(unit.getParentId().equals("-1")) return WebResult.error("无上级机构");
			
			Unit pUnit = unitService.getById_noLazyRes(unit.getParentId());
			
			unit.getResources().clear();
			unit.getResources().addAll(pUnit.getResources());
			
			unitService.update(unit);
			
			return WebResult.success().element("count", unit.getResources().size());
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
	
	
	@RequestMapping(value = "/copyres/chilren", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject copyResToChilren(String id) {
		try {
			//获取机构
			Unit unit = unitService.getById_noLazyRes(id);
			if(ObjectUtil.isNull(unit)) return WebResult.error("机构不存在");
			if(unit.getResources().size() == 0) return WebResult.error("机构下无菜单权限");
			List<Unit> cUnits = unitService.list_noLazyRes(Condition.NEW().eq("parentId", id));
			if(cUnits.size() == 0) return WebResult.error("此机构下无子机构");
			//将此机构的菜单复制到下级机构
			for(Unit cUnit : cUnits){
				List<Resource> pResList = unit.getResources();
				List<Resource> cResList = cUnit.getResources();
				
				for(Resource res : pResList){
					//判断菜单是否存在于此机构下
					boolean eqRes = false;
					for(Resource cRes : cResList){
						if(cRes.getId().equals(res.getId())){
							eqRes = true;
							break;
						}
					}
					if(!eqRes){
						cResList.add(res);
					}
				}
			}
			unitService.update(cUnits);
			return WebResult.success().element("count", unit.getResources().size()).element("count2", cUnits.size());
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e);
		}
	}
}
