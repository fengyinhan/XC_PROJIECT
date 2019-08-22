package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 查询课程分类
 */
@Mapper
public interface CategoryMapper {
    public CategoryNode findList();
}
