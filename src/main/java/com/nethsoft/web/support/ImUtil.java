package com.nethsoft.web.support;

import com.easemob.server.example.httpclient.apidemo.EasemobIMUsers;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.Logger;

public class ImUtil {
    private static final Logger logger = Logger.getLogger(ImUtil.class);

    public static boolean createUser(String userName, String password, String nickname) {
        // 注册单个用户
        ObjectNode objectNode = (ObjectNode) EasemobIMUsers.createNewIMUserSingle(JsonNodeFactory.instance
                .objectNode().put("username", userName).put("password", password).put("nickname", nickname));
        String result = objectNode.toString();
        String statusCode = objectNode.get("statusCode").toString();
        if ("200".equals(statusCode)) {
            logger.info("注册用户[单个]: " + result);
            return true;
        } else if ("400".equals(statusCode)) {
            if ("\"duplicate_unique_property_exists\"".equals(objectNode.get("error").toString())) {
                logger.info("环信账号" + userName + "已存在");
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

}
