package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 课程管理dao
 */
public interface CourseBaseRepository extends JpaRepository<CourseBase,String> {
}
