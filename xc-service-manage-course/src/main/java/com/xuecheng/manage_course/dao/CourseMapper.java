package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;
import com.github.pagehelper.Page;


@Mapper
public interface CourseMapper {
   CourseBase findCourseBaseById(String id);
   Page<CourseBase> findCourseList();
//通过查询条件获取课程列表
   Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
}
