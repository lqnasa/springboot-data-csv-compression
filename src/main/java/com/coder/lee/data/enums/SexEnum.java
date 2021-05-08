package com.coder.lee.data.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * Description: Function Description
 * Copyright: Copyright (c)
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/5/8 8:58
 *
 * @author coderLee23
 */

public enum SexEnum {

    /**
     * 女
     */
    WOMAN(0, "女"),
    /**
     * 男
     */
    MAN(1, "男"),
    /**
     * 保密
     */
    SECRET(2, "保密");

    @JsonValue
    private int type;

    private String value;

    SexEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static SexEnum getSexEnumByType(int type) {
        return Arrays.stream(SexEnum.values())
                .filter(sexEnum -> sexEnum.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("参数不合法:" + type));
    }
}

