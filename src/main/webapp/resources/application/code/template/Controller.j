package ${pack};

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.nethsoft.core.util.ObjectUtil;
import com.nethsoft.core.web.support.ControllerCommon;
import com.nethsoft.core.web.support.WebResult;
import com.nethsoft.orm.query.PageBean;
import com.nethsoft.web.controller.BaseController;
import ${service_pack}.${className}Service;
import ${entity_pack}.${className};

@Controller
@RequestMapping(value="${requestMapping}")
public class ${className}Controller extends BaseController<${className}>{
	
	private Logger logger = Logger.getLogger(this.getClass());
	private final String TPL = "${requestMapping}/";
	
	@Autowired
	private ${className}Service ${classNameLower}Service;
	
	/**
	 * 首页
	 */
	@RequestMapping(method={RequestMethod.GET, RequestMethod.POST})
	public String index(Model model, PageBean pageBean){
		List<${className}> list = ${classNameLower}Service.listByPage(pageBean);
		model.addAttribute("list", list);
		return TPL + "index";
	}
	
	/**
	 * 转向新增页面
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String add(Model model, String pid){
		return TPL + "add";
	}
	
	/**
	 * 执行新增
	 */
	@RequestMapping(value="/add/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(${className} ${classNameLower}){
		try {
			//TODO 新增代码
			${classNameLower}Service.save(${classNameLower});
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.toString());
		}
	}
	
	/**
	 * 转向编辑页面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}", method={RequestMethod.GET, RequestMethod.POST})
	public String edit(Model model, @PathVariable String id){
		${className} ${classNameLower} = ${classNameLower}Service.getById(id);
		if(!ObjectUtil.isNotNull(${classNameLower}))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		model.addAttribute("${classNameLower}", ${classNameLower});
		return TPL + "edit";
	}
	
	@RequestMapping(value="/edit/do", method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(${className} ${classNameLower}){
		try {
			//TODO 校验属性
			
			//从数据库中获取最新数据
			${className} eq${className} = ${classNameLower}Service.getById(${classNameLower}.getId());
			if(ObjectUtil.isNull(eq${className})){
				return WebResult.error("非法操作");
			}
			//TODO 设置需要修改的属性值
			
			${classNameLower}Service.merge(eq${className});
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.toString());
		}
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delete/{id}", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public JSONObject delete(@PathVariable String id){
		try {
			//删除相关的所有数据
			${classNameLower}Service.delete(id);
			return WebResult.success();
		} catch (Exception e) {
			logger.error(e);
			return WebResult.error(e.toString());
		}
	}
	
	/**
	 * 查看详情
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/detail/{id}", method={RequestMethod.GET, RequestMethod.POST})
	public String detail(Model model, @PathVariable String id){
		${className} ${classNameLower} = ${classNameLower}Service.getById(id);
		if(!ObjectUtil.isNotNull(${classNameLower}))
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		model.addAttribute("${classNameLower}", ${classNameLower});
		return TPL + "detail";
	}
}
