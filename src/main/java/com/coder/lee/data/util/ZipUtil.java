package com.coder.lee.data.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Description: Function Description
 * Copyright: Copyright (c)
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/5/8 9:40
 *
 * @author coderLee23
 */
public class ZipUtil {

    private static final Logger LOGGER = LogManager.getLogger(CsvUtil.class);

    private ZipUtil() {
    }

    /**
     * 压缩字符串
     *
     * @param content 压缩前字符串
     * @return byte[] 压缩后数组
     * @throws IOException 压缩异常
     */
    public static byte[] deflater(String content) throws IOException {
        Assert.hasText(content, "content must not be null or empty");
        Deflater def = new Deflater(Deflater.BEST_COMPRESSION, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(byteArrayOutputStream, def)) {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            dos.write(bytes);
            dos.flush();
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 解压字节数组
     *
     * @param bytes 字节数组
     * @return string 解压后的字符串
     */
    public static String inflater(byte[] bytes) throws IOException {
        Assert.isTrue(bytes.length > 0, "bytes length > 0");
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Inflater inf = new Inflater(true);
        try (InflaterInputStream iis = new InflaterInputStream(bis, inf)) {
            int readCount = 0;
            byte[] buf = new byte[1024];
            while ((readCount = iis.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, readCount);
            }
            bos.flush();
        }

        return bos.toString(StandardCharsets.UTF_8.name());
    }

}
