package org.springside.examples.bootapi.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.bootapi.domain.XbCourse;
import org.springside.examples.bootapi.domain.XbCoursePreset;

import java.util.List;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * CrudRepository默认有针对实体对象的CRUD方法.
 * 
 * Spring Data JPA 还会解释新增方法名生成新方法的实现.
 */
public interface XbCoursePresetDao extends PagingAndSortingRepository<XbCoursePreset, String>,JpaSpecificationExecutor<XbCoursePreset> {
    List<XbCoursePreset> findByCourseId(String courseid);
    @Query(value = "SELECT * FROM xb_course_preset WHERE organ_ids = ?1 GROUP BY course_id,organ_ids",nativeQuery = true)
    List<XbCoursePreset> findCourseListByOrginid(String orgid);

}
