package com.nethsoft.web.support.qrcode;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.nethsoft.app.qrcode.MatrixToImageWriter;
import com.nethsoft.web.controller.common.UploadController;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码相关的工具类
 * Created by cf on 2016/5/24.
 */
public class QRCodeUtil {

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 生成二维码图片
     * @param content  二维码内容
     */
    public static String createCodeFile(String content) {
        String path = "";
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 400, 400,hints);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIO.write(bufferedImage, "jpg", out);
            path = UploadController.upload(out.toByteArray(),"jpg");
        }catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }
    
    
    	
    /**
     * 解析二维码图片
     */
    public static void decodeCodeFile(){
        try {
            MultiFormatReader formatReader = new MultiFormatReader();
            String filePath = "f:/code.jpg";
            File file = new File(filePath);
            BufferedImage image = ImageIO.read(file);;
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer  binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            Result result = formatReader.decode(binaryBitmap,hints);
            System.out.println("result = "+ result.toString());
            System.out.println("resultFormat = "+ result.getBarcodeFormat());
            System.out.println("resultText = "+ result.getText());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
