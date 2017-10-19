package com.nethsoft.web.controller.open.alumni;

import com.nethsoft.core.support.ApplicationCoreConfigHelper;
import com.nethsoft.core.util.Md5Util;
import com.nethsoft.web.entity.alumni.AlumniDonate;
import com.nethsoft.web.service.alumni.AlumniDonateService;
import com.nethsoft.web.support.BaseObject;
import com.nethsoft.web.support.Constant;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;


@Controller
@RequestMapping(value = "/open/alumni/payment/")
public class OpenAlumniPaymentController extends BaseObject{
	private Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private AlumniDonateService alumniDonateService;

	@RequestMapping(value="callback/alipay")
	@ResponseBody
	public synchronized String alipay(@RequestParam Map<String, String> params){
		//接口是否调用成功
		String is_success = params.get("trade_status");
		logger.error(is_success);
		if("TRADE_SUCCESS".equals(is_success)){
			return "success";
		}else{
			return "error";
		}
	}

	// 商户提供的API密钥
//	private String key = ApplicationCoreConfigHelper.getProperty("pay.wechat.key");
	private String key = "";

	@RequestMapping(value="callback/wechat")
	@ResponseBody
	public synchronized String wechat(HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> params){
		String orderNum = "";
		try {
			// 处理接收消息
			ServletInputStream in = request.getInputStream();
			// 将流转换为字符串
			StringBuilder xmlMsg = new StringBuilder();
			byte[] b = new byte[4096];
			for (int n; (n = in.read(b)) != -1;) {
				xmlMsg.append(new String(b, 0, n, "UTF-8"));
			}
			// 将字符串流转成map
			Map<String, String> map = parse(xmlMsg.toString());
			if (null != map && map.keySet().size() > 0) {
				// 判断状态
				if ("SUCCESS".equals(map.get("return_code"))) {
					String stringA = this.createLinkString(map);
					String stringSignTemp = stringA + "&key=" + key;
					String _sign = Md5Util.encode(stringSignTemp).toUpperCase();
					String sign = map.get("sign");
					// 验证签名
					if (_sign.equals(sign)) {
						String product_id = map.get("product_id");
						AlumniDonate alumniDonate = alumniDonateService.get(Restrictions.eq("orderNum",product_id));
						alumniDonate.setState(Constant.STATE_ALUMNI_DONATE_DONE);
						alumniDonateService.update(alumniDonate);
						return return2WeChat("SUCCESS");
					} else {
						return return2WeChat("FAIL");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return return2WeChat("FAIL");
	}

	/**
	 * describe:将返回的xml 封装成map集合
	 * name:parse
	 * reutnType:Map<String,String>
	 * author:liJun
	 * date:2016年4月28日 下午3:42:46
	 */
	private Map<String, String> parse(String protocolXML) {
		Map<String, String> xmlMap = new HashMap<String, String>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(protocolXML)));
			Element root = doc.getDocumentElement();
			NodeList books = root.getChildNodes();
			if (books != null) {
				for (int i = 0; i < books.getLength(); i++) {
					Node book = books.item(i);
					if(null != book.getFirstChild()){
						xmlMap.put(book.getNodeName(),book.getFirstChild().getNodeValue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlMap;
	}


	//对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	private String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if(!"sign".equals(key)){
				if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
					prestr = prestr + key + "=" + value;
				} else {
					prestr = prestr + key + "=" + value + "&";
				}
			}

		}
		return prestr;
	}


	/**
	 * describe: 封装成xml返回给微信服务器
	 * name:return2WeChat
	 * reutnType:String
	 * author:liJun
	 * date:2016年5月6日 下午2:45:09
	 */
	private String return2WeChat(String status){
		String xmlString =
				"<xml>"+
						"<return_code>"+"<![CDATA["+status+"]]>"+"</return_code>"+
						//"<return_msg>"+"<![CDATA[OK]]>"+"</return_msg>"+
						"</xml>";
		return xmlString;
	}

}
