package org.springside.examples.bootapi.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.bootapi.domain.XbStudentRelationViewNew;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * CrudRepository默认有针对实体对象的CRUD方法.
 * 
 * Spring Data JPA 还会解释新增方法名生成新方法的实现.
 */
public interface XbStudentRelationViewNewDao extends PagingAndSortingRepository<XbStudentRelationViewNew, String>,JpaSpecificationExecutor<XbStudentRelationViewNew> {
    XbStudentRelationViewNew findById(String id);
    @Query(value = " " +
            " select count(1) " +
            "from `xb_student_relation` `o` " +
            "LEFT JOIN  `xb_student` `t` " +
            "on `o`.`student_id` = `t`.`id` " +
            "where `t`.`delete_status` = 1  " +
            "and `o`.`student_start` in ('0','2')  " +
            "and `o`.`class_id` = ?1  " +
            "and `o`.`period_num` > 0  ",nativeQuery = true)
    Long findAll1(String classesId);
}
