package com.fighter.rocket.authorize;

import com.google.common.base.Preconditions;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by hanwm on 17/3/28.
 */
public class RocketAuthorize {

    private static Logger logger = LoggerFactory.getLogger(RocketAuthorize.class);

    /**
     * 生成客户端登录时需要的url参数
     *
     * @param appId 要订阅的的app id
     * @param appKey 申请的app key
     * @param userId 用户id
     * @param expireMilliseconds
     *            token will expired after expireMilliseconds milliseconds,must > 0
     * @return return a queryString as
     *         "?param=8887-112-7-113-15-35976542-8354101783113,app1,PWb5fRPjMLlPigMbH%2FJlJUyS2Sli1c6lOYIhAaQtiQA%3D%0A"
     */
    public static String getQueryString(String appId, String appKey, String userId, long expireMilliseconds) {
        try {
            String token = getToken(appId, appKey, userId, expireMilliseconds);

            String urlToken = URLEncoder.encode(token, "ISO-8859-1");

            String md5 = DigestUtils.md5Hex(userId);
            StringBuilder sb = new StringBuilder(128);
            sb.append("?param=").append(md5).append(",").append(appId).append(",").append(urlToken);
            return sb.toString();
        } catch (Exception ex) {
            logger.error("getQueryString failed", ex);
            throw new RuntimeException("getQueryString failed", ex);
        }
    }

    /**
     * 生成客户端订阅app消息时需要的token，订阅非主app或者断开重连token失效时需要调用此方法，一般由客户端通过ajax调用app服务端，
     * app服务端调用此方法
     *
     * @param appId 要订阅的的app id
     * @param appKey 申请的app key
     * @param userId 用户id
     * @param expireMilliseconds
     *            token will expired after expireMilliseconds milliseconds,must > 0
     * @return token
     */
    public static String getToken(String appId, String appKey, String userId, long expireMilliseconds) {

        try {
            Preconditions.checkArgument(!appId.contains("_t_"), "appId can not include '_t_'");
            Preconditions.checkArgument(!appId.endsWith("_t"), "appId can not ends with '_t'");
            Preconditions.checkArgument(!appId.contains(","), "appId can not include ','");

            String expireTime = String.valueOf(System.currentTimeMillis() + expireMilliseconds);
            StringBuilder tokenSb = new StringBuilder(32).append("token=").append(expireTime);
            tokenSb.append(',').append(userId);
            return encrypt(tokenSb.toString(), appKey);
        } catch (Exception ex) {
            logger.error("getToken failed", ex);
            throw new RuntimeException("getToken failed", ex);
        }
    }

    /**
     * 生成客户端订阅topic消息时需要的token，订阅topic或者断开重连token失效时需要调用此方法
     *
     * @param appId 要订阅的的app id
     * @param appKey 申请的app key
     * @param userId 用户id
     * @param topic 要订阅的主题
     * @param expireMilliseconds
     *            token will expired after expireMilliseconds milliseconds,must > 0
     * @return token
     */
    public static String getTopicToken(String appId, String appKey, String userId, String topic, long expireMilliseconds) {

        try {
            Preconditions.checkArgument(!appId.contains("_t_"), "appId can not include '_t_'");
            Preconditions.checkArgument(!appId.endsWith("_t"), "appId can not ends with '_t'");
            Preconditions.checkArgument(!appId.contains(","), "appId can not include ','");
            Preconditions.checkArgument(!appId.contains("_t_"), "topic can not include '_t_'");
            Preconditions.checkArgument(!appId.contains(","), "topic can not include ','");

            String expireTime = String.valueOf(System.currentTimeMillis() + expireMilliseconds);
            StringBuilder tokenSb = new StringBuilder(32).append("token=").append(expireTime);
            tokenSb.append(',').append(userId).append(',').append(topic);
            return encrypt(tokenSb.toString(), appKey);
        } catch (Exception ex) {
            logger.error("getTopicToken failed", ex);
            throw new RuntimeException("getTopicToken failed", ex);
        }
    }

    public static String encrypt(String cnt, String password) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        byte[] content = cnt.getBytes("UTF-8");
        SecretKeySpec key = new SecretKeySpec(getKey(password), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(content);
        return new String(Base64.encodeBase64(result, false), "UTF-8");
    }

    public static String decrypt(String cnt, String password) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        byte[] content = Base64.decodeBase64(cnt);
        SecretKeySpec key = new SecretKeySpec(getKey(password), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] result = cipher.doFinal(content);
        return new String(result, "UTF-8");
    }

    private static byte[] getKey(String password) throws UnsupportedEncodingException {
        byte[] tmp = password.getBytes("UTF-8");
        return Arrays.copyOf(tmp, 16);
    }


}
