package com.ly.activit.core.api.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface CustomMapper {

    @Select("select * from act_ru_task")
    List<Map<String, Object>> findAll();
}
