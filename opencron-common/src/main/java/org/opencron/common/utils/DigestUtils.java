package org.opencron.common.utils;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * @author wanghuajie
 * @since 3.0
 */
public abstract class DigestUtils {

    private final static String DES = "DES";

    private static final String MD5_ALGORITHM_NAME = "MD5";

    private static final char[] HEX_CHARS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    public static byte[] md5Digest(String text) {
        return digest(MD5_ALGORITHM_NAME, text.getBytes());
    }

    public static byte[] md5Digest(byte[] bytes) {
        return digest(MD5_ALGORITHM_NAME, bytes);
    }


    public static String md5Hex(String text) {
        return md5Hex(text.getBytes());
    }

    /**
     * Return a hexadecimal string representation of the MD5 digest of the given
     * bytes.
     *
     * @param bytes the bytes to calculate the digest over
     * @return a hexadecimal digest string
     */
    public static String md5Hex(byte[] bytes) {
        return digestAsHexString(MD5_ALGORITHM_NAME, bytes);
    }

    /**
     * Append a hexadecimal string representation of the MD5 digest of the given
     * bytes to the given {@link StringBuilder}.
     *
     * @param bytes   the bytes to calculate the digest over
     * @param builder the string builder to append the digest to
     * @return the given string builder
     */
    public static StringBuilder appendMd5DigestAsHex(byte[] bytes, StringBuilder builder) {
        return appendDigestAsHex(MD5_ALGORITHM_NAME, bytes, builder);
    }

    /**
     * Creates a new {@link MessageDigest} with the given algorithm. Necessary
     * because {@code MessageDigest} is not thread-safe.
     */
    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", ex);
        }
    }


    private static byte[] digest(String algorithm, byte[] bytes) {
        return getDigest(algorithm).digest(bytes);
    }

    private static String digestAsHexString(String algorithm, byte[] bytes) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return new String(hexDigest);
    }

    private static StringBuilder appendDigestAsHex(String algorithm, byte[] bytes, StringBuilder builder) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return builder.append(hexDigest);
    }

    private static char[] digestAsHexChars(String algorithm, byte[] bytes) {
        byte[] digest = digest(algorithm, bytes);
        return encodeHex(digest);
    }

    private static char[] encodeHex(byte[] bytes) {
        char chars[] = new char[32];
        for (int i = 0; i < chars.length; i = i + 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
            chars[i + 1] = HEX_CHARS[b & 0xf];
        }
        return chars;
    }


    public static void main(String[] args) throws Exception {
        System.out.println(compressData("中华人民共和国"));
        System.out.println(decompressData("eJy7dmH3qhOnD3/cuWnX2Z2/GABciQr4"));

    }

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String key, String data) throws Exception {
        byte[] bt = desEncrypt(key.getBytes(), data.getBytes());
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws java.io.IOException
     * @throws Exception
     */
    public static String desDecrypt(String key, String data) throws Exception {
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = desDecrypt(key.getBytes(), buf);
        return new String(bt);
    }

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] desEncrypt(byte[] key, byte[] data) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }


    // 加密
    public static String toBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    // 解密
    public static String passBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] desDecrypt(byte[] key, byte[] data) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }


    public static String aesDecrypt(String key, String sSrc) throws Exception {
        try {
            if (key == null) {
                throw new ExceptionInInitializerError("key can't be null");
            }
            if (key.length() != 16) {
                throw new ExceptionInInitializerError("key length must be 16!");
            }

            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, skeySpec);
            byte[] encrypted1 = hex2byte(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    /**
     * @param sSrc
     * @return 加密程序
     * @throws Exception
     */
    public static String aesEncrypt(String key, String sSrc) throws Exception {
        if (key == null) {
            throw new ExceptionInInitializerError("key not bo null");
        }

        if (key.length() != 16) {
            throw new ExceptionInInitializerError("key length must be 16!");
        }
        byte[] raw = key.getBytes("ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());
        return byte2hex(encrypted).toLowerCase();
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    //	压缩字符串
    public static String compressData(String data) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DeflaterOutputStream zos = new DeflaterOutputStream(bos);
            zos.write(data.getBytes());
            zos.close();
            return new String(getenBASE64inCodec(bos.toByteArray()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "ZIP_ERR";
        }
    }

    //	压缩字符串
    public static String compressData(String data, String charset) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DeflaterOutputStream zos = new DeflaterOutputStream(bos);
            zos.write(data.getBytes());
            zos.close();
            return new String(getenBASE64inCodec(bos.toByteArray(), charset));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "ZIP_ERR";
        }
    }

    //	使用apche codec对数组进行encode
    public static String getenBASE64inCodec(byte[] b) {
        if (b == null)
            return null;
        return new String((new Base64()).encode(b));
    }

    //	使用apche codec对数组进行encode
    public static String getenBASE64inCodec(byte[] b, String charset) {
        if (b == null)
            return null;
        try {
            return new String((new Base64()).encode(b), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

//	base64转码为string

    public static byte[] getdeBASE64inCodec(String s) {
        if (s == null)
            return null;
        return new Base64().decode(s.getBytes());
    }

    //	解码字符串
    public static String decompressData(String encdata) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InflaterOutputStream zos = new InflaterOutputStream(bos);
            zos.write(getdeBASE64inCodec(encdata));
            zos.close();
            return new String(bos.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            return "UNZIP_ERR";
        }
    }

    //	解码字符串
    public static String decompressData(String encdata, String charset) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InflaterOutputStream zos = new InflaterOutputStream(bos);
            zos.write(getdeBASE64inCodec(encdata));
            zos.close();
            return new String(bos.toByteArray(), charset);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "UNZIP_ERR";
        }
    }

    public static String getEncoding(String text) {
        String encode = "ISO-8859-1";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception1) {
        }

        encode = "GB2312";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception) {}

        encode = "UTF-8";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception2) {
        }

        encode = "GBK";
        try {
            if (text.equals(new String(text.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception exception3) {
        }
        return encode;
    }
}
