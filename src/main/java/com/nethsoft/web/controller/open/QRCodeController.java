package com.nethsoft.web.controller.open;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.nethsoft.app.qrcode.MatrixToImageWriter;
import com.nethsoft.web.controller.BaseController;

/**
 * 二维码生成
 * @author zengc
 *
 */
@Controller
@RequestMapping("/open/qr")
public class QRCodeController extends BaseController<Object>{
	
	/**
	 * 生成二维码
	 * @param request
	 * @param response
	 * @param content
	 */
	@RequestMapping(method = { RequestMethod.GET })
	public void index(HttpServletRequest request, HttpServletResponse response, String content) {
		int width = 100, height = 100;
		String format = "png";
		Hashtable<EncodeHintType, String> hints = new Hashtable<>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
			MatrixToImageWriter.writeToStream(bitMatrix, format, response.getOutputStream());
			response.getOutputStream().close();
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
