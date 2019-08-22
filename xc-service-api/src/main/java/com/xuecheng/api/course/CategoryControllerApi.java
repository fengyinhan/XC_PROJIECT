package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Api(value="课程分类管理接口",description = "查询课程的分类")
public interface CategoryControllerApi {
    @ApiOperation("查询课程分类")
    public CategoryNode findList();

}
