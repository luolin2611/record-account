package cn.rollin.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author rollin
 * @date 2020年4月2日15:08:34
 * <p></p>
 * 概述：
 * <p>1.块加密说明</p>
 * <p>Cipher cipher = Cipher.getInstance("A/B/C");</p>
 * <p>A、加密的方式；比如：RSA、AES</p>
 * <p>B、加密模式；ECB（不需要向量）、CBC（需要向量）</p>
 * <p>C、在采用AES、DES等块加密时，有时需要对不满足一个整块（block）的部分需要进行填充；（ZeroPadding、PKCS5Padding与PKCS7Padding）</p>
 * <p>ZeroPadding，数据长度不对齐时使用0填充，否则不填充。使用0填充有个缺点，当元数据尾部也存在0时，在unpadding时可能会存在问题。</p>
 * <p>PKCS7Padding</p>
 * <p>假设每个区块大小为blockSize</p>
 * <p><1>已对齐，填充一个长度为blockSize且每个字节均为blockSize的数据。</p>
 * <p><2>未对齐，需要补充的字节个数为n，则填充一个长度为n且每个字节均为n的数据。</p>
 * <p>PKCS7Padding</p>
 * <p>PKCS7Padding的子集，只是块大小固定为8字节。两者的区别在于PKCS5Padding是限制块大小的PKCS7Padding</p>
 * <p></p>
 * <p>2.补充说明</p>
 * <p>2.1 AES、DES加密的方式位块加密，所以可以选择ZeroPadding、PKCS5Padding与PKCS7Padding这三种填充方式；对于不满足一整块进行填充</p>
 * <p>2.2 比如常见的加密方式的定义:Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");</p>
 */
public class AESUtils {
    public static String encrypt(String plaintext, String secretKey) {
        String ciphertext = null;
        try {
            byte[] encrypted = null;
            try {
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                byte[] raw = secretKey.getBytes();
                SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                encrypted = cipher.doFinal(plaintext.getBytes("utf-8"));
                ciphertext = new String(new Base64().encode(encrypted));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciphertext;
    }

    public static String decrypt(String ciphertext, String secretKey) {
        String plaintext = null;
        try {
            byte[] raw = secretKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decodeBase64(ciphertext);
            byte[] original = cipher.doFinal(encrypted1);
            plaintext = new String(original, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plaintext;
    }
}
