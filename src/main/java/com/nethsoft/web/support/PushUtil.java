package com.nethsoft.web.support;

import cn.jpush.api.common.ClientConfig;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

import java.util.Map;

public class PushUtil {

    protected static ClientConfig config = null;

    static {
        config= ClientConfig.getInstance();
        config.setPushHostName("https://api.jpush.cn");
        config.setApnsProduction(true);  //true IOS生产环境，false iOS开发环境
    }

    /**
     * 推送给所有用户 android和ios
     * @param title 标题
     * @param message  内容
     * @return
     */
    protected static PushPayload buildPushObject_android_and_ios(String title,String message) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder().setAlert(message)
                        .addPlatformNotification(AndroidNotification.newBuilder().setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).setSound("happy").build())
                        .build())
                .build();
    }

    /**
     * 推送给所有用户 android和ios
     * @param title 标题
     * @param message  内容
     * @param extras  额外数据
     * @return
     */
    protected static PushPayload buildPushObject_android_and_ios(String title,String message,Map<String, String> extras) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder().setAlert(message)
                        .addPlatformNotification(AndroidNotification.newBuilder().setTitle(title).addExtras(extras).build())
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).setSound("happy").addExtras(extras).build())
                        .build())
                .build();
    }

    /**
     * 推送给所有用户 ios
     * @param title 标题
     * @param message  内容
     * @param extras  额外数据
     * @return
     */
    protected static PushPayload buildPushObject_and_ios(String title,String message,Map<String, String> extras) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder().setAlert(message)
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).setSound("happy").addExtras(extras).build())
                        .build())
                .build();
    }

    /**
     * 根据标签推送  android和ios【交集】
     * @param title  标题
     * @param message  内容
     * @param tagValue  标签
     * @return
     */
    protected static PushPayload buildPushObject_android_and_ios_tag(String title,String message,String... tagValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.tag_and(tagValue))
                .setNotification(Notification.newBuilder().setAlert(message)
                        .addPlatformNotification(AndroidNotification.newBuilder().setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).setSound("happy").build())
                        .build())
                .build();
    }

    /**
     * 根据标签推送  android和ios【交集】
     * @param title  标题
     * @param message  内容
     * @param extras  额外数据
     * @param tagValue  标签
     * @return
     */
    protected static PushPayload buildPushObject_android_and_ios_tag(String title, String message,Map<String, String> extras,String... tagValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.tag_and(tagValue))
                .setNotification(Notification.newBuilder().setAlert(message)
                        .addPlatformNotification(AndroidNotification.newBuilder().setTitle(title).addExtras(extras).build())
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).setSound("happy").addExtras(extras).build())
                        .build())
                .build();
    }

    /**
     * 根据标签推送  ios【交集】
     * @param title  标题
     * @param message  内容
     * @param extras  额外数据
     * @param tagValue  标签
     * @return
     */
    protected static PushPayload buildPushObject_ios_tag(String title, String message,Map<String, String> extras,String... tagValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.tag_and(tagValue))
                .setNotification(Notification.newBuilder().setAlert(message)
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).setSound("happy").addExtras(extras).build())
                        .build())
                .build();
    }

    /**
     * 根据别名推送
     * @param title  标题
     * @param message  内容
     * @param alias  别名
     * @return
     */
    protected static PushPayload buildPushObject_android_and_ios_alias(String title,String message, String alias) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder().setAlert(message)
                        .addPlatformNotification(AndroidNotification.newBuilder().setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).setSound("happy").build())
                        .build())
                .build();
    }

    /**
     * 根据别名推送
     * @param title  标题
     * @param message  内容
     * @param alias  别名
     * @param extras  额外数据
     * @return
     */
    protected static PushPayload buildPushObject_android_and_ios_alias(String title,String message, String alias,Map<String,String> extras) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder().setAlert(message)
                        .addPlatformNotification(AndroidNotification.newBuilder().setTitle(title).addExtras(extras).build())
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).setSound("happy").addExtras(extras).build())
                        .build())
                .build();
    }
}