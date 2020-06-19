package com.glsx.plat.common.utils.encryption;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 加密工具
 */
public class EncryptUtils {

    //默认字符编码
    private static final String CHARSET_UTF8 = "UTF-8";

    //盐
    private static final byte[] SLAT_ARRAY = new byte[]{24, 17, 61, 78, 9, 45, 13, 55};

    //密钥种子
    private static final String PWD_ARRAY = "web4sVMS";

    /**
     * 对v值进行加密，加密后以base64编码格式输出，如果加密失败，则返回null。
     *
     * @param v :java.lang.String，需要进行加密的值
     * @param t :java.lang.String，8位日期字符串
     * @return java.lang.String
     */
    public static String PBEEncrypt(String v) {
        try {
            return StringUtils.trim(Encrypter.encryptBASE64(PBEEncrypter.encrypt(v.getBytes(CHARSET_UTF8), PWD_ARRAY, SLAT_ARRAY)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对v值进行解密，解密后以base64编码格式输出，如果解密失败，则返回null。
     *
     * @param v :java.lang.String，需要进行解密的值
     * @param t :java.lang.String，8位日期字符串
     * @return java.lang.String
     */
    public static String PBEDecrypt(String v) {
        try {
            return new String(PBEEncrypter.decrypt(PBEEncrypter.decryptBASE64(v), PWD_ARRAY, SLAT_ARRAY));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * md5 加密
     *
     * @param encodeStr
     * @return
     */
    public static String md5Encrypt(String encodeStr, String secret) {
        return DigestUtils.md5Hex(encodeStr + "{" + secret + "}");
    }

    /**
     * sha1 加密
     *
     * @param encodeStr
     * @param secret
     * @return
     */
    public static String sha1Encrypt(String encodeStr, String secret) {
        return DigestUtils.shaHex(encodeStr + "{" + secret + "}");
    }

//    public static void main(String[] args) {
//        System.out.println(md5Encrypt("0000", "0ca175b9"));
//
//        String a = "aRyNpOKTov8=";
//        String t = PBEEncrypt(a);
//        System.out.println(a);
//        System.out.println(t);
//        System.out.println(PBEDecrypt(a));
//    }

}
