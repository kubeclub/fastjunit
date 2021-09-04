package com.lucky.ut.effective.utils;


import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * @author xiuyin.cui@luckincoffee.com on 2020/09/27.
 */
public class Base64Utils {

    private static final Logger logger = LoggerFactory.getLogger(Base64Utils.class);

    private static final Base64.Decoder DECODER;
    private static final Base64.Encoder ENCODER;
    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    private Base64Utils() {
    }

    static {
        DECODER = Base64.getDecoder();
        ENCODER = Base64.getEncoder();
    }

    /**
     * BASE64字符串解码为二进制数据
     *
     * @param base64 字符串
     * @return 字节数组
     */
    public static byte[] decode(String base64) {
        return DECODER.decode(base64.replaceAll("\r|\n", ""));
    }

    /**
     * <p>
     * 二进制数据编码为BASE64字符串
     * </p>
     *
     * @param bytes 字节数组
     * @return string
     */
    public static String encode(byte[] bytes) {
        return new String(ENCODER.encode(bytes));
    }

    /**
     * 将文件编码为BASE64字符串;大文件慎用，可能会导致内存溢出
     *
     * @param filePath 文件绝对路径
     * @return string
     */
    public static String encodeFile(String filePath) {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    /**
     * BASE64字符串转回文件
     *
     * @param filePath 文件绝对路径
     * @param base64   编码字符串
     */
    public static void decodeToFile(String filePath, String base64) {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    /**
     * 文件转换为二进制数组
     *
     * @param filePath 文件路径
     * @return byte
     */
    public static byte[] fileToByte(String filePath) {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
                byte[] cache = new byte[CACHE_SIZE];
                int nRead;
                while ((nRead = in.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                out.close();
                in.close();
                data = out.toByteArray();
            } catch (IOException e) {
                logger.error(e, () -> "Base64Utils fileToByte IOException.filePath:" + filePath);
            }
        }
        return data;
    }

    /**
     * 二进制数组转换为文件
     *
     * @param bytes    二进制数据
     * @param filePath 文件生成目录
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) {
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            File destFile = new File(filePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            OutputStream out = new FileOutputStream(destFile);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
        } catch (IOException e) {
            logger.error(e, () -> "Base64Utils byteArrayToFile IOException.filePath:" + filePath);
        }
    }
}
