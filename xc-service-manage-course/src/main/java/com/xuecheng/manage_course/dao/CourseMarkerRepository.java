package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 课堂营销接口
 */
public interface CourseMarkerRepository extends JpaRepository<CourseMarket,String> {
}
