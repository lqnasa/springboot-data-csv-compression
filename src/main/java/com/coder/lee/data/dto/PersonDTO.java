package com.coder.lee.data.dto;

import com.coder.lee.data.enums.SexEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Description: Function Description
 * Copyright: Copyright (c)
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/5/8 8:56
 *
 * @author coderLee23
 */
public class PersonDTO {

    private String name;
    private Integer age;
    private SexEnum sexEnum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date birthday;

    public PersonDTO() {
    }

    public PersonDTO(String name, Integer age, SexEnum sexEnum, Date birthday) {
        this.name = name;
        this.age = age;
        this.sexEnum = sexEnum;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public SexEnum getSexEnum() {
        return sexEnum;
    }

    public void setSexEnum(SexEnum sexEnum) {
        this.sexEnum = sexEnum;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sexEnum=" + sexEnum +
                ", birthday=" + birthday +
                '}';
    }
}
