package com.coder.lee.data.service;

import com.coder.lee.data.dto.PersonDTO;
import com.coder.lee.data.enums.SexEnum;
import com.coder.lee.data.util.CsvUtil;
import com.coder.lee.data.util.ZipUtil;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c)
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/5/8 11:54
 *
 * @author coderLee23
 */
public class DataCsvService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataCsvService.class);

    public static void main(String[] args) throws IOException {
        List<PersonDTO> pojoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pojoList.add(new PersonDTO("firstName:姓" + i, i + 10, SexEnum.getSexEnumByType(i % 3), new Date()));
        }

        CsvSchema schema = CsvSchema.builder()
                .addColumn("name")
                .addColumn("age", CsvSchema.ColumnType.NUMBER)
                .addColumn("sexEnum", CsvSchema.ColumnType.NUMBER)
                .addColumn("birthday", CsvSchema.ColumnType.STRING)
                .build().withUseHeader(false);

        String csv = CsvUtil.toCsv(schema, pojoList);
        LOGGER.info("java对象转成csv数据:\n{}", csv);

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        byte[] deflater = ZipUtil.deflater(csv);
        LOGGER.info("csv数据源数组长度为:{}，csv数据压缩成byte数组长度为:{}", bytes.length, deflater.length);
        String inflater = ZipUtil.inflater(deflater);
        LOGGER.info("压缩数据还原成正常csv数据为:\n{}", inflater);
        List<PersonDTO> personDTOS = CsvUtil.parseObject(schema, inflater, PersonDTO.class);
        LOGGER.info("正常csv数据转成java对象为:\n{}", personDTOS);

    }
}
