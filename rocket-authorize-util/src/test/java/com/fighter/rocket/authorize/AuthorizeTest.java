package com.fighter.rocket.authorize;

import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by hanwm on 17/3/28.
 */
public class AuthorizeTest {

    @Test
    public void testGetQueryString3() {

        System.out.print("queryStr:");

        String queryStr = RocketAuthorize.getQueryString("music", "YouMustModifyThisToAnOtherString",
                "600000161", 24l * 60 * 60 * 1000 * 100);

        //String queryStr = ApushAuthorize.getQueryString("music", "musicdf14422d",
        //"600000161", 24l * 60 * 60 * 1000 * 100);
        System.out.println(queryStr);
    }

    @Test
    public void testGetToken() {

        System.out.print("token:");

        String token = RocketAuthorize.getToken("appTest1", "YouMustModifyThisToAnOtherString",
                "userTest1", 24l * 60 * 60 * 1000 * 100);
        System.out.println(token);
    }

    @Test
    public void testGetTopicToken() {

        System.out.print("token:");

        String token = RocketAuthorize.getTopicToken("appTest1", "YouMustModifyThisToAnOtherString",
                "userTest1", "topicTest1", 24l * 60 * 60 * 1000 * 10000);
        System.out.println(token);
    }

    @Test
    public void testDecrypt() throws Exception {
        String cnt = "asdfsaddf";
        String password = "initKey()";
        String rt = RocketAuthorize.encrypt(cnt, password);
        String cnt2 = RocketAuthorize.decrypt(rt, password);
        assertEquals(cnt, cnt2);
        cnt = "asdfsaddf我们";
        password = "中文";
        rt = RocketAuthorize.encrypt(cnt, password);
        cnt2 = RocketAuthorize.decrypt(rt, password);
        assertEquals(cnt, cnt2);
        cnt = "asdfsaddf我们3455";
        password = "中文asdfsaddf我们";
        rt = RocketAuthorize.encrypt(cnt, password);
        cnt2 = RocketAuthorize.decrypt(rt, password);
        assertEquals(cnt, cnt2);
        cnt = "asdfsaddf我们3455";
        password = "a";
        rt = RocketAuthorize.encrypt(cnt, password);
        cnt2 = RocketAuthorize.decrypt(rt, password);
        assertEquals(cnt, cnt2);
    }

    @Test
    public void testDecrypt3() throws Exception {
        String cnt = "asdfsaddf";
        // String password = initKey();
        String password = "initKey()";

        byte[] content = cnt.getBytes("UTF-8");

        // byte[] keys = Base64.decodeBase64(password);
        byte[] keys = getKey(password);
        SecretKeySpec key = new SecretKeySpec(keys, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(content);

        key = new SecretKeySpec(keys, "AES");
        Cipher cipher2 = Cipher.getInstance("AES");
        cipher2.init(Cipher.DECRYPT_MODE, key);

        byte[] result2 = cipher2.doFinal(result);
        String cnt2 = new String(result2, "UTF-8");
        assertEquals(cnt, cnt2);
    }

    private byte[] getKey(String password) throws UnsupportedEncodingException {
        byte[] tmp = password.getBytes("UTF-8");
        byte[] key = Arrays.copyOf(tmp, 16);
        return key;
    }

}
