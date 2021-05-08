package com.coder.lee.data.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c)
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/5/8 8:58
 *
 * @author coderLee23
 */
public class CsvUtil {

    private static final Logger logger = LogManager.getLogger(CsvUtil.class);

    private CsvUtil() {

    }

    /**
     * 输出全部属性
     * 如果csv中存在，对象中没有，则自动忽略该属性
     * 失败返回空list
     *
     * @param schema     schema
     * @param csvContent cvs字符串
     * @param clazz      类
     * @return list<T> 对象列表
     */
    public static <T> List<T> parseObject(CsvSchema schema, String csvContent, Class<T> clazz) {
        Assert.notNull(schema, "schema must not be null");
        Assert.hasText(csvContent, "csvContent must not be null or empty");
        Assert.notNull(clazz, "clazz must not be null");
        CsvMapper csvMapper = new CsvMapper();
        try (MappingIterator<T> mappingIterator = csvMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readerFor(clazz).with(schema).readValues(csvContent)) {
            return mappingIterator.readAll();
        } catch (IOException e) {
            logger.error("CsvToObject failed!", e);
        }
        return Collections.emptyList();
    }

    /**
     * 输出全部属性
     * 如果csv中存在，对象中没有，则自动忽略该属性
     * 失败返回空list
     *
     * @param schema schema
     * @param bytes  字节数组
     * @param clazz  类
     * @return list<T> 对象列表
     */
    public static <T> List<T> parseObject(CsvSchema schema, byte[] bytes, Class<T> clazz) {
        Assert.notNull(schema, "schema must not be null");
        Assert.isTrue(bytes.length > 0, "bytes length > 0");
        Assert.notNull(clazz, "clazz must not be null");
        CsvMapper csvMapper = new CsvMapper();
        try (MappingIterator<T> mappingIterator = csvMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readerFor(clazz).with(schema).readValues(bytes)) {
            return mappingIterator.readAll();
        } catch (IOException e) {
            logger.error("CsvToObject failed!", e);
        }
        return Collections.emptyList();
    }


    /**
     * 对象转成csv格式，返回字符串
     *
     * @param csvSchema csvSchema
     * @param object    对象
     * @return string
     */
    public static String toCsv(CsvSchema csvSchema, Object object) {
        Assert.notNull(csvSchema, "schema must not be null");
        Assert.notNull(object, "object must not be null");
        try {
            CsvMapper csvMapper = new CsvMapper();
            return csvMapper.writer(csvSchema).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("ObjToCsv failed！", e);
        }
        return "";
    }

}